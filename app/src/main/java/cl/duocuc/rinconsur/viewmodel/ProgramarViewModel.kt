package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.ProgramarUiState
import cl.duocuc.rinconsur.model.SesionObservacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel simple: guarda el estado del formulario de programar sesion y valida.
// No toca la base de datos: la lista de accesos y observadores la entrega RinconViewModel,
// y el choque de horarios tambien lo revisa RinconViewModel al guardar.
class ProgramarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProgramarUiState())
    val uiState: StateFlow<ProgramarUiState> = _uiState.asStateFlow()

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
        _uiState.update { it.copy(horaInicio = valor, errorInicio = errorHora(valor), errorGeneral = null) }
    }

    fun onHoraFinCambia(valor: String) {
        _uiState.update {
            it.copy(horaFin = valor, errorFin = errorFin(it.horaInicio, valor), errorGeneral = null)
        }
    }

    // Boton "Programar sesion": valida todo. Si hay errores devuelve null;
    // si no, devuelve la SesionObservacion lista para guardar.
    fun onProgramar(): SesionObservacion? {
        val estado = _uiState.value

        val errorAcceso = if (estado.idAcceso == null) "Elige un acceso" else null
        val errorObservador = if (estado.idObservador == null) "Elige un observador" else null
        val errorFecha = errorFecha(estado.fecha)
        val errorInicio = errorHora(estado.horaInicio)
        val errorFin = errorFin(estado.horaInicio, estado.horaFin)

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

    // La pantalla lo llama cuando RinconViewModel dice que ya hay una sesion en ese horario.
    fun onSolapamiento() {
        _uiState.update { it.copy(errorGeneral = "Ya hay una sesion en ese acceso y horario") }
    }

    // ---- Reglas de validacion: devuelven el mensaje de error, o null si el valor es valido ----

    private fun errorFecha(valor: String): String? {
        if (valor.isBlank()) return "Ingresa la fecha"
        if (!REGEX_FECHA.matches(valor)) return "Usa el formato AAAA-MM-DD"
        val anio = valor.substring(0, 4).toInt()
        val mes = valor.substring(5, 7).toInt()
        val dia = valor.substring(8, 10).toInt()
        val bisiesto = anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)
        val diasDelMes = when (mes) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (bisiesto) 29 else 28
            else -> return "El mes debe estar entre 01 y 12"
        }
        return if (dia < 1 || dia > diasDelMes) "Ese dia no existe en el mes" else null
    }

    private fun errorHora(valor: String): String? = when {
        valor.isBlank() -> "Ingresa la hora"
        !REGEX_HORA.matches(valor) -> "Usa el formato HH:MM (24 horas)"
        else -> null
    }

    private fun minutos(hora: String): Int =
        hora.substring(0, 2).toInt() * 60 + hora.substring(3, 5).toInt()

    // Hora de termino: formato valido, tienda abierta (10:00 a 21:00) y duracion de 30 a 60 minutos (RN-09).
    private fun errorFin(inicio: String, fin: String): String? {
        errorHora(fin)?.let { return it }
        if (errorHora(inicio) != null) return null
        if (minutos(inicio) < 10 * 60) return "La tienda abre a las 10:00"
        if (minutos(fin) > 21 * 60) return "La tienda cierra a las 21:00"
        val duracion = minutos(fin) - minutos(inicio)
        return if (duracion !in 30..60) "La sesion debe durar entre 30 y 60 minutos" else null
    }

    private companion object {
        val REGEX_FECHA = Regex("""^\d{4}-\d{2}-\d{2}$""")
        val REGEX_HORA = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")
    }
}