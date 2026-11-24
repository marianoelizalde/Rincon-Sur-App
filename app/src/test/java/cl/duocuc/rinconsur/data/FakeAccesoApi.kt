package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.AccesoApi
import cl.duocuc.rinconsur.data.remote.AccesoResponse

class FakeAccesoApi : AccesoApi {
    override suspend fun listar(): List<AccesoResponse> = listOf(
        AccesoResponse(1L, "A1", "Acceso norte", null),
        AccesoResponse(2L, "A2", "Acceso sur", "Cerca de la escalera")
    )
}