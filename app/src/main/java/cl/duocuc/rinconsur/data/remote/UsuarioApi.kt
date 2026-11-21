package cl.duocuc.rinconsur.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {

    @GET("api/usuarios")
    suspend fun listar(
        @Query("buscar") buscar: String? = null,
        @Query("perfilId") perfilId: Long? = null,
        @Query("activo") activo: Boolean? = null
    ): List<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioResponse

    @POST("api/usuarios")
    suspend fun registrar(@Body datos: RegistroUsuarioRequest): UsuarioResponse

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(@Body datos: LoginRequest): UsuarioResponse

    @POST("api/usuarios/recuperar")
    suspend fun recuperar(@Body datos: RecuperarRequest)

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: ActualizarUsuarioRequest): UsuarioResponse

    @PATCH("api/usuarios/{id}/activo")
    suspend fun cambiarActivo(
        @Path("id") id: Long,
        @Query("valor") valor: Boolean,
        @Header("X-Usuario-Id") idActor: Long
    ): UsuarioResponse

    @PATCH("api/usuarios/{id}/perfil")
    suspend fun cambiarPerfil(
        @Path("id") id: Long,
        @Query("perfilId") perfilId: Long,
        @Header("X-Usuario-Id") idActor: Long
    ): UsuarioResponse

    @Multipart
    @POST("api/usuarios/{id}/avatar")
    suspend fun subirAvatar(@Path("id") id: Long, @Part archivo: MultipartBody.Part): UsuarioResponse

    @GET("api/usuarios/{id}/avatar")
    suspend fun obtenerAvatar(@Path("id") id: Long): ResponseBody
}
