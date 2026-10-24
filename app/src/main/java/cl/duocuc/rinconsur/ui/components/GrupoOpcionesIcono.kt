package cl.duocuc.rinconsur.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.ui.theme.Dimens

@Composable
fun <T> GrupoOpcionesIcono(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    iconoDe: (T) -> Int,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.espacioFino)
        ) {
            opciones.forEach { opcion ->
                val elegida = opcion == seleccionada
                val forma = MaterialTheme.shapes.medium
                val colorFondo =
                    if (elegida) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                val colorBorde =
                    if (elegida) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                val colorContenido =
                    if (elegida) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(forma)
                        .background(colorFondo)
                        .border(if (elegida) 2.dp else 1.dp, colorBorde, forma)
                        .selectable(
                            selected = elegida,
                            onClick = { onSeleccionar(opcion) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = Dimens.espacioMedio, horizontal = Dimens.espacioMini)
                ) {
                    Icon(
                        painter = painterResource(iconoDe(opcion)),
                        contentDescription = null,
                        tint = colorContenido,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = textoDe(opcion),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorContenido,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = Dimens.espacioMini)
                    )
                }
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
