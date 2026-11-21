package cl.duocuc.rinconsur.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.rinconsur.data.ConvertidorImagen
import cl.duocuc.rinconsur.data.ModoTema
import cl.duocuc.rinconsur.data.RegistroRepository
import cl.duocuc.rinconsur.data.SesionPreferencias
import cl.duocuc.rinconsur.data.SesionRepository
import cl.duocuc.rinconsur.data.UsuarioRepository
import cl.duocuc.rinconsur.data.remote.UsuarioEnSesion
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.EstadoBd
import cl.duocuc.rinconsur.model.Estadisticas
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.UsuarioActual
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

class RinconViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = SesionPreferencias(application)

    private val usuariosRepo = UsuarioRepository()
    private val sesionesRepo = SesionRepository()
    private val registrosRepo = RegistroRepository()

    private val candadoPerfil = Mutex()

    private val _usuarioActual = MutableStateFlow<UsuarioActual?>(null)
    val usuarioActual: StateFlow<UsuarioActual?> = _usuarioActual.asStateFlow()

    private val _tema = MutableStateFlow(
        ModoTema.entries.firstOrNull { it.name == prefs.leerTema() } ?: ModoTema.SISTEMA
    )
    val tema: StateFlow<ModoTema> = _tema.asStateFlow()

    private val _sesiones = MutableStateFlow<List<SesionObservacion>>(emptyList())
    val sesiones: StateFlow<List<SesionObservacion>> = _sesiones.asStateFlow()

    private val _estados = MutableStateFlow<List<EstadoBd>>(emptyList())
    val estados: StateFlow<List<EstadoBd>> = _estados.asStateFlow()

    private val _accesos = MutableStateFlow<List<Acceso>>(emptyList())
    val accesos: StateFlow<List<Acceso>> = _accesos.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Colaborador>>(emptyList())
    val usuarios: StateFlow<List<Colaborador>> = _usuarios.asStateFlow()

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
        restaurarSesion()
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private fun avisar(error: Exception) {
        _mensaje.value = error.message ?: Textos.obtener(R.string.msg_ocurrio_un_error_inesperado)
    }

    private fun idActual(): Int? = _usuarioActual.value?.colaborador?.id

    private fun restaurarSesion() {
        val id = prefs.leerSesion()
        if (id == -1) return
        viewModelScope.launch {
            try {
                val colaborador = usuariosRepo.obtener(id)
                if (colaborador.activo) {
                    UsuarioEnSesion.id = colaborador.id.toLong()
                    _usuarioActual.value = UsuarioActual(colaborador)
                    cargarTodo()
                } else {
                    prefs.borrarSesion()
                }
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun cargarTodo() {
        try {
            _accesos.value = sesionesRepo.accesos()

            _estados.value = try { sesionesRepo.estados() } catch (e: Exception) { emptyList() }
            aplicarUsuarios(usuariosRepo.listar())
            _sesiones.value = sesionesRepo.listar()
        } catch (e: Exception) {
            avisar(e)
        }
    }

    private fun aplicarUsuarios(lista: List<Colaborador>) {
        _usuarios.value = lista
        _observadores.value = lista.filter { it.perfil == PerfilUsuario.OBSERVADOR && it.activo }
        _nombres.value = lista.associate { it.id to it.nombre }
    }

    private suspend fun refrescarUsuarios() {
        try {
            aplicarUsuarios(usuariosRepo.listar())
        } catch (e: Exception) {
            avisar(e)
        }
    }

    private suspend fun refrescarSesiones() {
        try {
            _sesiones.value = sesionesRepo.listar()
        } catch (e: Exception) {
            avisar(e)
        }
    }

    private suspend fun refrescarSesionAbierta() {
        val id = idSesionAbierta
        if (id == 0) return
        try {
            _sesionAbierta.value = sesionesRepo.obtener(id)
            _registros.value = registrosRepo.listar(id)
        } catch (e: Exception) {
            avisar(e)
        }
    }

    fun recargarSesiones() {
        viewModelScope.launch { refrescarSesiones() }
    }

    fun recargarUsuarios() {
        viewModelScope.launch { refrescarUsuarios() }
    }

    suspend fun iniciarSesion(correo: String, clave: String): String? {
        return try {
            val colaborador = usuariosRepo.iniciarSesion(correo.lowercase(), clave)
            prefs.guardarSesion(colaborador.id)
            UsuarioEnSesion.id = colaborador.id.toLong()
            _usuarioActual.value = UsuarioActual(colaborador)
            viewModelScope.launch { cargarTodo() }
            null
        } catch (e: Exception) {
            e.message ?: Textos.obtener(R.string.msg_no_se_pudo_iniciar_sesion)
        }
    }

    fun cerrarSesion() {
        prefs.borrarSesion()
        UsuarioEnSesion.id = null
        _usuarioActual.value = null
        _sesiones.value = emptyList()
        _usuarios.value = emptyList()
        _observadores.value = emptyList()
        _nombres.value = emptyMap()
        _sesionAbierta.value = null
        _registros.value = emptyList()
        idSesionAbierta = 0
    }

    fun onTemaSeleccionado(modo: ModoTema) {
        prefs.guardarTema(modo.name)
        _tema.value = modo
    }

    suspend fun registrarUsuario(colaborador: Colaborador): String? {
        if (_usuarioActual.value?.colaborador?.perfil != PerfilUsuario.SUPERVISOR) {
            return Textos.obtener(R.string.msg_solo_un_supervisor_puede_crear_usuarios)
        }
        return try {
            usuariosRepo.registrar(colaborador)
            _mensaje.value = Textos.obtener(R.string.msg_usuario_creado, colaborador.nombre)
            refrescarUsuarios()
            null
        } catch (e: Exception) {
            e.message ?: Textos.obtener(R.string.msg_no_se_pudo_crear_el_usuario)
        }
    }

    suspend fun recuperarCuenta(correo: String, rut: String, nuevaClave: String): String? {
        return try {
            usuariosRepo.recuperar(correo.trim().lowercase(), rut, nuevaClave)
            null
        } catch (e: Exception) {
            e.message ?: Textos.obtener(R.string.msg_no_se_pudo_cambiar_la_contrasena)
        }
    }

    private fun errorDeGestion(id: Int): String? {
        val actual = _usuarioActual.value?.colaborador ?: return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        return when {
            actual.perfil != PerfilUsuario.SUPERVISOR -> Textos.obtener(R.string.msg_solo_un_supervisor_puede_gestionar_usuarios)
            actual.id == id -> Textos.obtener(R.string.msg_no_puedes_cambiar_tu_propia_cuenta)
            else -> null
        }
    }

    fun cambiarActivo(id: Int): String? {
        errorDeGestion(id)?.let { return it }
        val objetivo = _usuarios.value.firstOrNull { it.id == id } ?: return Textos.obtener(R.string.msg_el_usuario_no_existe)
        val idActor = idActual() ?: return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        viewModelScope.launch {
            try {
                val nuevo = usuariosRepo.cambiarActivo(id, !objetivo.activo, idActor)
                _mensaje.value = if (nuevo.activo) {
                    Textos.obtener(R.string.msg_usuario_activado, nuevo.nombre)
                } else {
                    Textos.obtener(R.string.msg_usuario_desactivado, nuevo.nombre)
                }
                refrescarUsuarios()
            } catch (e: Exception) {
                avisar(e)
            }
        }
        return null
    }

    fun cambiarRol(id: Int): String? {
        errorDeGestion(id)?.let { return it }
        val objetivo = _usuarios.value.firstOrNull { it.id == id } ?: return Textos.obtener(R.string.msg_el_usuario_no_existe)
        val idActor = idActual() ?: return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        val nuevoPerfil = if (objetivo.perfil == PerfilUsuario.SUPERVISOR) {
            PerfilUsuario.OBSERVADOR
        } else {
            PerfilUsuario.SUPERVISOR
        }
        viewModelScope.launch {
            try {
                usuariosRepo.cambiarPerfil(id, nuevoPerfil, idActor)
                _mensaje.value = Textos.obtener(R.string.msg_ahora_es, objetivo.nombre, nuevoPerfil.etiqueta)
                refrescarUsuarios()
            } catch (e: Exception) {
                avisar(e)
            }
        }
        return null
    }

    suspend fun editarUsuario(id: Int, nombre: String, rut: String, cargo: String, telefono: String): String? {
        val actual = _usuarioActual.value?.colaborador ?: return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        if (actual.perfil != PerfilUsuario.SUPERVISOR) return Textos.obtener(R.string.msg_solo_un_supervisor_puede_gestionar_usuarios)
        val objetivo = _usuarios.value.firstOrNull { it.id == id } ?: return Textos.obtener(R.string.msg_el_usuario_no_existe)
        return try {
            val guardado = usuariosRepo.actualizar(
                objetivo.copy(nombre = nombre, rut = rut, cargo = cargo, telefono = telefono)
            )
            _mensaje.value = Textos.obtener(R.string.msg_datos_actualizados, guardado.nombre)
            if (guardado.id == actual.id) {
                _usuarioActual.value = UsuarioActual(guardado.copy(avatar = actual.avatar))
            }
            refrescarUsuarios()
            null
        } catch (e: Exception) {
            e.message ?: Textos.obtener(R.string.msg_no_se_pudieron_guardar_los_datos)
        }
    }

    fun cambiarAvatarDesdeCamara(foto: Bitmap) {
        subirAvatar(ConvertidorImagen.aBytes(foto))
    }

    fun cambiarAvatarDesdeGaleria(uri: Uri) {
        val imagen = ConvertidorImagen.desdeUri(getApplication(), uri) ?: return
        subirAvatar(ConvertidorImagen.aBytes(imagen))
    }

    private fun subirAvatar(bytes: ByteArray) {
        val usuario = _usuarioActual.value?.colaborador ?: return
        viewModelScope.launch {
            try {
                usuariosRepo.subirAvatar(usuario.id, bytes)
                val actual = _usuarioActual.value?.colaborador ?: return@launch
                _usuarioActual.value = UsuarioActual(actual.copy(avatar = bytes))
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }

    private fun guardarPerfil(cambio: (Colaborador) -> Colaborador) {
        viewModelScope.launch {
            candadoPerfil.withLock {
                val actual = _usuarioActual.value?.colaborador ?: return@withLock
                val nuevo = cambio(actual)
                if (nuevo == actual) return@withLock
                try {
                    val guardado = usuariosRepo.actualizar(nuevo)
                    _usuarioActual.value = UsuarioActual(guardado.copy(avatar = actual.avatar))
                } catch (e: Exception) {
                    avisar(e)
                }
            }
        }
    }

    fun actualizarNombre(nombre: String): String? {
        if (_usuarioActual.value == null) return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return Textos.obtener(R.string.msg_ingresa_tu_nombre)
        if (limpio.length < 3) return Textos.obtener(R.string.msg_minimo_3_caracteres)
        if (limpio.any { !it.isLetter() && it != ' ' }) return Textos.obtener(R.string.msg_solo_letras_y_espacios)
        guardarPerfil { it.copy(nombre = limpio) }
        return null
    }

    fun actualizarTelefono(telefono: String): String? {
        if (_usuarioActual.value == null) return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        val limpio = telefono.trim()
        if (limpio.isNotEmpty() && (limpio.length != 9 || limpio.any { !it.isDigit() })) {
            return Textos.obtener(R.string.msg_el_telefono_debe_tener_9_digitos)
        }
        guardarPerfil { it.copy(telefono = limpio) }
        return null
    }

    fun actualizarDireccion(direccion: String): String? {
        if (_usuarioActual.value == null) return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        val limpio = direccion.trim()
        if (limpio.isNotEmpty() && limpio.length < 5) return Textos.obtener(R.string.msg_minimo_5_caracteres)
        if (limpio.length > 80) return Textos.obtener(R.string.msg_maximo_80_caracteres)
        guardarPerfil { it.copy(direccion = limpio) }
        return null
    }

    fun actualizarComuna(comuna: String): String? {
        if (_usuarioActual.value == null) return Textos.obtener(R.string.msg_no_hay_sesion_iniciada)
        val limpio = comuna.trim()
        if (limpio.isNotEmpty() && limpio.length < 3) return Textos.obtener(R.string.msg_minimo_3_caracteres)
        if (limpio.any { !it.isLetter() && it != ' ' }) return Textos.obtener(R.string.msg_solo_letras_y_espacios)
        guardarPerfil { it.copy(comuna = limpio) }
        return null
    }

    suspend fun programarSesion(nueva: SesionObservacion): String? {
        return try {
            sesionesRepo.programar(nueva)
            refrescarSesiones()
            null
        } catch (e: Exception) {
            e.message ?: Textos.obtener(R.string.msg_no_se_pudo_programar_la_sesion)
        }
    }

    fun abrirSesion(id: Int) {
        idSesionAbierta = id
        viewModelScope.launch { refrescarSesionAbierta() }
    }

    private fun accionSobreSesion(peticion: suspend (Int) -> SesionObservacion) {
        val actual = _sesionAbierta.value ?: return
        viewModelScope.launch {
            try {
                _sesionAbierta.value = peticion(actual.id)
                refrescarSesiones()
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }

    fun iniciar() = accionSobreSesion { sesionesRepo.iniciar(it) }

    fun finalizar() = accionSobreSesion { sesionesRepo.finalizar(it) }

    fun cancelar() = accionSobreSesion { sesionesRepo.cancelar(it) }

    fun validar(idValidador: Int) {
        val actual = _sesionAbierta.value ?: return
        if (actual.estado != EstadoSesion.FINALIZADA) return
        if (idValidador == actual.idObservador) {
            _mensaje.value = Textos.obtener(R.string.msg_el_validador_no_puede_ser_quien)
            return
        }
        viewModelScope.launch {
            try {
                val nueva = sesionesRepo.validar(actual.id, idValidador)
                _sesionAbierta.value = nueva
                refrescarSesiones()
                val validos = Estadisticas.registrosValidos(_registros.value).size
                _mensaje.value = if (nueva.estado == EstadoSesion.VALIDADA) {
                    Textos.obtener(R.string.msg_sesion_validada)
                } else {
                    Textos.obtener(R.string.msg_rechazada_solo_hay_registros_validos_minimo, validos, Estadisticas.MINIMO_REGISTROS_VALIDOS)
                }
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }

    fun rechazar(idValidador: Int, motivo: String): String? {
        val actual = _sesionAbierta.value ?: return Textos.obtener(R.string.msg_no_se_encontro_la_sesion)
        if (motivo.isBlank()) return Textos.obtener(R.string.msg_escribe_el_motivo_del_rechazo)
        if (motivo.trim().length < 5) return Textos.obtener(R.string.msg_minimo_5_caracteres)
        if (idValidador == actual.idObservador) return Textos.obtener(R.string.msg_el_validador_no_puede_ser_quien)
        viewModelScope.launch {
            try {
                _sesionAbierta.value = sesionesRepo.rechazar(actual.id, idValidador, motivo.trim())
                refrescarSesiones()
            } catch (e: Exception) {
                avisar(e)
            }
        }
        return null
    }

    fun registrarPersona(registro: RegistroObservacion) {
        if (_sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        val idActor = idActual() ?: return
        viewModelScope.launch {
            try {
                val nuevo = registrosRepo.registrar(registro, idActor)
                _registros.value = _registros.value + nuevo
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }

    fun anularRegistro(registro: RegistroObservacion) {
        if (_sesionAbierta.value?.estado != EstadoSesion.EN_CURSO) return
        val idActor = idActual() ?: return
        viewModelScope.launch {
            try {
                val anulado = registrosRepo.anular(registro.id, idActor)
                _registros.value = _registros.value.map { if (it.id == anulado.id) anulado else it }
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }

    fun deshacerUltimo() {
        val actual = _sesionAbierta.value ?: return
        if (actual.estado != EstadoSesion.EN_CURSO) return
        val idActor = idActual() ?: return
        viewModelScope.launch {
            try {
                registrosRepo.deshacerUltimo(actual.id, idActor)
                _registros.value = registrosRepo.listar(actual.id)
            } catch (e: Exception) {
                avisar(e)
            }
        }
    }
}
