package cl.duocuc.rinconsur.data.remote

import retrofit2.http.GET

interface AccesoApi {

    @GET("api/accesos")
    suspend fun listar(): List<AccesoResponse>
}
