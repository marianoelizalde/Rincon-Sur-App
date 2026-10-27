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
        val errorClave = errorClave(estado.clave)
        val errorConfirmar = errorConfirmar(estado.clave, estado.confirmar)
        val errorTerminos = if (!estado.aceptaTerminos) "Debes aceptar para crear la cuenta" else null

        val hayErrores = listOf(errorNombre, errorCorreo, errorClave, errorConfirmar, errorTerminos)
            .any { it != null }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar,
                errorTerminos = errorTerminos
            )
        }

        return if (hayErrores) null else Colaborador(
            nombre = estado.nombre.trim(),
            correo = estado.correo.trim().lowercase(),
            clave = estado.clave,
            perfil = estado.perfil
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
        valor.none { it.isLetter() } || valor.none { it.isDigit() } -> "Debe tener letras y numeros"
        else -> null
    }

    private fun errorConfirmar(clave: String, confirmar: String): String? = when {
        confirmar.isBlank() -> "Repite la contrasena"
        clave != confirmar -> "Las contrasenas no coinciden"
        else -> null
    }
}