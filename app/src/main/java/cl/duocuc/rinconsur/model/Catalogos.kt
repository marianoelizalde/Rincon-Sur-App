package cl.duocuc.rinconsur.model

import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

enum class Clasificacion(private val recurso: Int) {
    TRANSITO(R.string.clasificacion_transito),
    VISITA(R.string.clasificacion_visita);

    val etiqueta: String get() = Textos.obtener(recurso)
}

enum class RangoEtario(private val recurso: Int) {
    MENOR_18(R.string.rango_menor_18),
    DE_18_A_25(R.string.rango_18_25),
    DE_26_A_40(R.string.rango_26_40),
    DE_41_A_60(R.string.rango_41_60),
    DE_61_O_MAS(R.string.rango_61_mas);

    val etiqueta: String get() = Textos.obtener(recurso)
}

enum class TipoCompania(private val recurso: Int) {
    SOLO(R.string.compania_solo),
    PAREJA(R.string.compania_pareja),
    CON_NINOS(R.string.compania_con_ninos),
    GRUPO(R.string.compania_grupo);

    val etiqueta: String get() = Textos.obtener(recurso)
}

enum class EstadoSesion(private val recurso: Int) {
    PROGRAMADA(R.string.estado_programada),
    EN_CURSO(R.string.estado_en_curso),
    FINALIZADA(R.string.estado_finalizada),
    VALIDADA(R.string.estado_validada),
    RECHAZADA(R.string.estado_rechazada),
    CANCELADA(R.string.estado_cancelada);

    val etiqueta: String get() = Textos.obtener(recurso)
}

enum class Sexo(private val recurso: Int) {
    MASCULINO(R.string.sexo_masculino),
    FEMENINO(R.string.sexo_femenino);

    val etiqueta: String get() = Textos.obtener(recurso)
}

enum class TurnoSesion(private val recurso: Int) {
    MANANA(R.string.turno_manana),
    TARDE(R.string.turno_tarde),
    NOCHE(R.string.turno_noche);

    val etiqueta: String get() = Textos.obtener(recurso)
}
