package cl.duocuc.rinconsur.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ProgramarViewModelTest {

    private lateinit var viewModel: ProgramarViewModel

    @Before
    fun preparar() {
        viewModel = ProgramarViewModel()
    }

    @Test
    fun onProgramar_sinAccesoNiObservador() {
        val sesion = viewModel.onProgramar()
        val estado = viewModel.uiState.value

        assertNull(sesion)
        assertNotNull(estado.errorAcceso)
        assertNotNull(estado.errorObservador)
    }

    @Test
    fun onAccesoSeleccionado_y_onObservadorSeleccionado_programaLaSesion() {
        viewModel.onAccesoSeleccionado(1)
        viewModel.onObservadorSeleccionado(2)

        val sesion = viewModel.onProgramar()

        assertNotNull(sesion)
        assertEquals(1, sesion!!.idAcceso)
        assertEquals(2, sesion.idObservador)
        assertEquals("10:00", sesion.horaInicio)
        assertEquals("10:45", sesion.horaFin)
    }

    @Test
    fun onDuracionSeleccionada_cambiarLaDuracion() {
        viewModel.onDuracionSeleccionada(60)

        assertEquals("11:00", viewModel.uiState.value.horaFin)
    }

    @Test
    fun onHoraInicioCambia_cambiarLaHoraDeInicio() {
        viewModel.onHoraInicioCambia("14:30")

        assertEquals("15:15", viewModel.uiState.value.horaFin)
    }

    @Test
    fun onFechaCambia_fechaInexistente() {
        viewModel.onFechaCambia("2026-02-30")

        assertNotNull(viewModel.uiState.value.errorFecha)
    }

    @Test
    fun onFechaCambia_fechaDeAnioBisiestoEsValida() {
        viewModel.onFechaCambia("2024-02-29")

        assertNull(viewModel.uiState.value.errorFecha)
    }

    @Test
    fun onFechaCambia_fechaConFormatoIncorrecto() {
        viewModel.onFechaCambia("24-11-2026")

        assertNotNull(viewModel.uiState.value.errorFecha)
    }

    @Test
    fun onHoraInicioCambia_horaAntesDeLaApertura() {
        viewModel.onHoraInicioCambia("09:00")

        assertNotNull(viewModel.uiState.value.errorFin)
    }

    @Test
    fun onHoraInicioCambia_horaDespuesDelCierre() {
        viewModel.onHoraInicioCambia("20:30")

        assertNotNull(viewModel.uiState.value.errorFin)
    }

    @Test
    fun onHoraInicioCambia_horaConFormatoIncorrecto() {
        viewModel.onHoraInicioCambia("25:99")

        assertNotNull(viewModel.uiState.value.errorInicio)
    }

    @Test
    fun onHoraCambiaEn_laHoraNoPasaDeLas20() {
        viewModel.onHoraCambiaEn(20)

        assertEquals("20:00", viewModel.uiState.value.horaInicio)
    }

    @Test
    fun onMinutosCambiaEn_losMinutosAvanzanDeA15() {
        viewModel.onMinutosCambiaEn(1)

        assertEquals("10:15", viewModel.uiState.value.horaInicio)
    }

    @Test
    fun onDuracionCambiaEn_laDuracionQuedaEntre30y60Minutos() {
        viewModel.onDuracionCambiaEn(5)
        assertEquals(60, viewModel.uiState.value.duracion)

        viewModel.onDuracionCambiaEn(-5)
        assertEquals(30, viewModel.uiState.value.duracion)
    }
}
