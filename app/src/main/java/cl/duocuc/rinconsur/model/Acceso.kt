package cl.duocuc.rinconsur.model

data class Acceso(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val observacion: String? = null
)