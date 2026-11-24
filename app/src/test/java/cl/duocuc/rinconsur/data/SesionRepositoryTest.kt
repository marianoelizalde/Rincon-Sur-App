package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.SesionObservacion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
    fun rechazar_enviaElMotivoYElValidador() = runBlocking {
        val sesion = repositorio.rechazar(1, 9, "Faltan registros")

        assertEquals(9L, apiFalsa.ultimoRechazo?.idValidador)
        assertEquals("Faltan registros", apiFalsa.ultimoRechazo?.motivo)
        assertEquals(EstadoSesion.RECHAZADA, sesion.estado)
    }

}
