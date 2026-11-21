package cl.duocuc.rinconsur.data.remote

import retrofit2.http.GET

interface EstadoApi {

    @GET("api/estados-sesion")
    suspend fun listar(): List<EstadoSesionResponse>
}
