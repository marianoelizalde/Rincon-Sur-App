package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.ActualizarUsuarioRequest
import cl.duocuc.rinconsur.data.remote.LoginRequest
import cl.duocuc.rinconsur.data.remote.PerfilApi
import cl.duocuc.rinconsur.data.remote.PerfilResponse
import cl.duocuc.rinconsur.data.remote.RecuperarRequest
import cl.duocuc.rinconsur.data.remote.RegistroUsuarioRequest
import cl.duocuc.rinconsur.data.remote.UsuarioApi
import cl.duocuc.rinconsur.data.remote.UsuarioResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException


class FakeUsuarioApi : UsuarioApi {

    var sinConexion = false
    var ultimoLogin: LoginRequest? = null
    var ultimoRegistro: RegistroUsuarioRequest? = null
    var ultimaRecuperacion: RecuperarRequest? = null
    var ultimaActualizacion: ActualizarUsuarioRequest? = null

    private val ana = respuesta(1L, "Ana Perez", "ana@duocuc.cl", 10L, "Supervisor", "/avatar/1")
    private val luis = respuesta(2L, "Luis Soto", "luis@duocuc.cl", 20L, "Observador", null)

    private fun respuesta(
        id: Long,
        nombre: String,
        correo: String,
        perfilId: Long,
        perfilNombre: String,
        avatarUrl: String?
    ) = UsuarioResponse(
        id = id,
        nombre = nombre,
        correo = correo,
        rut = "12345678-5",
        cargo = "Guardia",
        telefono = null,
        direccion = null,
        comuna = null,
        activo = true,
        perfilId = perfilId,
        perfilNombre = perfilNombre,
        avatarUrl = avatarUrl
    )

    override suspend fun listar(
        buscar: String?,
        perfilId: Long?,
        activo: Boolean?
    ): List<UsuarioResponse> {
        if (sinConexion) throw IOException("sin red")
        return listOf(ana, luis)
    }

    override suspend fun obtener(id: Long): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        return ana
    }

    override suspend fun registrar(datos: RegistroUsuarioRequest): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        ultimoRegistro = datos
        return luis
    }

    override suspend fun iniciarSesion(datos: LoginRequest): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        ultimoLogin = datos
        return ana
    }

    override suspend fun recuperar(datos: RecuperarRequest) {
        if (sinConexion) throw IOException("sin red")
        ultimaRecuperacion = datos
    }

    override suspend fun actualizar(id: Long, datos: ActualizarUsuarioRequest): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        ultimaActualizacion = datos
        return ana
    }

    override suspend fun cambiarActivo(id: Long, valor: Boolean, idActor: Long): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        return luis
    }

    override suspend fun cambiarPerfil(id: Long, perfilId: Long, idActor: Long): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        return luis
    }

    override suspend fun subirAvatar(id: Long, archivo: MultipartBody.Part): UsuarioResponse {
        if (sinConexion) throw IOException("sin red")
        return ana
    }

    override suspend fun obtenerAvatar(id: Long): ResponseBody {
        return ByteArray(3).toResponseBody()
    }
}