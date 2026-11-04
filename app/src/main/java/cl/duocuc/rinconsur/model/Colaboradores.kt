package cl.duocuc.rinconsur.model

enum class PerfilUsuario(val etiqueta: String) {
    SUPERVISOR("Supervisor"),
    OBSERVADOR("Observador")
}


data class Colaborador(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: PerfilUsuario,

    val avatar: ByteArray? = null,
    val telefono: String = "",
    val rut: String = "",
    val cargo: String = "",
    val direccion: String = "",
    val comuna: String = ""
)



data class UsuarioActual(val colaborador: Colaborador)
