package cl.duocuc.rinconsur.navigation

sealed class Ruta(val ruta: String) {
    data object Login : Ruta("login")

    data object Recuperar : Ruta("recuperar")
    data object CrearUsuario : Ruta("crear_usuario")
    data object Sesiones : Ruta("sesiones")
    data object Perfil : Ruta("perfil")
    data object Programar : Ruta("programar")

    data object Sesion : Ruta("sesion/{id}") {
        fun crear(id: Int) = "sesion/$id"
    }

    data object Registro : Ruta("registro/{id}") {
        fun crear(id: Int) = "registro/$id"
    }
}
