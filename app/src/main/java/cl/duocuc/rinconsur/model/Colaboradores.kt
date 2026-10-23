package cl.duocuc.rinconsur.model

enum class PerfilUsuario(val etiqueta: String) {
    SUPERVISOR("Supervisor"),
    OBSERVADOR("Observador")
}

// Usuario de la app (el caso los llama colaboradores). Vive en memoria mientras la app esta abierta.
// El correo no se repite. La clave se guarda tal cual
data class Colaborador(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val perfil: PerfilUsuario,
    // Foto de perfil como BYTES (lo que se guarda en la columna BLOB). null = sin foto todavia.
    // Va al final y con valor por defecto: el resto del proyecto no cambia.
    val avatar: ByteArray? = null
)

// Usuario que inicio sesion (su foto de perfil viaja dentro de colaborador.avatar).
data class UsuarioActual(val colaborador: Colaborador)
