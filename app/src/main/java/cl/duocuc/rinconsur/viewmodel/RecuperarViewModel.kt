package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.RecuperarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

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

    suspend fun onRecuperar(recuperarCuenta: suspend (String, String, String) -> String?) {
        val estado = _uiState.value

        val errorCorreo = errorCorreo(estado.correo)
        val errorRut = errorRut(estado.rut)
        val errorClave = errorClave(estado.clave)
        val errorConfirmar = when {
            estado.confirmar.isBlank() -> Textos.obtener(R.string.msg_repite_la_contrasena)
            estado.clave != estado.confirmar -> Textos.obtener(R.string.msg_las_contrasenas_no_coinciden)
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

        val error = recuperarCuenta(
            estado.correo.trim().lowercase(),
            normalizarRut(estado.rut),
            estado.clave
        )
        _uiState.update {
            if (error == null) it.copy(listo = true)
            else it.copy(errorGeneral = error)
        }
    }

    private fun errorCorreo(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_tu_correo)
        !valor.contains("@") || !valor.contains(".") -> Textos.obtener(R.string.msg_correo_no_valido_ej_nombre_correo)
        else -> null
    }

    private fun errorRut(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_tu_rut)
        !REGEX_RUT.matches(normalizarRut(valor)) -> Textos.obtener(R.string.msg_rut_no_valido_ej_12345678_5)
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
    valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_una_contrasena)
    valor.length < 8 -> Textos.obtener(R.string.msg_minimo_8_caracteres)
    valor.none { it.isUpperCase() } -> Textos.obtener(R.string.msg_debe_tener_una_mayuscula)
    valor.none { it.isLowerCase() } -> Textos.obtener(R.string.msg_debe_tener_una_minuscula)
    valor.none { it.isDigit() } -> Textos.obtener(R.string.msg_debe_tener_un_numero)
    valor.none { !it.isLetterOrDigit() } -> Textos.obtener(R.string.msg_debe_tener_un_caracter_especial_ej)
    else -> null
}