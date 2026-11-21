package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EditarUsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

class EditarUsuarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditarUsuarioUiState())
    val uiState: StateFlow<EditarUsuarioUiState> = _uiState.asStateFlow()

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

    suspend fun onGuardar(
        editarUsuario: suspend (Int, String, String, String, String) -> String?
    ): Boolean {
        val estado = _uiState.value

        val errorNombre = errorNombre(estado.nombre)
        val errorRut = errorRut(estado.rut)
        val errorCargo = errorCargo(estado.cargo)
        val errorTelefono = errorTelefono(estado.telefono)

        val rut = normalizarRut(estado.rut)

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorRut = errorRut,
                errorCargo = errorCargo,
                errorTelefono = errorTelefono,
                errorGeneral = null
            )
        }

        val hayErrores = listOf(errorNombre, errorRut, errorCargo, errorTelefono)
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
            _uiState.update {
                if (error.contains("RUT", ignoreCase = true)) it.copy(errorRut = error)
                else it.copy(errorGeneral = error)
            }
            return false
        }
        return true
    }

    private fun errorNombre(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_el_nombre)
        valor.trim().length < 3 -> Textos.obtener(R.string.msg_minimo_3_caracteres)
        valor.any { !it.isLetter() && it != ' ' } -> Textos.obtener(R.string.msg_solo_letras_y_espacios)
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
}