package cl.duocuc.rinconsur.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.ui.styles.EstilosBoton
import cl.duocuc.rinconsur.ui.styles.estiloAltoBoton
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme


@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icono: Int? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.estiloAltoBoton(),
        shape = EstilosBoton.forma,
        colors = EstilosBoton.coloresSecundario(),
        border = EstilosBoton.bordeSecundario(),
        contentPadding = EstilosBoton.relleno
    ) {
        if (icono != null) {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text = texto)
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonSecundarioPreview() {
    RinconSurTheme {
        BotonSecundario(
            texto = "Crear usuario",
            onClick = {},
            icono = R.drawable.ic_persona_agregar,
            modifier = Modifier.padding(16.dp)
        )
    }
}