

package cl.duocuc.rinconsur.model

data class EditarUsuarioUiState(
    val idUsuario: Int = 0,
    val nombre: String = "",
    val rut: String = "",
    val cargo: String = "",
    val telefono: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorCargo: String? = null,
    val errorTelefono: String? = null,
    val errorGeneral: String? = null
)