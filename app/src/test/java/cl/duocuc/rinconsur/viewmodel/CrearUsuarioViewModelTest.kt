package cl.duocuc.rinconsur.viewmodel

import cl.duocuc.rinconsur.model.PerfilUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CrearUsuarioViewModelTest {

    private lateinit var viewModel: CrearUsuarioViewModel

    @Before
    fun preparar() {
        viewModel = CrearUsuarioViewModel()
    }

    private fun llenarFormularioValido() {
        viewModel.onNombreCambia("Ana Perez")
        viewModel.onCorreoCambia("Ana@Duocuc.cl")
        viewModel.onRutCambia("12.345.678-5")
        viewModel.onCargoCambia("Guardia")
        viewModel.onTelefonoCambia("912345678")
        viewModel.onClaveCambia("Clave123!")
        viewModel.onConfirmarCambia("Clave123!")
        viewModel.onPerfilSeleccionado(PerfilUsuario.SUPERVISOR)
        viewModel.onTerminosCambia(true)
    }

    @Test
    fun onCrear_formularioValido_creaElColaboradorConDatosNormalizados() {
        llenarFormularioValido()

        val colaborador = viewModel.onCrear()

        assertNotNull(colaborador)
        assertEquals("Ana Perez", colaborador!!.nombre)
        assertEquals("ana@duocuc.cl", colaborador.correo)
        assertEquals("12345678-5", colaborador.rut)
        assertEquals(PerfilUsuario.SUPERVISOR, colaborador.perfil)
    }

    @Test
    fun onCrear_formularioVacio_noCreaNadaYMarcaTodosLosErrores() {
        val colaborador = viewModel.onCrear()
        val estado = viewModel.uiState.value

        assertNull(colaborador)
        assertNotNull(estado.errorNombre)
        assertNotNull(estado.errorCorreo)
        assertNotNull(estado.errorRut)
        assertNotNull(estado.errorCargo)
        assertNotNull(estado.errorClave)
        assertNotNull(estado.errorConfirmar)
        assertNotNull(estado.errorTerminos)
    }

    @Test
    fun onCrear_rutConDigitoVerificadorIncorrecto() {
        llenarFormularioValido()
        viewModel.onRutCambia("12345678-9")

        val colaborador = viewModel.onCrear()

        assertNull(colaborador)
        assertNotNull(viewModel.uiState.value.errorRut)
    }

    @Test
    fun onCrear_clavesDistintas() {
        llenarFormularioValido()
        viewModel.onConfirmarCambia("Otra123!")

        val colaborador = viewModel.onCrear()

        assertNull(colaborador)
        assertNotNull(viewModel.uiState.value.errorConfirmar)
        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onCrear_correoSinArroba() {
        llenarFormularioValido()
        viewModel.onCorreoCambia("anaduocuc.cl")

        val colaborador = viewModel.onCrear()

        assertNull(colaborador)
        assertNotNull(viewModel.uiState.value.errorCorreo)
    }

    @Test
    fun onClaveCambia_claveSinMayusculaNoEsValida() {
        viewModel.onClaveCambia("clave123!")

        assertNotNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onClaveCambia_claveValidaNoMarcaError() {
        viewModel.onClaveCambia("Clave123!")

        assertNull(viewModel.uiState.value.errorClave)
    }

    @Test
    fun onNombreCambia_nombreConNumerosNoEsValido() {
        viewModel.onNombreCambia("Ana123")

        assertNotNull(viewModel.uiState.value.errorNombre)
    }

    @Test
    fun onTelefonoCambia_telefonoConOchoDigitosNoEsValido() {
        llenarFormularioValido()
        viewModel.onTelefonoCambia("12345678")

        val colaborador = viewModel.onCrear()

        assertNull(colaborador)
        assertNotNull(viewModel.uiState.value.errorTelefono)
    }

    @Test
    fun onTelefonoCambia_telefonoVacioEsOpcional() {
        llenarFormularioValido()
        viewModel.onTelefonoCambia("")

        val colaborador = viewModel.onCrear()

        assertNotNull(colaborador)
        assertNull(viewModel.uiState.value.errorTelefono)
    }

    @Test
    fun onTerminosCambia_sinAceptarLosTerminosNoSeCreaLaCuenta() {
        llenarFormularioValido()
        viewModel.onTerminosCambia(false)

        val colaborador = viewModel.onCrear()

        assertNull(colaborador)
        assertNotNull(viewModel.uiState.value.errorTerminos)
    }

    @Test
    fun onRutCambia_rutConDigitoVerificadorKEsValido() {
        llenarFormularioValido()
        viewModel.onRutCambia("10.000.013-K")

        val colaborador = viewModel.onCrear()

        assertNotNull(colaborador)
        assertEquals("10000013-K", colaborador!!.rut)
    }

    @Test
    fun onLimpiar_DejaElFormularioComoAlInicio() {
        llenarFormularioValido()

        viewModel.onLimpiar()

        assertEquals("", viewModel.uiState.value.nombre)
        assertEquals("", viewModel.uiState.value.correo)
    }
    @Test
    fun onCorreoRepetido_marcaElErrorDelCorreo() {
        viewModel.onCorreoRepetido("Ese correo ya existe")

        assertEquals("Ese correo ya existe", viewModel.uiState.value.errorCorreo)
    }
}