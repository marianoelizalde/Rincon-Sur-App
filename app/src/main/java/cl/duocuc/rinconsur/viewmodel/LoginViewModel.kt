package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null) }
    }

    fun onClaveCambia(valor: String) {
        _uiState.update { it.copy(clave = valor, errorClave = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(claveVisible = !it.claveVisible) }
    }

    fun onIngresar(): Boolean {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> Textos.obtener(R.string.msg_ingresa_tu_correo)
            !estado.correo.contains("@") || !estado.correo.contains(".") ->
                Textos.obtener(R.string.msg_correo_no_valido_ej_nombre_correo)
            else -> null
        }
        val errorClave = if (estado.clave.isBlank()) Textos.obtener(R.string.msg_ingresa_tu_contrasena) else null

        _uiState.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }

        return errorCorreo == null && errorClave == null
    }

    fun onCargando(valor: Boolean) {
        _uiState.update { it.copy(cargando = valor) }
    }

    fun onErrorGeneral(mensaje: String) {
        _uiState.update { it.copy(errorGeneral = mensaje) }
    }
}