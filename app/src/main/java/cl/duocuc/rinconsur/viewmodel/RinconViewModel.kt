package cl.duocuc.rinconsur.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import cl.duocuc.rinconsur.data.ConvertidorImagen
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



    private val _observadores = MutableStateFlow<List<Colaborador>>(emptyList())
    val observadores: StateFlow<List<Colaborador>> = _observadores.asStateFlow()



    private val _nombres = MutableStateFlow<Map<Int, String>>(emptyMap())
    val nombres: StateFlow<Map<Int, String>> = _nombres.asStateFlow()



    private val _sesionAbierta = MutableStateFlow<SesionObservacion?>(null)
    val sesionAbierta: StateFlow<SesionObservacion?> = _sesionAbierta.asStateFlow()

    private val _registros = MutableStateFlow<List<RegistroObservacion>>(emptyList())
    val registros: StateFlow<List<RegistroObservacion>> = _registros.asStateFlow()



    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private var idSesionAbierta = 0

    init {
        recargar()
    }



    private fun recargar() {
        val id = sesion.leerSesion()
        _usuarioActual.value = if (id == -1) {
            null
        } else {
            db.buscarColaboradorPorId(id)?.let { UsuarioActual(it) }
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



    fun iniciarSesion(correo: String, clave: String): Boolean {
        val colaborador = db.buscarColaboradorPorCorreo(correo.lowercase())
        if (colaborador == null || colaborador.clave != clave) return false
        sesion.guardarSesion(colaborador.id)
        recargar()
        return true
    }



    fun registrarUsuario(colaborador: Colaborador): Boolean {
        if (usuarioActual.value?.colaborador?.perfil != PerfilUsuario.SUPERVISOR) return false
        if (db.buscarColaboradorPorCorreo(colaborador.correo) != null) return false
        db.insertarColaborador(colaborador)               // INSERT en SQLite
        _mensaje.value = "Usuario creado: ${colaborador.nombre}"
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



    fun cambiarAvatarDesdeCamara(foto: Bitmap) {
        guardarAvatar(ConvertidorImagen.aBytes(foto))
    }



    fun cambiarAvatarDesdeGaleria(uri: Uri) {
        val imagen = ConvertidorImagen.desdeUri(getApplication(), uri) ?: return
        guardarAvatar(ConvertidorImagen.aBytes(imagen))
    }



    private fun guardarAvatar(bytes: ByteArray) {
        val usuario = _usuarioActual.value ?: return
        db.actualizarAvatar(usuario.colaborador.id, bytes)
        recargar()
    }


    fun recuperarCuenta(correo: String, nombre: String, nuevaClave: String): Boolean {
        val colaborador = db.buscarColaboradorPorCorreo(correo.trim().lowercase()) ?:
        return false
        if (!colaborador.nombre.equals(nombre.trim(), ignoreCase = true)) return false
        db.actualizarClave(colaborador.id, nuevaClave) // UPDATE en SQLite
        return true
    }
    fun actualizarNombre(nombre: String): String? {
        val usuario = _usuarioActual.value ?: return "No hay sesion iniciada"
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return "Ingresa tu nombre"
        if (limpio.length < 3) return "Minimo 3 caracteres"
        if (limpio.any { !it.isLetter() && it != ' ' }) return "Solo letras y espacios"
        db.actualizarNombre(usuario.colaborador.id, limpio) // UPDATE en SQLite
        recargar() // vuelve a leer al usuario
        return null
    }




    fun programarSesion(nueva: SesionObservacion): Boolean {
        if (db.haySolapamiento(nueva.idAcceso, nueva.fecha, nueva.horaInicio, nueva.horaFin)) return false
        db.insertarSesion(nueva)                          // INSERT en SQLite
        recargar()
        return true
    }




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




    private fun guardar(nueva: SesionObservacion) {
        db.actualizarSesion(nueva)
        recargar()
    }




    fun registrarPersona(registro: RegistroObservacion) {
        if (sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        db.insertarRegistro(registro)                     // INSERT en SQLite
        recargar()
    }




    fun anularRegistro(registro: RegistroObservacion) {
        if (sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        db.anularRegistro(registro.id)
        recargar()
    }



    fun deshacerUltimo() {
        val ultimo = Estadisticas.registrosValidos(registros.value).lastOrNull() ?: return
        anularRegistro(ultimo)
    }

    override fun onCleared() {
        db.close()
    }
}
