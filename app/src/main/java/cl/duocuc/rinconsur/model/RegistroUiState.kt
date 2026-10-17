package cl.duocuc.rinconsur.model

data class RegistroUiState(
    val clasificacion: Clasificacion?=null,
    val rango: RangoEtario?=null,
    val compania: TipoCompania?=null,
    val compraOtraTienda: Boolean =false,
    val idDestino: Int? =null,
    val errorClasificacion: String?=null,
    val errorRango: String?=null,
    val errorCompania: String?=null,
)







