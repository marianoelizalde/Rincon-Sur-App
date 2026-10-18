package cl.duocuc.rinconsur.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme

// =====================================================================
//  ControlesSeleccion.kt  -  Casilla (checkbox) y grupo de opciones (chips).
//  Truco de usabilidad: toda la fila es presionable (toggleable) y al
//  control se le pasa onCheckedChange = null.
// =====================================================================

/** Checkbox + texto (por ejemplo, aceptar terminos). */
@Composable
fun CasillaConTexto(
    texto: String,
    marcado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = marcado, onValueChange = onCambio, role = Role.Checkbox)
            .padding(vertical = Dimens.espacioMini),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = marcado, onCheckedChange = null)
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = Dimens.espacioChico)
        )
    }
}

/** Texto + Switch (interruptor encendido/apagado). */
@Composable
fun InterruptorConTexto(
    texto: String,
    activado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = activado, onValueChange = onCambio, role = Role.Switch)
            .padding(vertical = Dimens.espacioMini),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween   // texto a la izquierda, switch a la derecha
    ) {
        Text(text = texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = activado, onCheckedChange = null)
    }
}

/**
 * Grupo de chips: se elige UNA opcion entre varias. Es generico <T>.
 * @param textoDe funcion que convierte cada opcion en el texto a mostrar.
 * @param error mensaje bajo el grupo si no se eligio nada.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> GrupoChips(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
            modifier = Modifier.padding(top = Dimens.espacioFino)
        ) {
            opciones.forEach { opcion ->
                val elegida = opcion == seleccionada
                FilterChip(
                    selected = elegida,
                    onClick = { onSeleccionar(opcion) },
                    label = { Text(textoDe(opcion)) },
                    leadingIcon = if (elegida) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(Dimens.tamanoIcono)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = Dimens.espacioMini)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlesSeleccionPreview() {
    RinconSurTheme {
        Column(Modifier.padding(16.dp)) {
            CasillaConTexto(texto = "Acepto los terminos", marcado = true, onCambio = {})
            InterruptorConTexto(texto = "Compro en otra tienda", activado = false, onCambio = {})
            GrupoChips(
                titulo = "Acceso",
                opciones = listOf("A1", "A2", "A3"),
                seleccionada = "A2",
                onSeleccionar = {},
                textoDe = { it }
            )
        }
    }
}