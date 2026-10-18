package cl.duocuc.rinconsur.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.EstadoCancelada
import cl.duocuc.rinconsur.ui.theme.EstadoEnCurso
import cl.duocuc.rinconsur.ui.theme.EstadoFinalizada
import cl.duocuc.rinconsur.ui.theme.EstadoProgramada
import cl.duocuc.rinconsur.ui.theme.EstadoRechazada
import cl.duocuc.rinconsur.ui.theme.EstadoValidada
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme

// Etiqueta con el estado de la sesion; el color cambia con una animacion suave.
@Composable
fun EtiquetaEstado(estado: EstadoSesion, modifier: Modifier = Modifier) {
    val destino = when (estado) {
        EstadoSesion.PROGRAMADA -> EstadoProgramada
        EstadoSesion.EN_CURSO -> EstadoEnCurso
        EstadoSesion.FINALIZADA -> EstadoFinalizada
        EstadoSesion.VALIDADA -> EstadoValidada
        EstadoSesion.RECHAZADA -> EstadoRechazada
        EstadoSesion.CANCELADA -> EstadoCancelada
    }
    val color by animateColorAsState(destino, label = "colorEstado")
    Surface(
        color = color,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            estado.etiqueta,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(PaddingValues(horizontal = Dimens.espacioEtiqueta, vertical = Dimens.espacioMini))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EtiquetaEstadoPreview() {
    RinconSurTheme {
        EtiquetaEstado(estado = EstadoSesion.VALIDADA, modifier = Modifier.padding(16.dp))
    }
}