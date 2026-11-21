package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.CrearUsuarioUiState
import cl.duocuc.rinconsur.model.PerfilUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

class CrearUsuarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CrearUsuarioUiState())
    val uiState: StateFlow<CrearUsuarioUiState> = _uiState.asStateFlow()

    fun onNombreCambia(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = if (valor.isEmpty()) null else errorNombre(valor)) }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim(), errorCorreo = null) }
    }

    fun onClaveCambia(valor: String) {
        _uiState.update { it.copy(clave = valor, errorClave = if (valor.isEmpty()) null else errorClave(valor)) }
    }
    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null) }
    }

    fun onCargoCambia(valor: String) {
        _uiState.update { it.copy(cargo = valor, errorCargo = null) }
    }

    fun onTelefonoCambia(valor: String) {
        _uiState.update { it.copy(telefono = valor.trim(), errorTelefono = null) }
    }

    fun onConfirmarCambia(valor: String) {
        _uiState.update {
            it.copy(confirmar = valor, errorConfirmar = if (valor.isEmpty()) null else errorConfirmar(it.clave, valor))
        }
    }

    fun onPerfilSeleccionado(perfil: PerfilUsuario) {
        _uiState.update { it.copy(perfil = perfil) }
    }

    fun onTerminosCambia(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor, errorTerminos = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(claveVisible = !it.claveVisible) }
    }

    fun onLimpiar() {
        _uiState.value = CrearUsuarioUiState()
    }

    fun onCrear(): Colaborador? {
        val estado = _uiState.value

        val errorNombre = errorNombre(estado.nombre)
        val errorCorreo = errorCorreo(estado.correo)
        val errorRut = errorRut(estado.rut)
        val errorCargo = errorCargo(estado.cargo)
        val errorTelefono = errorTelefono(estado.telefono)
        val errorClave = errorClave(estado.clave)
        val errorConfirmar = errorConfirmar(estado.clave, estado.confirmar)
        val errorTerminos = if (!estado.aceptaTerminos) Textos.obtener(R.string.msg_debes_aceptar_para_crear_la_cuenta) else null

        val hayErrores = listOf(errorNombre, errorCorreo, errorRut, errorCargo, errorTelefono, errorClave, errorConfirmar, errorTerminos)
            .any { it != null }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorRut = errorRut,
                errorCargo = errorCargo,
                errorTelefono = errorTelefono,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar,
                errorTerminos = errorTerminos
            )
        }

        return if (hayErrores) null else Colaborador(
            nombre = estado.nombre.trim(),
            correo = estado.correo.trim().lowercase(),
            clave = estado.clave,
            perfil = estado.perfil,
            telefono = estado.telefono,
            rut = normalizarRut(estado.rut),
            cargo = estado.cargo.trim()
        )

    }

    fun onCorreoRepetido(mensaje: String = Textos.obtener(R.string.msg_ese_correo_ya_tiene_una_cuenta)) {
        _uiState.update { it.copy(errorCorreo = mensaje) }
    }

    fun onRutRepetido(mensaje: String = Textos.obtener(R.string.msg_ese_rut_ya_pertenece_a_otro)) {
        _uiState.update { it.copy(errorRut = mensaje) }
    }

    private fun errorNombre(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_tu_nombre)
        valor.trim().length < 3 -> Textos.obtener(R.string.msg_minimo_3_caracteres)
        valor.any { !it.isLetter() && it != ' ' } -> Textos.obtener(R.string.msg_solo_letras_y_espacios)
        else -> null
    }

    private fun errorCorreo(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_tu_correo)
        !valor.contains("@") || !valor.contains(".") -> Textos.obtener(R.string.msg_correo_no_valido_ej_nombre_correo)
        else -> null
    }

    private fun errorClave(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_una_contrasena)
        valor.length < 8 -> Textos.obtener(R.string.msg_minimo_8_caracteres)
        valor.none { it.isUpperCase() } -> Textos.obtener(R.string.msg_debe_tener_una_mayuscula)
        valor.none { it.isLowerCase() } -> Textos.obtener(R.string.msg_debe_tener_una_minuscula)
        valor.none { it.isDigit() } -> Textos.obtener(R.string.msg_debe_tener_un_numero)
        valor.none { !it.isLetterOrDigit() } -> Textos.obtener(R.string.msg_debe_tener_un_caracter_especial_ej)
        else -> null
    }
    private fun errorRut(valor: String): String? {
        if (valor.isBlank()) return Textos.obtener(R.string.msg_ingresa_el_rut)
        val rut = normalizarRut(valor)
        val cuerpo = rut.substringBefore("-")
        val dv = rut.substringAfter("-", "")
        if (cuerpo.length !in 7..8 || cuerpo.any { !it.isDigit() }) return Textos.obtener(R.string.msg_rut_no_valido_ej_12345678_5)
        if (dv != digitoVerificador(cuerpo)) return Textos.obtener(R.string.msg_rut_no_valido_ej_12345678_5)
        return null
    }

    private fun normalizarRut(texto: String): String {
        val limpio = texto.filter { it.isLetterOrDigit() }.uppercase()
        return if (limpio.length < 2) limpio else limpio.dropLast(1) + "-" + limpio.last()
    }

    private fun digitoVerificador(cuerpo: String): String {
        var suma = 0
        var factor = 2
        for (digito in cuerpo.reversed()) {
            suma += digito.digitToInt() * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        return when (val resto = 11 - suma % 11) {
            11 -> "0"
            10 -> "K"
            else -> resto.toString()
        }
    }

    private fun errorCargo(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_el_cargo)
        valor.trim().length < 3 -> Textos.obtener(R.string.msg_minimo_3_caracteres)
        else -> null
    }

    private fun errorTelefono(valor: String): String? = when {
        valor.isEmpty() -> null
        valor.length != 9 || valor.any { !it.isDigit() } -> Textos.obtener(R.string.msg_el_telefono_debe_tener_9_digitos)
        else -> null
    }

    private fun errorConfirmar(clave: String, confirmar: String): String? = when {
        confirmar.isBlank() -> Textos.obtener(R.string.msg_repite_la_contrasena)
        clave != confirmar -> Textos.obtener(R.string.msg_las_contrasenas_no_coinciden)
        else -> null
    }
}