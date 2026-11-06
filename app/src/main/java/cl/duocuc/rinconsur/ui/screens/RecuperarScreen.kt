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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.model.RecuperarUiState
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.CampoContrasena
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.RecuperarViewModel

@Composable
fun RecuperarScreen(
    onRecuperarCuenta: (String, String, String) -> Boolean,
    onVolver: () -> Unit,
    viewModel: RecuperarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecuperarContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onRutCambia = viewModel::onRutCambia,
        onClaveCambia = viewModel::onClaveCambia,
        onConfirmarCambia = viewModel::onConfirmarCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onRecuperar = { viewModel.onRecuperar(onRecuperarCuenta) },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarContent(
    uiState: RecuperarUiState,
    onCorreoCambia: (String) -> Unit,
    onRutCambia: (String) -> Unit,
    onClaveCambia: (String) -> Unit,
    onConfirmarCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onRecuperar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_recuperar)) },
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
            if (uiState.listo) {
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
                    valor = uiState.correo,
                    onValorCambia = onCorreoCambia,
                    etiqueta = stringResource(R.string.campo_correo),
                    icono = R.drawable.ic_correo,
                    error = uiState.errorCorreo,
                    tipoTeclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = uiState.rut,
                    onValorCambia = onRutCambia,
                    etiqueta = stringResource(R.string.campo_rut),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorRut
                )
                CampoContrasena(
                    valor = uiState.clave,
                    onValorCambia = onClaveCambia,
                    etiqueta = stringResource(R.string.campo_contrasena_nueva),
                    visible = uiState.claveVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorClave
                )
                CampoContrasena(
                    valor = uiState.confirmar,
                    onValorCambia = onConfirmarCambia,
                    etiqueta = stringResource(R.string.campo_repetir_contrasena),
                    visible = uiState.claveVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorConfirmar
                )
                if (uiState.errorGeneral != null) {
                    Text(
                        text = uiState.errorGeneral.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BotonPrincipal(
                    texto = stringResource(R.string.btn_cambiar_contrasena),
                    onClick = onRecuperar,
                    icono = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RecuperarVacioPreview() {
    RinconSurTheme {
        RecuperarContent(
            uiState = RecuperarUiState(),
            onCorreoCambia = {}, onRutCambia = {}, onClaveCambia = {}, onConfirmarCambia = {},
            onAlternarVisibilidad = {}, onRecuperar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "Con errores")
@Composable
private fun RecuperarConErroresPreview() {
    RinconSurTheme {
        RecuperarContent(
            uiState = RecuperarUiState(
                correo = "ana@",
                rut = "123",
                errorCorreo = "Correo no valido (ej: nombre@correo.cl)",
                errorRut = "RUT no valido (ej: 12345678-5)",
                errorClave = "Ingresa una contrasena",
                errorConfirmar = "Repite la contrasena",
                errorGeneral = "Los datos no coinciden con ninguna cuenta"
            ),
            onCorreoCambia = {}, onRutCambia = {}, onClaveCambia = {}, onConfirmarCambia = {},
            onAlternarVisibilidad = {}, onRecuperar = {}, onVolver = {}
        )
    }
}