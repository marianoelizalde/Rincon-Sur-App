package cl.duocuc.rinconsur.data

import android.content.Context

object Textos {

    private var contexto: Context? = null

    fun iniciar(contexto: Context) {
        this.contexto = contexto.applicationContext
    }

    fun obtener(id: Int, vararg argumentos: Any): String {
        val actual = contexto ?: return ""
        return actual.getString(id, *argumentos)
    }
}
