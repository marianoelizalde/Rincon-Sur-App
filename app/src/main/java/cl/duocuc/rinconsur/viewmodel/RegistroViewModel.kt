package cl.duocuc.rinconsur.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.RegistroUiState
import cl.duocuc.rinconsur.model.Sexo
import cl.duocuc.rinconsur.model.TipoCompania
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.Textos

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

    fun onSexoSeleccionado(valor: Sexo) {
        _uiState.update { it.copy(sexo = valor, errorSexo = null) }
    }

    fun onCompraOtraTienda(valor: Boolean) {
        _uiState.update { it.copy(compraOtraTienda = valor) }
    }

    fun onRegistrar(idSesion: Int): RegistroObservacion? {
        val estado = _uiState.value

        val clasificacion = estado.clasificacion

        val pideDetalle = clasificacion != null

        val errorClasificacion = if (clasificacion == null) Textos.obtener(R.string.msg_elige_una_clasificacion) else null
        val errorRango = if (pideDetalle && estado.rango == null) Textos.obtener(R.string.msg_elige_un_rango_de_edad) else null
        val errorCompania = if (pideDetalle && estado.compania == null) Textos.obtener(R.string.msg_elige_con_quien_viene) else null
        val errorSexo = if (pideDetalle && estado.sexo == null) Textos.obtener(R.string.msg_elige_el_sexo) else null
        _uiState.update {
            it.copy(
                errorClasificacion = errorClasificacion,
                errorRango = errorRango,
                errorCompania = errorCompania,
                errorSexo = errorSexo
            )
        }
        if (clasificacion == null || errorRango != null || errorCompania != null ||
            errorSexo != null) return null

        val hora = SimpleDateFormat("HH:mm", Locale.US).format(Date())
        val registro = RegistroObservacion(
            idSesion = idSesion,
            hora = hora,
            clasificacion = clasificacion,
            rangoEtario = if (pideDetalle) estado.rango else null,
            tipoCompania = if (pideDetalle) estado.compania else null,
            compraOtraTienda = pideDetalle && estado.compraOtraTienda,
            sexo = if (pideDetalle) estado.sexo else null
        )

        _uiState.value = RegistroUiState()
        return registro
    }
}