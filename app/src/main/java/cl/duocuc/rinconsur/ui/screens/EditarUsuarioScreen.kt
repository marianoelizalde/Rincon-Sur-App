package cl.duocuc.rinconsur.ui.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EditarUsuarioUiState
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.EditarUsuarioViewModel

@Composable
fun EditarUsuarioScreen(
    usuario: Colaborador,
    onRutEnUso: (String, Int) -> Boolean,
    onEditarUsuario: (Int, String, String, String, String) -> String?,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
    viewModel: EditarUsuarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Llena el formulario con los datos actuales del usuario
    LaunchedEffect(usuario.id) { viewModel.cargar(usuario) }

    EditarUsuarioContent(
        uiState = uiState,
        onNombreCambia = viewModel::onNombreCambia,
        onRutCambia = viewModel::onRutCambia,
        onCargoCambia = viewModel::onCargoCambia,
        onTelefonoCambia = viewModel::onTelefonoCambia,
        onGuardar = {
            if (viewModel.onGuardar(onRutEnUso, onEditarUsuario)) onGuardado()
        },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioContent(
    uiState: EditarUsuarioUiState,
    onNombreCambia: (String) -> Unit,
    onRutCambia: (String) -> Unit,
    onCargoCambia: (String) -> Unit,
    onTelefonoCambia: (String) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_editar_usuario)) },
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
                .padding(padding)
                .padding(Dimens.espacioPantalla)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = stringResource(R.string.seccion_datos_cuenta)) {
                CampoTexto(
                    valor = uiState.nombre,
                    onValorCambia = onNombreCambia,
                    etiqueta = stringResource(R.string.campo_nombre),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorNombre
                )
                CampoTexto(
                    valor = uiState.rut,
                    onValorCambia = onRutCambia,
                    etiqueta = stringResource(R.string.campo_rut),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorRut
                )
                CampoTexto(
                    valor = uiState.cargo,
                    onValorCambia = onCargoCambia,
                    etiqueta = stringResource(R.string.campo_cargo),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorCargo
                )
                CampoTexto(
                    valor = uiState.telefono,
                    onValorCambia = onTelefonoCambia,
                    etiqueta = stringResource(R.string.campo_telefono),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorTelefono,
                    tipoTeclado = KeyboardType.Phone
                )
            }

            if (uiState.errorGeneral != null) {
                Text(
                    text = uiState.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BotonPrincipal(
                texto = stringResource(R.string.btn_guardar_cambios),
                onClick = onGuardar,
                icono = R.drawable.ic_check,
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = stringResource(R.string.btn_cancelar),
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EditarUsuarioPreview() {
    RinconSurTheme {
        EditarUsuarioContent(
            uiState = EditarUsuarioUiState(
                idUsuario = 7,
                nombre = "Lukas Llanten",
                rut = "18765432-7",
                cargo = "Observador de acceso",
                telefono = "987654321"
            ),
            onNombreCambia = {}, onRutCambia = {}, onCargoCambia = {}, onTelefonoCambia = {},
            onGuardar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "Con errores")
@Composable
private fun EditarUsuarioConErroresPreview() {
    RinconSurTheme {
        EditarUsuarioContent(
            uiState = EditarUsuarioUiState(
                idUsuario = 7,
                nombre = "Lu",
                rut = "12345678-9",
                telefono = "123",
                errorNombre = "Minimo 3 caracteres",
                errorRut = "Ese RUT ya pertenece a otro usuario",
                errorCargo = "Ingresa el cargo",
                errorTelefono = "El telefono debe tener 9 digitos"
            ),
            onNombreCambia = {}, onRutCambia = {}, onCargoCambia = {}, onTelefonoCambia = {},
            onGuardar = {}, onVolver = {}
        )
    }
}