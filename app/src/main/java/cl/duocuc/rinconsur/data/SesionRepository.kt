package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.AccesoApi
import cl.duocuc.rinconsur.data.remote.EstadoApi
import cl.duocuc.rinconsur.data.remote.RechazoRequest
import cl.duocuc.rinconsur.data.remote.RetrofitClient
import cl.duocuc.rinconsur.data.remote.SesionApi
import cl.duocuc.rinconsur.data.remote.SesionRequest
import cl.duocuc.rinconsur.data.remote.aAcceso
import cl.duocuc.rinconsur.data.remote.aEstado
import cl.duocuc.rinconsur.data.remote.aSesion
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.EstadoBd
import cl.duocuc.rinconsur.model.SesionObservacion

class SesionRepository(
    private val sesionApi: SesionApi = RetrofitClient.sesionApi,
    private val accesoApi: AccesoApi = RetrofitClient.accesoApi,
    private val estadoApi: EstadoApi = RetrofitClient.estadoApi
) {

    suspend fun estados(): List<EstadoBd> =
        llamar { estadoApi.listar() }.map { it.aEstado() }

    suspend fun accesos(): List<Acceso> =
        llamar { accesoApi.listar() }.map { it.aAcceso() }

    suspend fun listar(): List<SesionObservacion> =
        llamar { sesionApi.listar(null, null, null, null) }
            .map { it.aSesion() }
            .sortedWith(compareByDescending<SesionObservacion> { it.fecha }.thenByDescending { it.horaInicio })

    suspend fun obtener(id: Int): SesionObservacion =
        llamar { sesionApi.obtener(id.toLong(), true) }.aSesion()

    suspend fun programar(nueva: SesionObservacion): SesionObservacion {
        val datos = SesionRequest(
            accesoId = nueva.idAcceso.toLong(),
            observadorId = nueva.idObservador.toLong(),
            fecha = nueva.fecha,
            horaInicio = nueva.horaInicio,
            horaFin = nueva.horaFin
        )
        return llamar { sesionApi.programar(datos) }.aSesion()
    }

    suspend fun iniciar(id: Int): SesionObservacion =
        llamar { sesionApi.iniciar(id.toLong()) }.aSesion()

    suspend fun finalizar(id: Int): SesionObservacion =
        llamar { sesionApi.finalizar(id.toLong()) }.aSesion()

    suspend fun cancelar(id: Int): SesionObservacion =
        llamar { sesionApi.cancelar(id.toLong()) }.aSesion()

    suspend fun validar(id: Int, idValidador: Int): SesionObservacion =
        llamar { sesionApi.validar(id.toLong(), idValidador.toLong()) }.aSesion()

    suspend fun rechazar(id: Int, idValidador: Int, motivo: String): SesionObservacion =
        llamar { sesionApi.rechazar(id.toLong(), RechazoRequest(idValidador.toLong(), motivo)) }.aSesion()
}
