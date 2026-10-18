package cl.duocuc.rinconsur.data



import android.content.Context
import androidx.core.content.edit


class SesionPreferencias(context: Context) {

    // Abre (o crea) el archivo "sesion.xml". MODE_PRIVATE = solo esta app puede leerlo.
    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    private companion object {
        const val CLAVE_ID_USUARIO = "id_usuario"
        const val CLAVE_TEMA = "modo_tema"
        const val SIN_SESION = -1
    }

    // ---- Quien inicio sesion ----

    // edit { } abre el editor y al terminar llama a apply() (guarda en segundo plano).
    fun guardarSesion(idUsuario: Int) {
        prefs.edit { putInt(CLAVE_ID_USUARIO, idUsuario) }
    }

    // Si la clave no existe devuelve -1 (nadie inicio sesion).
    fun leerSesion(): Int = prefs.getInt(CLAVE_ID_USUARIO, SIN_SESION)

    // Borra la clave -> al reabrir la app no hay sesion.
    fun borrarSesion() {
        prefs.edit { remove(CLAVE_ID_USUARIO) }
    }

    // ---- Tema de la app ----

    fun guardarTema(nombre: String) {
        prefs.edit { putString(CLAVE_TEMA, nombre) }
    }

    fun leerTema(): String? = prefs.getString(CLAVE_TEMA, null)

    // ---- Foto de perfil (ruta del archivo) de cada usuario ----

    fun guardarFoto(idUsuario: Int, ruta: String) {
        prefs.edit { putString("foto_$idUsuario", ruta) }
    }

    fun leerFoto(idUsuario: Int): String? = prefs.getString("foto_$idUsuario", null)
}