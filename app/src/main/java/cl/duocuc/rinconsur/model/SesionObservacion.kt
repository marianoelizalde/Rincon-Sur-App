package cl.duocuc.rinconsur.model

data class  SesionObservacion(
    val id:Int,
    val idAcceso: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val idObservador: Int,
    val estado: EstadoSesion = EstadoSesion.PROGRAMADA,
    val idValidador: Int? = null,
    val motivoRechazo: String? = null
)

