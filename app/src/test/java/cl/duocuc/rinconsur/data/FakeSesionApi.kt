package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.AccesoApi
import cl.duocuc.rinconsur.data.remote.AccesoResponse
import cl.duocuc.rinconsur.data.remote.EstadoApi
import cl.duocuc.rinconsur.data.remote.EstadoSesionResponse
import cl.duocuc.rinconsur.data.remote.RechazoRequest
import cl.duocuc.rinconsur.data.remote.SesionApi
import cl.duocuc.rinconsur.data.remote.SesionRequest
import cl.duocuc.rinconsur.data.remote.SesionResponse
import java.io.IOException

fun sesionRespuesta(
    id: Long,
    fecha: String = "2026-11-20",
    horaInicio: String = "10:00",
    estado: String = "PROGRAMADA"
    ) = SesionResponse(
        id = id,
        accesoId = 1L,
        accesoCodigo = "A1",
        accesoNombre = "Acceso norte",
        fecha = fecha,
        horaInicio = horaInicio,
        horaFin = "10:45",
        turno = "MANANA",
        observadorId = 2L,
        observador = "Luis Soto",
        estado = estado,
        estadoEtiqueta = estado,
        validadorId = null,
        motivoRechazo = null,
        registrosValidos = 0L
    )




class FakeSesionApi : SesionApi {

    var sinConexion = false
    var sesiones = listOf(
        sesionRespuesta(1L, "2026-11-20", "10:00"),
        sesionRespuesta(2L, "2026-11-22", "15:00"),
        sesionRespuesta(3L, "2026-11-22", "11:00")
    )
    var ultimaProgramacion: SesionRequest? = null
    var ultimoRechazo: RechazoRequest? = null
    var ultimoValidador: Long? = null


    override suspend fun listar(
        fecha: String?,
        estado: String?,
        observadorId: Long?,
        accesoId: Long?
    ): List<SesionResponse> {
        if (sinConexion) throw IOException("sin red")
        return sesiones
    }

    override suspend fun obtener(id: Long, conteo: Boolean): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        return sesionRespuesta(id)
    }

    override suspend fun programar(datos: SesionRequest): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        ultimaProgramacion = datos
        return sesionRespuesta(10L, datos.fecha, datos.horaInicio)
    }

    override suspend fun iniciar(id: Long): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        return sesionRespuesta(id, estado = "EN_CURSO")
    }

    override suspend fun finalizar(id: Long): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        return sesionRespuesta(id, estado = "FINALIZADA")
    }

    override suspend fun cancelar(id: Long): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        return sesionRespuesta(id, estado = "CANCELADA")
    }

    override suspend fun validar(id: Long, idValidador: Long): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        ultimoValidador = idValidador
        return sesionRespuesta(id, estado = "VALIDADA")
    }

    override suspend fun rechazar(id: Long, datos: RechazoRequest): SesionResponse {
        if (sinConexion) throw IOException("sin red")
        ultimoRechazo = datos
        return sesionRespuesta(id, estado = "RECHAZADA")
    }
}