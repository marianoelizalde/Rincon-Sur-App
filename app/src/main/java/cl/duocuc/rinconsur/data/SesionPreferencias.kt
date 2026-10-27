package cl.duocuc.rinconsur.data

import android.content.Context
import androidx.core.content.edit


class SesionPreferencias(context: Context) {


    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    private companion object {
        const val CLAVE_ID_USUARIO = "id_usuario"
        const val CLAVE_TEMA = "modo_tema"
        const val SIN_SESION = -1
    }


    fun guardarSesion(idUsuario: Int) {
        prefs.edit { putInt(CLAVE_ID_USUARIO, idUsuario) }
    }


    fun leerSesion(): Int = prefs.getInt(CLAVE_ID_USUARIO, SIN_SESION)


    fun borrarSesion() {
        prefs.edit { remove(CLAVE_ID_USUARIO) }
    }



    fun guardarTema(nombre: String) {
        prefs.edit { putString(CLAVE_TEMA, nombre) }
    }

    fun leerTema(): String? = prefs.getString(CLAVE_TEMA, null)
}
