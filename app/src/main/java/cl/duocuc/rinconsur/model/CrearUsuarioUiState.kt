package cl.duocuc.rinconsur.model

data class CrearUsuarioUiState(
    val nombre: String="",
    val correo: String="",
    val rut: String="",
    val cargo: String="",
    val telefono: String="",
    val clave : String="",
    val confirmar: String="",
    val perfil: PerfilUsuario = PerfilUsuario.OBSERVADOR,
    val aceptaTerminos: Boolean=false,
    val claveVisible: Boolean=false,
    val errorNombre: String? = null,
    val errorCorreo: String?=null,
    val errorRut: String? = null,
    val errorCargo: String? = null,
    val errorTelefono: String? = null,
    val errorClave: String?=null,
    val errorConfirmar: String?=null,
    val errorTerminos: String?=null,

    )