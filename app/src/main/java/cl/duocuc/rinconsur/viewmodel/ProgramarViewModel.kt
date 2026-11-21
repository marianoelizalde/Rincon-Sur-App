package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.ProgramarUiState
import cl.duocuc.rinconsur.model.SesionObservacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

class ProgramarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProgramarUiState())
    val uiState: StateFlow<ProgramarUiState> = _uiState.asStateFlow()

    init {
        onFechaCambia(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
        onHoraInicioCambia("10:00")
        onDuracionSeleccionada(45)
    }

    fun onAccesoSeleccionado(id: Int) {
        _uiState.update { it.copy(idAcceso = id, errorAcceso = null, errorGeneral = null) }
    }

    fun onObservadorSeleccionado(id: Int) {
        _uiState.update { it.copy(idObservador = id, errorObservador = null, errorGeneral = null) }
    }

    fun onFechaCambia(valor: String) {
        _uiState.update { it.copy(fecha = valor, errorFecha = errorFecha(valor), errorGeneral = null) }
    }

    fun onHoraInicioCambia(valor: String) {
        _uiState.update {
            val fin = calcularFin(valor, it.duracion)
            it.copy(
                horaInicio = valor,
                horaFin = fin,
                errorInicio = errorHora(valor),
                errorFin = if (fin.isEmpty()) null else errorFin(valor, fin),
                errorGeneral = null
            )
        }
    }

    fun onDuracionSeleccionada(minutos: Int) {
        _uiState.update {
            val fin = calcularFin(it.horaInicio, minutos)
            it.copy(
                duracion = minutos,
                horaFin = fin,
                errorFin = if (fin.isEmpty()) null else errorFin(it.horaInicio, fin),
                errorGeneral = null
            )
        }
    }

    fun onHoraCambiaEn(delta: Int) {
        val (hora, minuto) = partesDeInicio()
        onHoraInicioCambia("%02d:%02d".format((hora + delta).coerceIn(10, 20), minuto))
    }

    fun onMinutosCambiaEn(delta: Int) {
        val (hora, minuto) = partesDeInicio()
        onHoraInicioCambia("%02d:%02d".format(hora, (minuto + delta * 15 + 60) % 60))
    }

    fun onDuracionCambiaEn(delta: Int) {
        val actual = _uiState.value.duracion ?: 45
        onDuracionSeleccionada((actual + delta * 15).coerceIn(30, 60))
    }

    private fun partesDeInicio(): Pair<Int, Int> {
        val inicio = _uiState.value.horaInicio
        return if (errorHora(inicio) == null) {
            minutos(inicio) / 60 to minutos(inicio) % 60
        } else {
            10 to 0
        }
    }

    fun onProgramar(): SesionObservacion? {
        val estado = _uiState.value

        val errorAcceso = if (estado.idAcceso == null) Textos.obtener(R.string.msg_elige_un_acceso) else null
        val errorObservador = if (estado.idObservador == null) Textos.obtener(R.string.msg_elige_un_observador) else null
        val errorFecha = errorFecha(estado.fecha)
        val errorInicio = errorHora(estado.horaInicio)
        val errorFin = if (estado.duracion == null) Textos.obtener(R.string.msg_elige_la_duracion)
        else errorFin(estado.horaInicio, estado.horaFin)

        _uiState.update {
            it.copy(
                errorAcceso = errorAcceso,
                errorObservador = errorObservador,
                errorFecha = errorFecha,
                errorInicio = errorInicio,
                errorFin = errorFin
            )
        }

        val idAcceso = estado.idAcceso
        val idObservador = estado.idObservador
        val hayErrores = listOf(errorAcceso, errorObservador, errorFecha, errorInicio, errorFin)
            .any { it != null }

        return if (hayErrores || idAcceso == null || idObservador == null) null else SesionObservacion(
            idAcceso = idAcceso,
            fecha = estado.fecha,
            horaInicio = estado.horaInicio,
            horaFin = estado.horaFin,
            idObservador = idObservador
        )
    }

    fun onErrorGeneral(mensaje: String) {
        _uiState.update { it.copy(errorGeneral = mensaje) }
    }

    private fun errorFecha(valor: String): String? {
        if (valor.isBlank()) return Textos.obtener(R.string.msg_ingresa_la_fecha)
        if (!REGEX_FECHA.matches(valor)) return Textos.obtener(R.string.msg_usa_el_formato_aaaa_mm_dd)
        val anio = valor.substring(0, 4).toInt()
        val mes = valor.substring(5, 7).toInt()
        val dia = valor.substring(8, 10).toInt()
        val bisiesto = anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)
        val diasDelMes = when (mes) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (bisiesto) 29 else 28
            else -> return Textos.obtener(R.string.msg_el_mes_debe_estar_entre_01)
        }
        return if (dia < 1 || dia > diasDelMes) Textos.obtener(R.string.msg_ese_dia_no_existe_en_el) else null
    }

    private fun errorHora(valor: String): String? = when {
        valor.isBlank() -> Textos.obtener(R.string.msg_ingresa_la_hora)
        !REGEX_HORA.matches(valor) -> Textos.obtener(R.string.msg_usa_el_formato_hh_mm_24)
        else -> null
    }

    private fun minutos(hora: String): Int =
        hora.substring(0, 2).toInt() * 60 + hora.substring(3, 5).toInt()

    private fun calcularFin(inicio: String, duracion: Int?): String {
        if (duracion == null || errorHora(inicio) != null) return ""
        val total = minutos(inicio) + duracion
        return "%02d:%02d".format(total / 60, total % 60)
    }
    private fun errorFin(inicio: String, fin: String): String? {
        errorHora(fin)?.let { return it }
        if (errorHora(inicio) != null) return null
        if (minutos(inicio) < 10 * 60) return Textos.obtener(R.string.msg_la_tienda_abre_a_las_10)
        if (minutos(fin) > 21 * 60) return Textos.obtener(R.string.msg_la_tienda_cierra_a_las_21)
        val duracion = minutos(fin) - minutos(inicio)
        return if (duracion !in 30..60) Textos.obtener(R.string.msg_la_sesion_debe_durar_entre_30) else null
    }

    private companion object {
        val REGEX_FECHA = Regex("""^\d{4}-\d{2}-\d{2}$""")
        val REGEX_HORA = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")
    }
}