package cl.duocuc.rinconsur.viewmodel

import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.PerfilUsuario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditarUsuarioViewModelTest {

    private lateinit var viewModel: EditarUsuarioViewModel
    private var idEnviado: Int? = null
    private var rutEnviado: String? = null
    private var llamadas = 0

    private val colaborador = Colaborador(
        id = 5,
        nombre = "Ana Perez",
        correo = "ana@duocuc.cl",
        clave = "",
        perfil = PerfilUsuario.SUPERVISOR,
        rut = "12345678-5",
        cargo = "Guardia",
        telefono = "912345678"
    )

    @Before
    fun preparar() {
        viewModel = EditarUsuarioViewModel()
        idEnviado = null
        rutEnviado = null
        llamadas = 0
        viewModel.cargar(colaborador)
    }

    private fun guardarConExito(id: Int, nombre: String, rut: String, cargo: String, telefono: String): String? {
        llamadas++
        idEnviado = id
        rutEnviado = rut
        return null
    }

    @Test
    fun onRutCambia_guardarConDatosValidos_enviaElRutNormalizado() = runBlocking {
        viewModel.onRutCambia("12.345.678-5")

        val resultado = viewModel.onGuardar { i, n, r, c, t -> guardarConExito(i, n, r, c, t) }

        assertTrue(resultado)
        assertEquals(1, llamadas)
        assertEquals(5, idEnviado)
        assertEquals("12345678-5", rutEnviado)
    }

    @Test
    fun onNombreCambia_nombreMuyCorto() = runBlocking {
        viewModel.onNombreCambia("Al")

        val resultado = viewModel.onGuardar { i, n, r, c, t -> guardarConExito(i, n, r, c, t) }

        assertFalse(resultado)
        assertEquals(0, llamadas)
        assertNotNull(viewModel.uiState.value.errorNombre)
    }

    @Test
    fun onRutCambia_rutConDigitoVerificadorIncorrecto() = runBlocking {
        viewModel.onRutCambia("12345678-9")

        val resultado = viewModel.onGuardar { i, n, r, c, t -> guardarConExito(i, n, r, c, t) }

        assertFalse(resultado)
        assertEquals(0, llamadas)
        assertNotNull(viewModel.uiState.value.errorRut)
    }

    @Test
    fun onTelefonoCambia_telefonoConOchoDigitos() = runBlocking {
        viewModel.onTelefonoCambia("12345678")

        val resultado = viewModel.onGuardar { i, n, r, c, t -> guardarConExito(i, n, r, c, t) }

        assertFalse(resultado)
        assertNotNull(viewModel.uiState.value.errorTelefono)
    }

    @Test
    fun onTelefonoCambia_telefonoVacioEsValido() = runBlocking {
        viewModel.onTelefonoCambia("")

        val resultado = viewModel.onGuardar { i, n, r, c, t -> guardarConExito(i, n, r, c, t) }

        assertTrue(resultado)
        assertNull(viewModel.uiState.value.errorTelefono)
    }

    @Test
    fun onGuardar_errorDelServidorconRut() = runBlocking {
        val resultado = viewModel.onGuardar { _, _, _, _, _ -> "Ese RUT ya pertenece a otro usuario" }

        assertFalse(resultado)
        assertEquals("Ese RUT ya pertenece a otro usuario", viewModel.uiState.value.errorRut)
        assertNull(viewModel.uiState.value.errorGeneral)
    }

    @Test
    fun onGuardar_errorGeneralDelServidor() = runBlocking {
        val resultado = viewModel.onGuardar { _, _, _, _, _ -> "No se pudo conectar" }

        assertFalse(resultado)
        assertEquals("No se pudo conectar", viewModel.uiState.value.errorGeneral)
    }
}
