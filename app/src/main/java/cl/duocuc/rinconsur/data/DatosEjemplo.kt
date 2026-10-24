package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.TipoCompania

// Datos de ejemplo escritos directamente en el codigo, basados en el Anexo 9 del caso.
object DatosEjemplo {
    val accesos = listOf(
        Acceso(1, "A1", "Principal", "Frente al pasillo central del mall"),
        Acceso(2, "A2", "Lateral", "Patio de comidas"),
        Acceso(3, "A3", "Estacionamiento", "Senal demasiado debil"),
        Acceso(4, "A4", "Conexion", "Hacia la tienda vecina")
    )

    // Contrasena de ambas cuentas: Rincon2026
    val colaboradores = listOf(
        Colaborador(3, "Mariano Elizalde", "mariano@rinconsur.cl", "Rincon2026", PerfilUsuario.SUPERVISOR),
        Colaborador(7, "Lukas LLanten", "lukas@rinconsur.cl", "Rincon2026", PerfilUsuario.OBSERVADOR)
    )

    val sesiones = listOf(
        SesionObservacion(1, 4, "2026-09-15", "10:00", "10:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(2, 1, "2026-09-15", "15:00", "15:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(3, 4, "2026-09-12", "11:00", "11:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(4, 2, "2026-09-12", "17:00", "17:30", 7, EstadoSesion.RECHAZADA, 3, "Muestra insuficiente"),
        SesionObservacion(5, 3, "2026-09-22", "13:00", "13:45", 7, EstadoSesion.FINALIZADA),
        SesionObservacion(6, 4, "2026-10-08", "15:00", "15:45", 7, EstadoSesion.PROGRAMADA)
    )

    // Cantidad de personas por sesion: transito, visita y no determinado (Anexo 9).
    private val cantidades = mapOf(
        1 to Triple(61, 52, 5),
        2 to Triple(38, 63, 3),
        3 to Triple(79, 58, 5),
        4 to Triple(4, 9, 1),
        5 to Triple(7, 27, 2)
    )

    fun registros(): List<RegistroObservacion> {
        val lista = mutableListOf<RegistroObservacion>()
        for (sesion in sesiones) {
            val (transito, visita, noDeterminado) = cantidades[sesion.id] ?: continue
            repeat(transito) { i ->
                // El destino de un transito es otro acceso distinto al de la sesion.
                val destino = accesos.map { it.id }.filter { it != sesion.idAcceso }[i % 3]
                lista += nuevo(sesion, Clasificacion.TRANSITO, i)
            }
            repeat(visita) { i -> lista += nuevo(sesion, Clasificacion.VISITA, i) }
        }
        // Cada registro necesita un id propio (antes lo asignaba la base de datos).
        return lista.mapIndexed { i, registro -> registro.copy(id = i + 1) }
    }

    private fun nuevo(
        sesion: SesionObservacion,
        clasificacion: Clasificacion,
        indice: Int
    ) = RegistroObservacion(
        idSesion = sesion.id,
        hora = sesion.horaInicio,
        clasificacion = clasificacion,
        rangoEtario = RangoEtario.entries[indice % RangoEtario.entries.size],
        tipoCompania = TipoCompania.entries[indice % TipoCompania.entries.size],
        compraOtraTienda = indice % 4 == 0,

    )
}