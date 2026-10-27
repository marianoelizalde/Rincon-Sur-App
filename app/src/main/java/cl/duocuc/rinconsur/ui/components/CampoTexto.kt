package cl.duocuc.rinconsur.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.ui.styles.EstilosCampo
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme


@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    @DrawableRes icono: Int,
    modifier: Modifier = Modifier,
    error: String? = null,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        trailingIcon = {

            if (error != null) {
                Icon(
                    painter = painterResource(R.drawable.ic_error),
                    contentDescription = stringResource(R.string.cd_error),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        },
        isError = error != null,
        supportingText = {
            val texto = error ?: ayuda
            if (texto != null) Text(texto)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = ImeAction.Next
        ),
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    RinconSurTheme {
        CampoTexto(
            valor = "ana@",
            onValorCambia = {},
            etiqueta = "Correo electronico",
            icono = R.drawable.ic_correo,
            error = "Correo no valido (ej: nombre@correo.cl)",
            modifier = Modifier.padding(16.dp)
        )
    }
}