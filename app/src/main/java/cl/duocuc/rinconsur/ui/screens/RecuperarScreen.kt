package cl.duocuc.rinconsur.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.R.string.titulo_recuperar
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.CampoContrasena
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarScreen(
    onRecuperarCuenta: (String, String, String) -> Boolean,
    onVolver: () -> Unit
    ) {
    var correo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }
    var errorConfirmar by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var listo by remember { mutableStateOf(false) }

    fun recuperar() {
        errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo"
            !correo.contains("@") || !correo.contains(".") -> "Correo no valido (ej:nombre@correo.cl)"
            else -> null
        }
        errorNombre = if (nombre.isBlank()) "Ingresa tu nombre completo" else null
        errorClave = when {
            clave.isBlank() -> "Ingresa una contrasena"
            clave.length < 8 -> "Minimo 8 caracteres"
            clave.none { it.isLetter() } || clave.none { it.isDigit() } -> "Debe tener letras y numeros"
            else -> null
        }
        errorConfirmar = when {
            confirmar.isBlank() -> "Repite la contrasena"
            clave != confirmar -> "Las contrasenas no coinciden"
            else -> null
        }
        errorGeneral = null

        val hayErrores = listOf(errorCorreo, errorNombre, errorClave, errorConfirmar).any {
            it != null }
        if (hayErrores) return
        if (onRecuperarCuenta(correo.trim().lowercase(), nombre.trim(), clave)) {
            listo = true
        } else {
            errorGeneral = "Los datos no coinciden con ninguna cuenta"
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titulo_recuperar)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioGrande)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            if (listo) {
                Text(
                    text = stringResource(R.string.recuperar_exito),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                BotonPrincipal(
                    texto = stringResource(R.string.btn_volver_login),
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(R.string.recuperar_explicacion),
                    style = MaterialTheme.typography.bodyLarge
                )
                CampoTexto(
                    valor = correo,
                    onValorCambia = { correo = it; errorCorreo = null; errorGeneral = null
                    },
                    etiqueta = stringResource(R.string.campo_correo),
                    icono = R.drawable.ic_correo,
                    error = errorCorreo,
                    tipoTeclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = nombre,
                    onValorCambia = { nombre = it; errorNombre = null; errorGeneral = null
                    },
                    etiqueta = stringResource(R.string.campo_nombre),
                    icono = R.drawable.ic_persona,
                    error = errorNombre
                )
                CampoContrasena(
                    valor = clave,
                    onValorCambia = { clave = it; errorClave = null },
                    etiqueta = stringResource(R.string.campo_contrasena_nueva),
                    visible = visible,
                    onAlternarVisible = { visible = !visible },
                    error = errorClave
                )
                CampoContrasena(
                    valor = confirmar,
                    onValorCambia = { confirmar = it; errorConfirmar = null },
                    etiqueta = stringResource(R.string.campo_repetir_contrasena),
                    visible = visible,
                    onAlternarVisible = { visible = !visible },
                    error = errorConfirmar
                )
                if (errorGeneral != null) {
                    Text(
                        text = errorGeneral.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BotonPrincipal(
                    texto = stringResource(R.string.btn_cambiar_contrasena),
                    onClick = { recuperar() },
                    icono = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RecuperarPreview() {
    RinconSurTheme {
        RecuperarScreen(onRecuperarCuenta = { _, _, _ -> true }, onVolver = {})
    }
}