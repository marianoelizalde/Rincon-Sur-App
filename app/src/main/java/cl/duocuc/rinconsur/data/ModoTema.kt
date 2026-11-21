package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.R

enum class ModoTema(private val recurso: Int) {
    SISTEMA(R.string.tema_sistema),
    CLARO(R.string.tema_claro),
    OSCURO(R.string.tema_oscuro);

    val etiqueta: String get() = Textos.obtener(recurso)
}
