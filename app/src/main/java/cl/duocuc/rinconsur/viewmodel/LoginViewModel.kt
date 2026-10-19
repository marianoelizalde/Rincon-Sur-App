package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel simple, como FormularioViewModel del profesor: guarda el estado de la pantalla
// y valida los campos. NO toca la base de datos: eso lo hace RinconViewModel.
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())          // privado y mutable
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()     // publico, solo lectura

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null) }
    }

    fun onClaveCambia(valor: String) {
        _uiState.update { it.copy(clave = valor, errorClave = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(claveVisible = !it.claveVisible) }
    }

    // Boton "Ingresar": valida los campos. Devuelve true si estan bien escritos.
    // La pantalla despues le pregunta a RinconViewModel si el correo y la clave existen.
    fun onIngresar(): Boolean {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> "Ingresa tu correo"
            !estado.correo.contains("@") || !estado.correo.contains(".") ->
                "Correo no valido (ej: nombre@correo.cl)"
            else -> null
        }
        val errorClave = if (estado.clave.isBlank()) "Ingresa tu contrasena" else null

        _uiState.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }

        return errorCorreo == null && errorClave == null
    }

    // La pantalla lo llama cuando RinconViewModel dice que el correo o la clave no coinciden.
    fun onCredencialesIncorrectas() {
        _uiState.update { it.copy(errorGeneral = "Correo o contrasena incorrectos") }
    }
}