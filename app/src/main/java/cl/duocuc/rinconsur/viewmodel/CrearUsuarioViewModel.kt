package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.CrearUsuarioUiState
import cl.duocuc.rinconsur.model.PerfilUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
        val errorTerminos = if (!estado.aceptaTerminos) "Debes aceptar para crear la cuenta" else null


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


    fun onCorreoRepetido() {
        _uiState.update { it.copy(errorCorreo = "Ese correo ya tiene una cuenta") }
    }



    private fun errorNombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre"
        valor.trim().length < 3 -> "Minimo 3 caracteres"
        valor.any { !it.isLetter() && it != ' ' } -> "Solo letras y espacios"
        else -> null
    }

    private fun errorCorreo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !valor.contains("@") || !valor.contains(".") -> "Correo no valido (ej: nombre@correo.cl)"
        else -> null
    }

    private fun errorClave(valor: String): String? = when {
        valor.isBlank() -> "Ingresa una contrasena"
        valor.length < 8 -> "Minimo 8 caracteres"
        valor.none { it.isUpperCase() } -> "Debe tener una mayuscula"
        valor.none { it.isLowerCase() } -> "Debe tener una minuscula"
        valor.none { it.isDigit() } -> "Debe tener un numero"
        valor.none { !it.isLetterOrDigit() } -> "Debe tener un caracter especial (ej: ! # $ %)"
        else -> null
    }
    private fun errorRut(valor: String): String? {
        if (valor.isBlank()) return "Ingresa el RUT"
        val rut = normalizarRut(valor)
        val cuerpo = rut.substringBefore("-")
        val dv = rut.substringAfter("-", "")
        if (cuerpo.length !in 7..8 || cuerpo.any { !it.isDigit() }) return "RUT no valido (ej: 12345678-5)"
        if (dv != digitoVerificador(cuerpo)) return "RUT no valido (ej: 12345678-5)"
        return null
    }

    // "12.345.678-5" o "123456785" -> "12345678-5"
    private fun normalizarRut(texto: String): String {
        val limpio = texto.filter { it.isLetterOrDigit() }.uppercase()
        return if (limpio.length < 2) limpio else limpio.dropLast(1) + "-" + limpio.last()
    }

    // Modulo 11: multiplica de derecha a izquierda por 2,3,4,5,6,7 y repite
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
        valor.isBlank() -> "Ingresa el cargo"
        valor.trim().length < 3 -> "Minimo 3 caracteres"
        else -> null
    }

    private fun errorTelefono(valor: String): String? = when {
        valor.isEmpty() -> null
        valor.length != 9 || valor.any { !it.isDigit() } -> "El telefono debe tener 9 digitos"
        else -> null
    }

    private fun errorConfirmar(clave: String, confirmar: String): String? = when {
        confirmar.isBlank() -> "Repite la contrasena"
        clave != confirmar -> "Las contrasenas no coinciden"
        else -> null
    }
}