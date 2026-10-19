package cl.duocuc.rinconsur.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duocuc.rinconsur.data.ModoTema
import cl.duocuc.rinconsur.data.RinconDbHelper
import cl.duocuc.rinconsur.data.SesionPreferencias
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.Estadisticas
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.UsuarioActual
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class RinconViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RinconDbHelper(application)
    private val sesion = SesionPreferencias(application)

    // ---------------- Estado que leen las pantallas ----------------

    // Quien inicio sesion (null = nadie).
    private val _usuarioActual = MutableStateFlow<UsuarioActual?>(null)
    val usuarioActual: StateFlow<UsuarioActual?> = _usuarioActual.asStateFlow()

    private val _tema = MutableStateFlow(
        ModoTema.entries.firstOrNull { it.name == sesion.leerTema() } ?: ModoTema.SISTEMA
    )
    val tema: StateFlow<ModoTema> = _tema.asStateFlow()

    private val _sesiones = MutableStateFlow<List<SesionObservacion>>(emptyList())
    val sesiones: StateFlow<List<SesionObservacion>> = _sesiones.asStateFlow()

    private val _accesos = MutableStateFlow<List<Acceso>>(emptyList())
    val accesos: StateFlow<List<Acceso>> = _accesos.asStateFlow()

    // Solo los usuarios con perfil Observador se pueden asignar a una sesion.
    private val _observadores = MutableStateFlow<List<Colaborador>>(emptyList())
    val observadores: StateFlow<List<Colaborador>> = _observadores.asStateFlow()

    // id del colaborador -> nombre, para mostrar quien observa y quien valida.
    private val _nombres = MutableStateFlow<Map<Int, String>>(emptyMap())
    val nombres: StateFlow<Map<Int, String>> = _nombres.asStateFlow()

    // La sesion que se esta mirando en el detalle y sus registros.
    private val _sesionAbierta = MutableStateFlow<SesionObservacion?>(null)
    val sesionAbierta: StateFlow<SesionObservacion?> = _sesionAbierta.asStateFlow()

    private val _registros = MutableStateFlow<List<RegistroObservacion>>(emptyList())
    val registros: StateFlow<List<RegistroObservacion>> = _registros.asStateFlow()

    // Mensaje corto para mostrar en un Snackbar (null = nada que mostrar).
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var idSesionAbierta = 0

    init {
        recargar()      // al abrir la app: si quedo una sesion guardada, se recupera
    }

    // Vuelve a leer SQLite y SharedPreferences.
    private fun recargar() {
        val id = sesion.leerSesion()
        _usuarioActual.value = if (id == -1) {
            null
        } else {
            db.buscarColaboradorPorId(id)?.let { UsuarioActual(it, sesion.leerFoto(it.id)) }
        }
        _sesiones.value = db.listarSesiones()
        _accesos.value = db.listarAccesos()
        val colaboradores = db.listarColaboradores()
        _observadores.value = colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR }
        _nombres.value = colaboradores.associate { it.id to it.nombre }
        _sesionAbierta.value = db.buscarSesion(idSesionAbierta)
        _registros.value = db.listarRegistros(idSesionAbierta)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    // ---------------- Usuarios y sesion guardada ----------------

    // Login: devuelve true si el correo y la clave coinciden, y deja la sesion guardada.
    fun iniciarSesion(correo: String, clave: String): Boolean {
        val colaborador = db.buscarColaboradorPorCorreo(correo.lowercase())
        if (colaborador == null || colaborador.clave != clave) return false
        sesion.guardarSesion(colaborador.id)
        recargar()
        return true
    }

    // Crear usuario: devuelve false si el correo ya existe; si no, lo guarda e inicia sesion.
    fun registrarUsuario(colaborador: Colaborador): Boolean {
        if (db.buscarColaboradorPorCorreo(colaborador.correo) != null) return false
        val id = db.insertarColaborador(colaborador)      // INSERT en SQLite
        sesion.guardarSesion(id.toInt())                  // queda con sesion iniciada
        recargar()
        return true
    }

    fun cerrarSesion() {
        sesion.borrarSesion()
        recargar()
    }

    fun onTemaSeleccionado(modo: ModoTema) {
        sesion.guardarTema(modo.name)
        _tema.value = modo
    }

    fun onFotoGuardada(idColaborador: Int, ruta: String) {
        sesion.guardarFoto(idColaborador, ruta)
        recargar()
    }

    // ---------------- Sesiones de observacion ----------------

    // Programar: devuelve false si ya hay otra sesion en ese acceso y horario (RN-09).
    fun programarSesion(nueva: SesionObservacion): Boolean {
        if (db.haySolapamiento(nueva.idAcceso, nueva.fecha, nueva.horaInicio, nueva.horaFin)) return false
        db.insertarSesion(nueva)                          // INSERT en SQLite
        recargar()
        return true
    }

    // La pantalla de detalle (y la de registro) llama esto para elegir cual sesion mirar.
    fun abrirSesion(id: Int) {
        idSesionAbierta = id
        recargar()
    }

    fun iniciar() = cambiarEstado(EstadoSesion.PROGRAMADA, EstadoSesion.EN_CURSO)

    fun finalizar() = cambiarEstado(EstadoSesion.EN_CURSO, EstadoSesion.FINALIZADA)

    fun cancelar() = cambiarEstado(EstadoSesion.PROGRAMADA, EstadoSesion.CANCELADA)

    private fun cambiarEstado(desde: EstadoSesion, hacia: EstadoSesion) {
        val actual = sesionAbierta.value ?: return
        if (actual.estado != desde) return
        guardar(actual.copy(estado = hacia))
    }

    // Valida la sesion. Si tiene menos de 30 registros validos, se rechaza por muestra insuficiente.
    fun validar(idValidador: Int) {
        val actual = sesionAbierta.value ?: return
        if (actual.estado != EstadoSesion.FINALIZADA) return
        if (idValidador == actual.idObservador) {
            _mensaje.value = "El validador no puede ser quien observo la sesion"
            return
        }
        val validos = Estadisticas.registrosValidos(registros.value).size
        if (validos < Estadisticas.MINIMO_REGISTROS_VALIDOS) {
            guardar(
                actual.copy(
                    estado = EstadoSesion.RECHAZADA,
                    idValidador = idValidador,
                    motivoRechazo = "Muestra insuficiente"
                )
            )
            _mensaje.value = "Rechazada: solo hay $validos registros validos (minimo 30)"
        } else {
            guardar(actual.copy(estado = EstadoSesion.VALIDADA, idValidador = idValidador))
            _mensaje.value = "Sesion validada"
        }
    }

    // Devuelve el mensaje de error si el motivo no es valido, o null si se rechazo bien.
    fun rechazar(idValidador: Int, motivo: String): String? {
        val actual = sesionAbierta.value ?: return "No se encontro la sesion"
        if (motivo.isBlank()) return "Escribe el motivo del rechazo"
        if (motivo.trim().length < 5) return "Minimo 5 caracteres"
        if (idValidador == actual.idObservador) return "El validador no puede ser quien observo la sesion"
        guardar(
            actual.copy(
                estado = EstadoSesion.RECHAZADA,
                idValidador = idValidador,
                motivoRechazo = motivo.trim()
            )
        )
        return null
    }

    // UPDATE en SQLite y se vuelve a leer
    private fun guardar(nueva: SesionObservacion) {
        db.actualizarSesion(nueva)
        recargar()
    }

    // ---------------- Registros de personas ----------------

    // Solo se puede registrar mientras la sesion esta en curso.
    fun registrarPersona(registro: RegistroObservacion) {
        if (sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        db.insertarRegistro(registro)                     // INSERT en SQLite
        recargar()
    }

    // Los registros no se borran: se anulan (RN-11).
    fun anularRegistro(registro: RegistroObservacion) {
        if (sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        db.anularRegistro(registro.id)
        recargar()
    }

    // Anula el ultimo registro valido (boton "Deshacer" de la pantalla de registro).
    fun deshacerUltimo() {
        val ultimo = Estadisticas.registrosValidos(registros.value).lastOrNull() ?: return
        anularRegistro(ultimo)
    }

    override fun onCleared() {
        db.close()
    }
}