package cl.duocuc.rinconsur.model

data class  RegistroObservacion(
    val id: Int = 0,
    val idSesion: Int,
    val hora: String,
    val clasificacion: Clasificacion,
    val rangoEtario: RangoEtario? = null,
    val tipoCompania: TipoCompania?=null,
    val compraOtraTienda: Boolean = false,
    val anulado: Boolean = false
)