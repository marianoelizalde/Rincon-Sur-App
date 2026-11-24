package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.SesionObservacion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class SesionRepositoryTest {

    private lateinit var apiFalsa: FakeSesionApi
    private lateinit var repositorio: SesionRepository

    @Before
    fun preparar() {
        apiFalsa = FakeSesionApi()
        repositorio = SesionRepository(
            sesionApi = apiFalsa,
            accesoApi = FakeAccesoApi(),
            estadoApi = FakeEstadoApi()
        )
    }

    @Test
    fun listar_ordenaLasSesionesDeNuevaAAntigua() = runBlocking {
        val lista = repositorio.listar()

        assertEquals(listOf(2, 3, 1), lista.map { it.id })
    }

    @Test
    fun programar_enviaLosDatosDeLaSesion() = runBlocking {
        val nueva = SesionObservacion(
            idAcceso = 1,
            fecha = "2026-12-01",
            horaInicio = "11:00",
            horaFin = "11:45",
            idObservador = 2
        )

        val creada = repositorio.programar(nueva)

        assertEquals(1L, apiFalsa.ultimaProgramacion?.accesoId)
        assertEquals(2L, apiFalsa.ultimaProgramacion?.observadorId)
        assertEquals("2026-12-01", apiFalsa.ultimaProgramacion?.fecha)
        assertEquals("2026-12-01", creada.fecha)
    }

    @Test
    fun iniciar_finalizarYCancelarDevuelvenElNuevoEstado() = runBlocking {
        assertEquals(EstadoSesion.EN_CURSO, repositorio.iniciar(1).estado)
        assertEquals(EstadoSesion.FINALIZADA, repositorio.finalizar(1).estado)
        assertEquals(EstadoSesion.CANCELADA, repositorio.cancelar(1).estado)
    }

    @Test
    fun validar_enviaElIdDelValidador() = runBlocking {
        val sesion = repositorio.validar(1, 9)

        assertEquals(9L, apiFalsa.ultimoValidador)
        assertEquals(EstadoSesion.VALIDADA, sesion.estado)
    }

    @Test
    fun rechazar_enviaElMotivoYElValidador() = runBlocking {
        val sesion = repositorio.rechazar(1, 9, "Faltan registros")

        assertEquals(9L, apiFalsa.ultimoRechazo?.idValidador)
        assertEquals("Faltan registros", apiFalsa.ultimoRechazo?.motivo)
        assertEquals(EstadoSesion.RECHAZADA, sesion.estado)
    }

    @Test
    fun accesos_seConvierteAlModelo() = runBlocking {
        val accesos = repositorio.accesos()

        assertEquals(2, accesos.size)
        assertEquals("Acceso sur", accesos[1].nombre)
    }
    @Test
    fun estados_seConvierteAlModelo() = runBlocking {
        val estados = repositorio.estados()
        assertEquals("EN_CURSO", estados[1].codigo)
        assertEquals("En curso", estados[1].etiqueta)
    }
}