package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.model.LoginUiState
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.CampoContrasena
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.LoginViewModel
import androidx.compose.material3.TextButton
// =====================================================================
//  LoginScreen.kt  -  Pantalla principal: iniciar sesion (las cuentas las crea el Supervisor).
//  Se separa en DOS funciones (igual que FormularioScreen del profesor):
//   - LoginScreen  : lee el estado del ViewModel y conecta los eventos.
//   - LoginContent : solo dibuja lo que recibe -> se puede ver en @Preview.
//  Quien revisa si el correo y la clave existen es RinconViewModel, que llega
//  como la funcion onIniciarSesion (la pasa AppNavegacion).
// =====================================================================
@Composable
fun LoginScreen(
    onIniciarSesion: (String, String) -> Boolean,   // RinconViewModel.iniciarSesion(correo, clave)
    onLoginExitoso: () -> Unit,
    onRecuperar: () -> Unit,
    viewModel: LoginViewModel = viewModel()          // se crea una vez y sobrevive a la rotacion
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onClaveCambia = viewModel::onClaveCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onRecuperar = onRecuperar,
        onIngresar = {
            // 1) el ViewModel de formulario revisa que los campos esten bien escritos
            if (viewModel.onIngresar()) {
                // 2) RinconViewModel revisa en SQLite que el correo y la clave existan
                if (onIniciarSesion(uiState.correo.lowercase(), uiState.clave)) {
                    onLoginExitoso()
                } else {
                    viewModel.onCredencialesIncorrectas()
                }
            }
        }
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoCambia: (String) -> Unit,
    onClaveCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onIngresar: () -> Unit,
    onRecuperar: () -> Unit
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioGrande)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioPantalla)
        ) {
            Spacer(Modifier.height(Dimens.espacioGrande))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.login_subtitulo),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.login_titulo),
                style = MaterialTheme.typography.titleLarge
            )

            CampoTexto(
                valor = uiState.correo,
                onValorCambia = onCorreoCambia,
                etiqueta = stringResource(R.string.campo_correo),
                icono = R.drawable.ic_correo,
                error = uiState.errorCorreo,
                tipoTeclado = KeyboardType.Email
            )
            CampoContrasena(
                valor = uiState.clave,
                onValorCambia = onClaveCambia,
                etiqueta = stringResource(R.string.campo_contrasena),
                visible = uiState.claveVisible,
                onAlternarVisible = onAlternarVisibilidad,
                error = uiState.errorClave
            )

            // Aviso cuando el correo o la contrasena no coinciden con ninguna cuenta.
            AnimatedVisibility(visible = uiState.errorGeneral != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_error),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(Dimens.tamanoIcono)
                    )
                    Text(
                        text = uiState.errorGeneral.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = Dimens.espacioChico)
                    )
                }
            }

            BotonPrincipal(
                texto = stringResource(R.string.btn_ingresar),
                onClick = onIngresar,
                icono = R.drawable.ic_ingresar,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = onRecuperar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login_olvido))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginVacioPreview() {
    RinconSurTheme {
        LoginContent(
            uiState = LoginUiState(),
            onCorreoCambia = {}, onClaveCambia = {}, onAlternarVisibilidad = {},
            onIngresar = {}, onRecuperar = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Con errores")
@Composable
private fun LoginConErroresPreview() {
    RinconSurTheme {
        LoginContent(
            uiState = LoginUiState(
                correo = "daniela@",
                errorCorreo = "Correo no valido (ej: nombre@correo.cl)",
                errorClave = "Ingresa tu contrasena",
                errorGeneral = "Correo o contrasena incorrectos"
            ),
            onCorreoCambia = {}, onClaveCambia = {}, onAlternarVisibilidad = {},
            onIngresar = {}, onRecuperar = {}
        )
    }
}