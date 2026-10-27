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

    val avatar: ByteArray? = null
)


data class UsuarioActual(val colaborador: Colaborador)
