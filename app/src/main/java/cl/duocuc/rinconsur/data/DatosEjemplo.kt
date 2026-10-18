package cl.duocuc.rinconsur.data



import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.SesionObservacion



object DatosEjemplo {
    val acceso = listOf(
        Acceso(1,"A1", "Principal","Frente al pasillo central del moll"),
        Acceso(2,"A2","Lateral","Patio de la comidas"),
        Acceso(3,"A3","Estacionamiento","Senal demasiado debil"),
        Acceso(4,"A4","Conexion","Hacia la tienda vecina")
    )

    val colaboradores = listOf(
        Colaborador(3,"Mariano Elizalde","mariano@rincorsur.cl","Rincor2026", PerfilUsuario.SUPERVISOR),
        Colaborador(7,"Lukas LLanten","lukas@rinconsur.cl","Rincon2026", PerfilUsuario.OBSERVADOR)

    )

    val sesiones = listOf(
        SesionObservacion(1, 4, "2026-09-15", "10:00", "10:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(2, 1, "2026-09-15", "15:00", "15:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(3, 4, "2026-09-12", "11:00", "11:45", 7, EstadoSesion.VALIDADA, 3),
        SesionObservacion(
            4, 2, "2026-09-12", "17:00", "17:30", 7,
            EstadoSesion.RECHAZADA, 3, "Muestra insuficiente"
        ),
        SesionObservacion(5, 3, "2026-09-22", "13:00", "13:45", 7, EstadoSesion.FINALIZADA),
        SesionObservacion(6, 4, "2026-10-08", "15:00", "15:45", 7, EstadoSesion.PROGRAMADA)
    )


}


