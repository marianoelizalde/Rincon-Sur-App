package cl.duocuc.rinconsur.model

data class ProgramarUiState(
    val idAcceso:Int?=null,
    val idObservador: Int?=null,
    val fecha: String="",
    val horaInicio: String="",
    val horaFin: String="",
    val errorAcceso: String?=null,
    val errorObservador: String?=null,
    val errorFecha: String?=null,
    val errorInicio: String?=null,
    val errorFin:String?=null,
    val errorGeneral:String?=null
)


