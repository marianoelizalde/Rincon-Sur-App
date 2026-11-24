package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.RegistroApi
import cl.duocuc.rinconsur.data.remote.RegistroResponse
import cl.duocuc.rinconsur.data.remote.NuevoRegistroRequest
import java.io.IOException
class FakeRegistroApi : RegistroApi{
    var sinConexion = false
    var nuevo: NuevoRegistroRequest? = null
    var actor: Long? = null
    private fun respuesta(id: Long, sesionId: Long, anulado: Boolean = false) = RegistroResponse(
        id = id,
        sesionId = sesionId,
        hora = "10:15",
        clasificacion = "VISITA",
        rangoEtario = "DE_26_A_40",
        tipoCompania = "PAREJA",
        compraOtraTienda = true,
        anulado = anulado,
        sexo = "FEMENINO"
    )
    override suspend fun listarPorSesion(sesionId: Long): List<RegistroResponse> {
        if (sinConexion) throw IOException("sin red")
        return listOf(respuesta(1L, sesionId), respuesta(2L, sesionId, anulado = true))
    }

    override suspend fun registrar(datos: NuevoRegistroRequest, idActor: Long): RegistroResponse {
        if (sinConexion) throw IOException("sin red")
        nuevo = datos
        actor = idActor
        return respuesta(3L, datos.sesionId)
    }
    override suspend fun anular(id: Long, idActor: Long): RegistroResponse {
        if (sinConexion) throw IOException("sin red")
        actor = idActor
        return respuesta(id, 1L, anulado = true)
    }
    override suspend fun deshacerUltimo(sesionId: Long, idActor: Long): RegistroResponse {
        if (sinConexion) throw IOException("sin red")
        actor = idActor
        return respuesta(2L, sesionId, anulado = true)
    }
}