package cl.duocuc.rinconsur.viewmodel


import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EditarUsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditarUsuarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditarUsuarioUiState())
    val uiState: StateFlow<EditarUsuarioUiState> = _uiState.asStateFlow()

    // Llena el formulario con los datos actuales del usuario (una sola vez por usuario)
    fun cargar(usuario: Colaborador) {
        if (_uiState.value.idUsuario == usuario.id) return
        _uiState.value = EditarUsuarioUiState(
            idUsuario = usuario.id,
            nombre = usuario.nombre,
            rut = usuario.rut,
            cargo = usuario.cargo,
            telefono = usuario.telefono
        )
    }

    fun onNombreCambia(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null, errorGeneral = null) }
    }

    fun onRutCambia(valor: String) {
        _uiState.update { it.copy(rut = valor.trim(), errorRut = null, errorGeneral = null) }
    }

    fun onCargoCambia(valor: String) {
        _uiState.update { it.copy(cargo = valor, errorCargo = null, errorGeneral = null) }
    }

    fun onTelefonoCambia(valor: String) {
        _uiState.update { it.copy(telefono = valor.trim(), errorTelefono = null, errorGeneral = null) }
    }


    fun onGuardar(
        rutEnUso: (String, Int) -> Boolean,
        editarUsuario: (Int, String, String, String, String) -> String?
    ): Boolean {
        val estado = _uiState.value

        val errorNombre = errorNombre(estado.nombre)
        val errorRut = errorRut(estado.rut)
        val errorCargo = errorCargo(estado.cargo)
        val errorTelefono = errorTelefono(estado.telefono)

        val rut = normalizarRut(estado.rut)
        // El RUT repetido solo se revisa si el formato ya es valido
        val rutRepetido = if (errorRut == null && rutEnUso(rut, estado.idUsuario)) {
            "Ese RUT ya pertenece a otro usuario"
        } else {
            errorRut
        }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorRut = rutRepetido,
                errorCargo = errorCargo,
                errorTelefono = errorTelefono,
                errorGeneral = null
            )
        }

        val hayErrores = listOf(errorNombre, rutRepetido, errorCargo, errorTelefono)
            .any { it != null }
        if (hayErrores) return false

        val error = editarUsuario(
            estado.idUsuario,
            estado.nombre.trim(),
            rut,
            estado.cargo.trim(),
            estado.telefono
        )
        if (error != null) {
            _uiState.update { it.copy(errorGeneral = error) }
            return false
        }
        return true
    }


    private fun errorNombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa el nombre"
        valor.trim().length < 3 -> "Minimo 3 caracteres"
        valor.any { !it.isLetter() && it != ' ' } -> "Solo letras y espacios"
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
        valor.isBlank() -> "Ingresa el cargo"
        valor.trim().length < 3 -> "Minimo 3 caracteres"
        else -> null
    }

    private fun errorTelefono(valor: String): String? = when {
        valor.isEmpty() -> null
        valor.length != 9 || valor.any { !it.isDigit() } -> "El telefono debe tener 9 digitos"
        else -> null
    }
}