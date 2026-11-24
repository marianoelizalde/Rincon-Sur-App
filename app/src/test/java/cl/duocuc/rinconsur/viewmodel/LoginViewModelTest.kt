package cl.duocuc.rinconsur.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        viewModel = LoginViewModel()
    }

    @Test
    fun onIngresar_conCamposVacios() {
        val resultado = viewModel.onIngresar()

        assertFalse(resultado)
        assertNotNull(viewModel.uiState.value.errorCorreo)
        assertNotNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onIngresar_ingresarConDatosCorrectos() {
        viewModel.onCorreoCambia("ana@duocuc.cl")
        viewModel.onClaveCambia("Clave123!")

        val resultado = viewModel.onIngresar()

        assertTrue(resultado)
        assertNull(viewModel.uiState.value.errorCorreo)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onCorreoCambia_sinEspaciosAlInicioNiAlFinal() {
        viewModel.onCorreoCambia("   ana@duocuc.cl   ")

        assertEquals("ana@duocuc.cl", viewModel.uiState.value.correo)
    }

    @Test
    fun onIngresar_correoSinArroba() {
        viewModel.onCorreoCambia("anaduocuc.cl")
        viewModel.onClaveCambia("Clave123!")

        val resultado = viewModel.onIngresar()

        assertFalse(resultado)
        assertNotNull(viewModel.uiState.value.errorCorreo)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onIngresar_claveVacia() {
        viewModel.onCorreoCambia("ana@duocuc.cl")

        val resultado = viewModel.onIngresar()

        assertFalse(resultado)
        assertNull(viewModel.uiState.value.errorCorreo)
        assertNotNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onIngresar_alEscribirDeNuevo() {
        viewModel.onIngresar()

        viewModel.onCorreoCambia("ana@duocuc.cl")
        viewModel.onClaveCambia("Clave123!")

        assertNull(viewModel.uiState.value.errorCorreo)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onAlternarVisibilidad_CambiaElEstadoDeLaClave() {
        assertFalse(viewModel.uiState.value.claveVisible)

        viewModel.onAlternarVisibilidad()
        assertTrue(viewModel.uiState.value.claveVisible)

        viewModel.onAlternarVisibilidad()
        assertFalse(viewModel.uiState.value.claveVisible)
    }

    @Test
    fun onCargando_y_onErrorGeneral_seGuardanEnElEstado() {
        viewModel.onCargando(true)
        viewModel.onErrorGeneral("Credenciales incorrectas")

        assertTrue(viewModel.uiState.value.cargando)
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.errorGeneral)
    }
}