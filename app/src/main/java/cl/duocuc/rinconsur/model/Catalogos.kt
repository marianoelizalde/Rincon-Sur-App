package cl.duocuc.rinconsur.model

enum class Clasificacion(val etiqueta : String){
    TRANSITO("Transito"),
    VISITA("Visita"),
    NO_DETERMINADO("No determinado")
}



enum class RangoEtario(val etiqueta: String){
    MENOR_18("Menor de 18"),
    DE_18_A_25("18 a 25"),
    DE_26_A_40("26 a 40"),
    DE_41_A_60("41 a 60"),
    DE_61_O_MAS("61 o mas")
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


