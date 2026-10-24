package cl.duocuc.rinconsur.model

enum class Clasificacion(val etiqueta : String){
    TRANSITO("Transito"),
    VISITA("Visita"),
}



enum class RangoEtario(val etiqueta: String){
    MENOR_18("Nino"),
    DE_18_A_25("Joven"),
    DE_26_A_40("Adulto"),
    DE_41_A_60("Maduro"),
    DE_61_O_MAS("Anciano")
}


enum class TipoCompania(val etiqueta: String){
    SOLO("Solo"),
    PAREJA("Pareja"),
    CON_NINOS("Con ninos"),
    GRUPO("Grupo")
}



enum class EstadoSesion(val etiqueta: String){
    PROGRAMADA("Programada"),
    EN_CURSO("En curso"),
    FINALIZADA("Finalizada"),
    VALIDADA("Validada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada")
}


