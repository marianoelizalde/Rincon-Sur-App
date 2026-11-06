package cl.duocuc.rinconsur.model

data class RecuperarUiState(
    val correo: String = "",
    val rut: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val claveVisible: Boolean = false,
    val errorCorreo: String? = null,
    val errorRut: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val errorGeneral: String? = null,
    val listo: Boolean = false
)