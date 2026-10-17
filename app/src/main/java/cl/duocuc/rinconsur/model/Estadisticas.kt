package cl.duocuc.rinconsur.model

object Estadisticas{
    const val MINIMO_REGISTROS_VALIDOS = 20


    fun registrosValidos(registros: List<RegistroObservacion>): List<RegistroObservacion> =
        registros.filter { !it.anulado }




    fun porcentajeTransito(registro : List<RegistroObservacion>): Double?{
        val validos = registrosValidos(registro)
        val determinados = validos.count {it.clasificacion != Clasificacion.NO_DETERMINADO }
        if (determinados == 0 )return  null
        val transito = validos.count{it.clasificacion == Clasificacion.TRANSITO }
        return  transito * 100.0/ determinados
    }
}