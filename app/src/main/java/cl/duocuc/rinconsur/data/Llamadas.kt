package cl.duocuc.rinconsur.data

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import cl.duocuc.rinconsur.R

internal suspend fun <T> llamar(peticion: suspend () -> T): T = try {
    peticion()
} catch (e: HttpException) {
    throw Exception(mensajeDelServidor(e))
} catch (e: IOException) {
    throw Exception(Textos.obtener(R.string.msg_no_se_pudo_conectar_con_el))
}

private fun mensajeDelServidor(e: HttpException): String {
    val cuerpo = e.response()?.errorBody()?.string()
    val mensaje = try {
        JSONObject(cuerpo ?: "").optString("message")
    } catch (ex: Exception) {
        ""
    }
    return mensaje.ifBlank { Textos.obtener(R.string.msg_error_del_servidor, e.code()) }
}
