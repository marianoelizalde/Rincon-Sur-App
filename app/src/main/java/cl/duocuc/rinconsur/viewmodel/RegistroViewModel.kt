package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.RegistroUiState
import cl.duocuc.rinconsur.model.TipoCompania
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onClasificacionSeleccionada(valor: Clasificacion) {
        _uiState.update {
            it.copy(
                clasificacion = valor,
                errorClasificacion = null
            )
        }
    }

    fun onRangoSeleccionado(valor: RangoEtario) {
        _uiState.update { it.copy(rango = valor, errorRango = null) }
    }

    fun onCompaniaSeleccionada(valor: TipoCompania) {
        _uiState.update { it.copy(compania = valor, errorCompania = null) }
    }

    fun onCompraOtraTienda(valor: Boolean) {
        _uiState.update { it.copy(compraOtraTienda = valor) }
    }


    // Boton "Guardar": valida. Si falta algo devuelve null; si no, devuelve el registro
    // y deja el formulario limpio para la siguiente persona.
    fun onRegistrar(idSesion: Int): RegistroObservacion? {
        val estado = _uiState.value

        val clasificacion = estado.clasificacion
        // Quien es "no determinado" no lleva detalle (rango ni compania).
        val pideDetalle = clasificacion != null

        val errorClasificacion = if (clasificacion == null) "Elige una clasificacion" else null
        val errorRango = if (pideDetalle && estado.rango == null) "Elige un rango de edad" else null
        val errorCompania = if (pideDetalle && estado.compania == null) "Elige con quien viene" else null

        _uiState.update {
            it.copy(
                errorClasificacion = errorClasificacion,
                errorRango = errorRango,
                errorCompania = errorCompania
            )
        }

        if (clasificacion == null || errorRango != null || errorCompania != null) return null

        val hora = SimpleDateFormat("HH:mm", Locale.US).format(Date())
        val registro = RegistroObservacion(
            idSesion = idSesion,
            hora = hora,
            clasificacion = clasificacion,
            rangoEtario = if (pideDetalle) estado.rango else null,
            tipoCompania = if (pideDetalle) estado.compania else null,
            compraOtraTienda = pideDetalle && estado.compraOtraTienda
        )
        _uiState.value = RegistroUiState()
        return registro
    }
}