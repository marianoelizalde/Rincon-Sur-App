package cl.duocuc.rinconsur.viewmodel

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecuperarViewModelTest {

    private lateinit var viewModel: RecuperarViewModel
    private var llamadas = 0
    private var correoEnviado: String? = null
    private var rutEnviado: String? = null
    private var claveEnviada: String? = null

    @Before
    fun preparar() {
        viewModel = RecuperarViewModel()
        llamadas = 0
        correoEnviado = null
        rutEnviado = null
        claveEnviada = null
    }

    private fun llenarFormularioValido() {
        viewModel.onCorreoCambia("Ana@Duocuc.cl")
        viewModel.onRutCambia("12.345.678-5")
        viewModel.onClaveCambia("Clave123!")
        viewModel.onConfirmarCambia("Clave123!")
    }

    private fun recuperarConExito(correo: String, rut: String, clave: String): String? {
        llamadas++
        correoEnviado = correo
        rutEnviado = rut
        claveEnviada = clave
        return null
    }

    @Test
    fun onRecuperar_formularioValido() = runBlocking {
        llenarFormularioValido()

        viewModel.onRecuperar { c, r, k -> recuperarConExito(c, r, k) }

        assertEquals(1, llamadas)
        assertEquals("ana@duocuc.cl", correoEnviado)
        assertEquals("12345678-5", rutEnviado)
        assertEquals("Clave123!", claveEnviada)
        assertTrue(viewModel.uiState.value.listo)
    }

    @Test
    fun onRecuperar_formularioVacio() = runBlocking {
        viewModel.onRecuperar { c, r, k -> recuperarConExito(c, r, k) }
        val estado = viewModel.uiState.value

        assertEquals(0, llamadas)
        assertFalse(estado.listo)
        assertNotNull(estado.errorCorreo)
        assertNotNull(estado.errorRut)
        assertNotNull(estado.errorClave)
        assertNotNull(estado.errorConfirmar)
    }

    @Test
    fun onConfirmarCambia_y_onRecuperar_clavesDistintas() = runBlocking {
        llenarFormularioValido()
        viewModel.onConfirmarCambia("Otra123!")

        viewModel.onRecuperar { c, r, k -> recuperarConExito(c, r, k) }

        assertEquals(0, llamadas)
        assertNotNull(viewModel.uiState.value.errorConfirmar)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onRecuperar_claveSinNumero() = runBlocking {
        llenarFormularioValido()
        viewModel.onClaveCambia("Clavecita!")
        viewModel.onConfirmarCambia("Clavecita!")

        viewModel.onRecuperar { c, r, k -> recuperarConExito(c, r, k) }

        assertEquals(0, llamadas)
        assertNotNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onRecuperar_siElServidorRespondeConError() = runBlocking {
        llenarFormularioValido()

        viewModel.onRecuperar { _, _, _ -> "Los datos no coinciden" }

        assertEquals("Los datos no coinciden", viewModel.uiState.value.errorGeneral)
        assertFalse(viewModel.uiState.value.listo)
    }

    @Test
    fun onAlternarVisibilidad_alternarVisibilidadCambiaElEstadoDeLaClave() {
        assertFalse(viewModel.uiState.value.claveVisible)

        viewModel.onAlternarVisibilidad()

        assertTrue(viewModel.uiState.value.claveVisible)
    }
}