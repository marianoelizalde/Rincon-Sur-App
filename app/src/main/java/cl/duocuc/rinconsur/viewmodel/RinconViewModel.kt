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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    private val _usuarios = MutableStateFlow<List<Colaborador>>(emptyList())
    val usuarios: StateFlow<List<Colaborador>> = _usuarios.asStateFlow()



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
            db.buscarColaboradorPorId(id)?.takeIf { it.activo }?.let { UsuarioActual(it) }
        }
        _sesiones.value = db.listarSesiones()
        _accesos.value = db.listarAccesos()
        val colaboradores = db.listarColaboradores()
        _usuarios.value = colaboradores
        _observadores.value = colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR && it.activo }
        _nombres.value = colaboradores.associate { it.id to it.nombre }
        _sesionAbierta.value = db.buscarSesion(idSesionAbierta)
        _registros.value = db.listarRegistros(idSesionAbierta)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }



    fun iniciarSesion(correo: String, clave: String): Boolean {
        val colaborador = db.buscarColaboradorPorCorreo(correo.lowercase())
        if (colaborador == null || !colaborador.activo || colaborador.clave != clave) return false
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


    private fun errorDeGestion(id: Int): String? {
        val actual = _usuarioActual.value?.colaborador ?: return "No hay sesion iniciada"
        return when {
            actual.perfil != PerfilUsuario.SUPERVISOR -> "Solo un supervisor puede gestionar usuarios"
            actual.id == id -> "No puedes cambiar tu propia cuenta"
            else -> null
        }
    }


    fun cambiarActivo(id: Int): String? {
        errorDeGestion(id)?.let { return it }
        val objetivo = db.buscarColaboradorPorId(id) ?: return "El usuario no existe"
        db.actualizarActivo(id, !objetivo.activo)         // UPDATE en SQLite
        _mensaje.value = if (objetivo.activo) {
            "Usuario desactivado: ${objetivo.nombre}"
        } else {
            "Usuario activado: ${objetivo.nombre}"
        }
        recargar()
        return null
    }

    fun cambiarRol(id: Int): String? {
        errorDeGestion(id)?.let { return it }
        val objetivo = db.buscarColaboradorPorId(id) ?: return "El usuario no existe"
        val nuevo = if (objetivo.perfil == PerfilUsuario.SUPERVISOR) {
            PerfilUsuario.OBSERVADOR
        } else {
            PerfilUsuario.SUPERVISOR
        }
        db.actualizarPerfil(id, nuevo)                    // UPDATE en SQLite
        _mensaje.value = "${objetivo.nombre} ahora es ${nuevo.etiqueta}"
        recargar()
        return null
    }

    fun cerrarSesion() {
        sesion.borrarSesion()
        recargar()
    }


    fun rutEnUso(rut: String, idExcluido: Int = 0): Boolean {
        val otro = db.buscarColaboradorPorRut(rut) ?: return false
        return otro.id != idExcluido
    }


    fun editarUsuario(id: Int, nombre: String, rut: String, cargo: String, telefono: String): String? {
        val actual = _usuarioActual.value?.colaborador ?: return "No hay sesion iniciada"
        if (actual.perfil != PerfilUsuario.SUPERVISOR) return "Solo un supervisor puede gestionar usuarios"
        if (db.buscarColaboradorPorId(id) == null) return "El usuario no existe"
        if (rutEnUso(rut, id)) return "Ese RUT ya pertenece a otro usuario"
        db.actualizarDatos(id, nombre, rut, cargo, telefono)   // UPDATE en SQLite
        _mensaje.value = "Datos actualizados: $nombre"
        recargar()
        return null
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


    fun recuperarCuenta(correo: String, rut: String, nuevaClave: String): Boolean {
        val colaborador = db.buscarColaboradorPorCorreo(correo.trim().lowercase()) ?: return false
        if (!colaborador.activo || colaborador.rut.isBlank() || colaborador.rut != rut) return false
        db.actualizarClave(colaborador.id, nuevaClave)
        return true
    }
    fun actualizarNombre(nombre: String): String? {
        val usuario = _usuarioActual.value ?: return "No hay sesion iniciada"
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return "Ingresa tu nombre"
        if (limpio.length < 3) return "Minimo 3 caracteres"
        if (limpio.any { !it.isLetter() && it != ' ' }) return "Solo letras y espacios"
        db.actualizarNombre(usuario.colaborador.id, limpio)
        recargar() // vuelve a leer al usuario
        return null
    }
    fun actualizarTelefono(telefono: String): String? {
        val usuario = _usuarioActual.value ?: return "No hay sesion iniciada"
        val limpio = telefono.trim()
        if (limpio.isNotEmpty() && (limpio.length != 9 || limpio.any { !it.isDigit() })) {
            return "El teléfono debe tener 9 dígitos"
        }
        db.actualizarTelefono(usuario.colaborador.id, limpio)
        recargar()
        return null
    }

    fun actualizarDireccion(direccion: String): String? {
        val usuario = _usuarioActual.value ?: return "No hay sesion iniciada"
        val limpio = direccion.trim()
        if (limpio.isNotEmpty() && limpio.length < 5) return "Mínimo 5 caracteres"
        if (limpio.length > 80) return "Máximo 80 caracteres"
        db.actualizarDireccion(usuario.colaborador.id, limpio)
        recargar()
        return null
    }

    fun actualizarComuna(comuna: String): String? {
        val usuario = _usuarioActual.value ?: return "No hay sesion iniciada"
        val limpio = comuna.trim()
        if (limpio.isNotEmpty() && limpio.length < 3) return "Mínimo 3 caracteres"
        if (limpio.any { !it.isLetter() && it != ' ' }) return "Solo letras y espacios"
        db.actualizarComuna(usuario.colaborador.id, limpio)
        recargar()
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

    fun iniciar() {
        val sesion = sesionAbierta.value ?: return
        val ahora = Date()
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(ahora)
        val hora = SimpleDateFormat("HH:mm", Locale.US).format(ahora)

        val error = when {
            hoy != sesion.fecha -> "Esta sesion es para el ${sesion.fecha}"
            hora < sesion.horaInicio -> "Aun no es la hora: parte a las ${sesion.horaInicio}"
            hora >= sesion.horaFin -> "La sesion ya termino a las ${sesion.horaFin}"
            else -> null
        }
        if (error != null) {
            _mensaje.value = error
            return
        }
        cambiarEstado(EstadoSesion.PROGRAMADA, EstadoSesion.EN_CURSO)
    }

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
