package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.PerfilApi
import cl.duocuc.rinconsur.data.remote.PerfilResponse

class FakePerfilApi(
    private val perfiles: List<PerfilResponse> = listOf(
        PerfilResponse(10L, "Supervisor"),
        PerfilResponse(20L, "Observador")
    )
) : PerfilApi {
    override suspend fun listar(): List<PerfilResponse> = perfiles
}