package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.ActualizarUsuarioRequest
import cl.duocuc.rinconsur.data.remote.LoginRequest
import cl.duocuc.rinconsur.data.remote.PerfilApi
import cl.duocuc.rinconsur.data.remote.PerfilResponse
import cl.duocuc.rinconsur.data.remote.RecuperarRequest
import cl.duocuc.rinconsur.data.remote.RegistroUsuarioRequest
import cl.duocuc.rinconsur.data.remote.RetrofitClient
import cl.duocuc.rinconsur.data.remote.UsuarioApi
import cl.duocuc.rinconsur.data.remote.UsuarioResponse
import cl.duocuc.rinconsur.data.remote.aColaborador
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.PerfilUsuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import cl.duocuc.rinconsur.R

class UsuarioRepository(
    private val usuarioApi: UsuarioApi = RetrofitClient.usuarioApi,
    private val perfilApi: PerfilApi = RetrofitClient.perfilApi
) {

    private var perfiles: List<PerfilResponse>? = null

    private suspend fun idDePerfil(perfil: PerfilUsuario): Long {
        val lista = perfiles ?: llamar { perfilApi.listar() }.also { perfiles = it }
        return lista.firstOrNull { it.nombre.equals(perfil.etiqueta, ignoreCase = true) }?.id
            ?: throw Exception(Textos.obtener(R.string.msg_no_existe_el_perfil, perfil.etiqueta))
    }

    private suspend fun conAvatar(respuesta: UsuarioResponse): Colaborador {
        val foto = if (respuesta.avatarUrl != null) descargarAvatar(respuesta.id) else null
        return respuesta.aColaborador(foto)
    }

    private suspend fun descargarAvatar(id: Long): ByteArray? = try {
        withContext(Dispatchers.IO) { usuarioApi.obtenerAvatar(id).bytes() }
    } catch (e: Exception) {
        null
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Colaborador =
        conAvatar(llamar { usuarioApi.iniciarSesion(LoginRequest(correo, contrasena)) })

    suspend fun obtener(id: Int): Colaborador =
        conAvatar(llamar { usuarioApi.obtener(id.toLong()) })

    suspend fun listar(): List<Colaborador> =
        llamar { usuarioApi.listar(null, null, null) }.map { it.aColaborador() }

    suspend fun registrar(usuario: Colaborador): Colaborador {
        val datos = RegistroUsuarioRequest(
            nombre = usuario.nombre,
            correo = usuario.correo,
            contrasena = usuario.clave,
            rut = usuario.rut,
            cargo = usuario.cargo,
            telefono = usuario.telefono,
            direccion = usuario.direccion,
            comuna = usuario.comuna,
            perfilId = idDePerfil(usuario.perfil)
        )
        return llamar { usuarioApi.registrar(datos) }.aColaborador()
    }

    suspend fun actualizar(usuario: Colaborador): Colaborador {
        val datos = ActualizarUsuarioRequest(
            nombre = usuario.nombre,
            rut = usuario.rut,
            cargo = usuario.cargo,
            telefono = usuario.telefono,
            direccion = usuario.direccion,
            comuna = usuario.comuna
        )
        return llamar { usuarioApi.actualizar(usuario.id.toLong(), datos) }.aColaborador()
    }

    suspend fun recuperar(correo: String, rut: String, nuevaContrasena: String) {
        llamar { usuarioApi.recuperar(RecuperarRequest(correo, rut, nuevaContrasena)) }
    }

    suspend fun cambiarActivo(id: Int, activo: Boolean, idActor: Int): Colaborador =
        llamar { usuarioApi.cambiarActivo(id.toLong(), activo, idActor.toLong()) }.aColaborador()

    suspend fun cambiarPerfil(id: Int, perfil: PerfilUsuario, idActor: Int): Colaborador {
        val perfilId = idDePerfil(perfil)
        return llamar { usuarioApi.cambiarPerfil(id.toLong(), perfilId, idActor.toLong()) }.aColaborador()
    }

    suspend fun subirAvatar(id: Int, bytes: ByteArray) {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
        val parte = MultipartBody.Part.createFormData("archivo", "avatar.jpg", cuerpo)
        llamar { usuarioApi.subirAvatar(id.toLong(), parte) }
    }
}
