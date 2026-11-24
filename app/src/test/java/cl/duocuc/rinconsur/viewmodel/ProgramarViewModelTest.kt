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
    fun onFechaCambia_fechaInexistente() {
        viewModel.onFechaCambia("2026-02-30")

        assertNotNull(viewModel.uiState.value.errorFecha)
    }

}
