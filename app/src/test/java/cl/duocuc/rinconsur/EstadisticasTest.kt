package cl.duocuc.rinconsur

import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.Estadisticas
import cl.duocuc.rinconsur.model.RegistroObservacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// La validacion de formularios vive dentro de los ViewModels (como en el proyecto del profesor);
// aqui se prueba la regla de negocio pura: el porcentaje de transito.
class EstadisticasTest {

    @Test
    fun porcentajeDeTransitoDelAnexo9() {
        // Sesion S-0001: 61 transito, 52 visita, 5 no determinados -> 61 / (118 - 5) = 54,0 %
        val registros = List(61) { registro(Clasificacion.TRANSITO) } +
            List(52) { registro(Clasificacion.VISITA) } +
            List(5) { registro(Clasificacion.NO_DETERMINADO) }
        assertEquals(54.0, Estadisticas.porcentajeTransito(registros)!!, 0.05)
    }

    @Test
    fun sinRegistrosNoHayPorcentaje() = assertNull(Estadisticas.porcentajeTransito(emptyList()))

    @Test
    fun losRegistrosAnuladosNoCuentan() {
        val registros = List(3) { registro(Clasificacion.TRANSITO) } +
            RegistroObservacion(idSesion = 1, hora = "10:00", clasificacion = Clasificacion.VISITA, anulado = true)
        assertEquals(3, Estadisticas.registrosValidos(registros).size)
    }

    private fun registro(clasificacion: Clasificacion) =
        RegistroObservacion(idSesion = 1, hora = "10:00", clasificacion = clasificacion)
}
