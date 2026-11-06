package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.RecuperarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecuperarUiState())
    val uiState: StateFlow<RecuperarUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }
    }

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor, errorRut = null, errorGeneral = null) }
    }

    fun onClaveCambia(valor: String) {
        _uiState.update { it.copy(clave = valor, errorClave = null, errorGeneral = null) }
    }

    fun onConfirmarCambia(valor: String) {
        _uiState.update { it.copy(confirmar = valor, errorConfirmar = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(claveVisible = !it.claveVisible) }
    }

    // Recibe la funcion que verifica y cambia la clave (viene de RinconViewModel)
    fun onRecuperar(recuperarCuenta: (String, String, String) -> Boolean) {
        val estado = _uiState.value

        val errorCorreo = errorCorreo(estado.correo)
        val errorRut = errorRut(estado.rut)
        val errorClave = errorClave(estado.clave)
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> "Repite la contrasena"
            estado.clave != estado.confirmar -> "Las contrasenas no coinciden"
            else -> null
        }

        _uiState.update {
            it.copy(
                errorCorreo = errorCorreo,
                errorRut = errorRut,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar,
                errorGeneral = null
            )
        }

        val hayErrores = listOf(errorCorreo, errorRut, errorClave, errorConfirmar)
            .any { it != null }
        if (hayErrores) return

        val cambiada = recuperarCuenta(
            estado.correo.trim().lowercase(),
            normalizarRut(estado.rut),
            estado.clave
        )
        _uiState.update {
            if (cambiada) it.copy(listo = true)
            else it.copy(errorGeneral = "Los datos no coinciden con ninguna cuenta")
        }
    }

    private fun errorCorreo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !valor.contains("@") || !valor.contains(".") -> "Correo no valido (ej: nombre@correo.cl)"
        else -> null
    }

    private fun errorRut(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu RUT"
        !REGEX_RUT.matches(normalizarRut(valor)) -> "RUT no valido (ej: 12345678-5)"
        else -> null
    }


    private fun normalizarRut(valor: String): String {
        val limpio = valor.filter { it != '.' && it != '-' && !it.isWhitespace() }.uppercase()
        return if (limpio.length > 1) limpio.dropLast(1) + "-" + limpio.last() else limpio
    }


    private companion object {
        val REGEX_RUT = Regex("""^\d{7,8}-[\dK]$""")
    }
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