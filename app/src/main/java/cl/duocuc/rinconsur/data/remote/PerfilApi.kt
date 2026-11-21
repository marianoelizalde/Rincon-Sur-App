package cl.duocuc.rinconsur.data.remote

import retrofit2.http.GET

interface PerfilApi {

    @GET("api/perfiles")
    suspend fun listar(): List<PerfilResponse>
}
