package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.Sexo
import cl.duocuc.rinconsur.model.TipoCompania
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegistroRepositoryTest {

    private lateinit var apiFalsa: FakeRegistroApi
    private lateinit var repositorio: RegistroRepository

    @Before
    fun preparar() {
        apiFalsa = FakeRegistroApi()
        repositorio = RegistroRepository(registroApi = apiFalsa)
    }

    @Test
    fun listar_retornaRegistro() = runBlocking {
        val lista = repositorio.listar(1)
        assertEquals(2, lista.size)
        assertEquals(Clasificacion.VISITA, lista[0].clasificacion)
        assertEquals(RangoEtario.DE_26_A_40, lista[0].rangoEtario)
        assertEquals(4, lista[0].idSesion)
        assertTrue(lista[1].anulado)
    }

    @Test
    fun registrar_enviaLosDatos_y_elIdDelActor() = runBlocking {
        val registro = RegistroObservacion(
            idSesion = 4,
            hora = "10:15",
            clasificacion = Clasificacion.TRANSITO,
            rangoEtario = RangoEtario.DE_18_A_25,
            tipoCompania = TipoCompania.GRUPO,
            compraOtraTienda = true,
            sexo = Sexo.MASCULINO
        )

        repositorio.registrar(registro, 9)

        assertEquals(4L, apiFalsa.nuevo?.sesionId)
        assertEquals("TRANSITO", apiFalsa.nuevo?.clasificacion)
        assertEquals("DE_18_A_25", apiFalsa.nuevo?.rangoEtario)
        assertEquals("GRUPO", apiFalsa.nuevo?.tipoCompania)
        assertEquals("MASCULINO", apiFalsa.nuevo?.sexo)
        assertEquals(9L, apiFalsa.actor)
    }
    @Test
    fun registrar_unRegistroNuevoNoVieneAnulado() = runBlocking {
        val registro = repositorio.registrar(
            RegistroObservacion(idSesion = 4, hora = "10:15", clasificacion = Clasificacion.VISITA),
            9
        )

        assertFalse(registro.anulado)
    }
    @Test
    fun anular_devuelveElRegistroAnulado() = runBlocking {
        val registro = repositorio.anular(1, 9)

        assertTrue(registro.anulado)
        assertEquals(9L, apiFalsa.actor)
    }

    @Test
    fun deshacerUltimo_devuelveElRegistroAnulado() = runBlocking {
        val registro = repositorio.deshacerUltimo(4, 9)

        assertTrue(registro.anulado)
        assertEquals(4, registro.idSesion)
    }
}
