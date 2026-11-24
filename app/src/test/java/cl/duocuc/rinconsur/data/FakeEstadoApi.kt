package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.EstadoApi
import cl.duocuc.rinconsur.data.remote.EstadoSesionResponse

class FakeEstadoApi : EstadoApi {
    override suspend fun listar(): List<EstadoSesionResponse> = listOf(
        EstadoSesionResponse(1L, "PROGRAMADA", "Programada"),
        EstadoSesionResponse(2L, "EN_CURSO", "En curso")
    )
}