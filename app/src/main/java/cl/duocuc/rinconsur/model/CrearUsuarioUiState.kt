package cl.duocuc.rinconsur.model

data class CrearUsuarioUiState(
    val nombre: String="",
    val correo: String="",
    val clave : String="",
    val confirmar: String="",
    val perfil: PerfilUsuario = PerfilUsuario.OBSERVADOR,
    val aceptarTerminos: Boolean=false,
    val claveVisible: Boolean=false,
    val errorNombre: String? = null,
    val errorCorreo: String?=null,
    val errorClave: String?=null,
    val errorConfirmar: String?=null,
    val errorTerminos: String?=null,

)