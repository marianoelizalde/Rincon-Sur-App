package cl.duocuc.rinconsur.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SesionApi {

    @GET("api/sesiones")
    suspend fun listar(
        @Query("fecha") fecha: String? = null,
        @Query("estado") estado: String? = null,
        @Query("observadorId") observadorId: Long? = null,
        @Query("accesoId") accesoId: Long? = null
    ): List<SesionResponse>

    @GET("api/sesiones/{id}")
    suspend fun obtener(@Path("id") id: Long, @Query("conteo") conteo: Boolean = true): SesionResponse

    @POST("api/sesiones")
    suspend fun programar(@Body datos: SesionRequest): SesionResponse

    @PATCH("api/sesiones/{id}/iniciar")
    suspend fun iniciar(@Path("id") id: Long): SesionResponse

    @PATCH("api/sesiones/{id}/finalizar")
    suspend fun finalizar(@Path("id") id: Long): SesionResponse

    @PATCH("api/sesiones/{id}/cancelar")
    suspend fun cancelar(@Path("id") id: Long): SesionResponse

    @PATCH("api/sesiones/{id}/validar")
    suspend fun validar(@Path("id") id: Long, @Query("idValidador") idValidador: Long): SesionResponse

    @PATCH("api/sesiones/{id}/rechazar")
    suspend fun rechazar(@Path("id") id: Long, @Body datos: RechazoRequest): SesionResponse
}
