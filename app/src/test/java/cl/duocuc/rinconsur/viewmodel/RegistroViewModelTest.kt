package cl.duocuc.rinconsur.viewmodel

import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.Sexo
import cl.duocuc.rinconsur.model.TipoCompania
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegistroViewModelTest {

    private lateinit var viewModel: RegistroViewModel

    @Before
    fun preparar() {
        viewModel = RegistroViewModel()
    }

    private fun llenarRegistroCompleto() {
        viewModel.onClasificacionSeleccionada(Clasificacion.VISITA)
        viewModel.onRangoSeleccionado(RangoEtario.DE_26_A_40)
        viewModel.onCompaniaSeleccionada(TipoCompania.PAREJA)
        viewModel.onSexoSeleccionado(Sexo.FEMENINO)
    }

    @Test
    fun onRegistrar_sinClasificacion() {
        val registro = viewModel.onRegistrar(1)

        assertNull(registro)
        assertNotNull(viewModel.uiState.value.errorClasificacion)
    }

    @Test
    fun onRegistrar_registroCompleto() {
        llenarRegistroCompleto()
        viewModel.onCompraOtraTienda(true)

        val registro = viewModel.onRegistrar(7)

        assertNotNull(registro)
        assertEquals(7, registro!!.idSesion)
        assertEquals(Clasificacion.VISITA, registro.clasificacion)
        assertEquals(RangoEtario.DE_26_A_40, registro.rangoEtario)
        assertEquals(TipoCompania.PAREJA, registro.tipoCompania)
        assertEquals(Sexo.FEMENINO, registro.sexo)
        assertTrue(registro.compraOtraTienda)
        assertFalse(registro.anulado)
    }

    @Test
    fun onRegistrar_despuesDeRegistrar() {
        llenarRegistroCompleto()

        viewModel.onRegistrar(1)
        val estado = viewModel.uiState.value

        assertNull(estado.clasificacion)
        assertNull(estado.rango)
        assertNull(estado.compania)
        assertNull(estado.sexo)
        assertFalse(estado.compraOtraTienda)
    }

}
