package cl.duocuc.rinconsur.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RegistroApi {

    @GET("api/registros/sesion/{sesionId}")
    suspend fun listarPorSesion(@Path("sesionId") sesionId: Long): List<RegistroResponse>

    @POST("api/registros")
    suspend fun registrar(
        @Body datos: NuevoRegistroRequest,
        @Header("X-Usuario-Id") idActor: Long
    ): RegistroResponse

    @PATCH("api/registros/{id}/anular")
    suspend fun anular(
        @Path("id") id: Long,
        @Header("X-Usuario-Id") idActor: Long
    ): RegistroResponse

    @POST("api/registros/sesion/{sesionId}/deshacer-ultimo")
    suspend fun deshacerUltimo(
        @Path("sesionId") sesionId: Long,
        @Header("X-Usuario-Id") idActor: Long
    ): RegistroResponse
}
