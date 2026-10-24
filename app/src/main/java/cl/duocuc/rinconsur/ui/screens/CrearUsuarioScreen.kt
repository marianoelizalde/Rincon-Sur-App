package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.CrearUsuarioUiState
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.CampoContrasena
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.components.CasillaConTexto
import cl.duocuc.rinconsur.ui.components.GrupoChips
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.CrearUsuarioViewModel

// Formulario para crear una cuenta. Solo el Supervisor llega aqui (desde el menu lateral).
// Igual que FormularioScreen del profesor: el ViewModel de formulario valida y devuelve el
// Colaborador; RinconViewModel (que llega como onCrearUsuario) lo guarda en SQLite.
@Composable
fun CrearUsuarioScreen(
    onCrearUsuario: (Colaborador) -> Boolean,   // RinconViewModel.registrarUsuario
    onUsuarioCreado: () -> Unit,
    onVolver: () -> Unit,
    viewModel: CrearUsuarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CrearUsuarioContent(
        uiState = uiState,
        onNombreCambia = viewModel::onNombreCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onClaveCambia = viewModel::onClaveCambia,
        onConfirmarCambia = viewModel::onConfirmarCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onPerfilSeleccionado = viewModel::onPerfilSeleccionado,
        onTerminosCambia = viewModel::onTerminosCambia,
        onCrear = {
            // 1) el ViewModel de formulario valida y devuelve el Colaborador (o null si hay errores)
            viewModel.onCrear()?.let { colaborador ->
                // 2) RinconViewModel lo guarda; devuelve false si el correo ya existe
                if (onCrearUsuario(colaborador)) onUsuarioCreado() else viewModel.onCorreoRepetido()
            }
        },
        onLimpiar = viewModel::onLimpiar,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioContent(
    uiState: CrearUsuarioUiState,
    onNombreCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onClaveCambia: (String) -> Unit,
    onConfirmarCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onPerfilSeleccionado: (PerfilUsuario) -> Unit,
    onTerminosCambia: (Boolean) -> Unit,
    onCrear: () -> Unit,
    onLimpiar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_crear_usuario)) },
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
            Text(
                text = stringResource(R.string.crear_intro),
                style = MaterialTheme.typography.bodyLarge
            )

            TarjetaSeccion(titulo = stringResource(R.string.seccion_datos_cuenta)) {
                CampoTexto(
                    valor = uiState.nombre,
                    onValorCambia = onNombreCambia,
                    etiqueta = stringResource(R.string.campo_nombre),
                    icono = R.drawable.ic_persona,
                    error = uiState.errorNombre
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
                    error = uiState.errorClave,
                    ayuda = stringResource(R.string.ayuda_contrasena)
                )
                CampoContrasena(
                    valor = uiState.confirmar,
                    onValorCambia = onConfirmarCambia,
                    etiqueta = stringResource(R.string.campo_repetir_contrasena),
                    visible = uiState.claveVisible,
                    onAlternarVisible = onAlternarVisibilidad,
                    error = uiState.errorConfirmar
                )
            }

            TarjetaSeccion(titulo = stringResource(R.string.seccion_rol)) {
                GrupoChips(
                    titulo = stringResource(R.string.etiqueta_perfil),
                    opciones = PerfilUsuario.entries.toList(),
                    seleccionada = uiState.perfil,
                    onSeleccionar = onPerfilSeleccionado,
                    textoDe = { it.etiqueta }
                )
            }

            Column {
                CasillaConTexto(
                    texto = stringResource(R.string.opcion_terminos),
                    marcado = uiState.aceptaTerminos,
                    onCambio = onTerminosCambia
                )
                AnimatedVisibility(visible = uiState.errorTerminos != null) {
                    Text(
                        text = uiState.errorTerminos.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = Dimens.espacioChico)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
                BotonSecundario(
                    texto = stringResource(R.string.btn_limpiar),
                    onClick = onLimpiar,
                    modifier = Modifier.weight(1f)
                )
                BotonPrincipal(
                    texto = stringResource(R.string.btn_crear_cuenta),
                    onClick = onCrear,
                    icono = R.drawable.ic_persona_agregar,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CrearUsuarioVacioPreview() {
    RinconSurTheme {
        CrearUsuarioContent(
            uiState = CrearUsuarioUiState(),
            onNombreCambia = {}, onCorreoCambia = {}, onClaveCambia = {}, onConfirmarCambia = {},
            onAlternarVisibilidad = {}, onPerfilSeleccionado = {}, onTerminosCambia = {},
            onCrear = {}, onLimpiar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1200, name = "Con errores")
@Composable
private fun CrearUsuarioConErroresPreview() {
    RinconSurTheme {
        CrearUsuarioContent(
            uiState = CrearUsuarioUiState(
                nombre = "Al",
                correo = "ana@",
                errorNombre = "Minimo 3 caracteres",
                errorCorreo = "Correo no valido (ej: nombre@correo.cl)",
                errorClave = "Ingresa una contrasena",
                errorConfirmar = "Repite la contrasena",
                errorTerminos = "Debes aceptar para crear la cuenta"
            ),
            onNombreCambia = {}, onCorreoCambia = {}, onClaveCambia = {}, onConfirmarCambia = {},
            onAlternarVisibilidad = {}, onPerfilSeleccionado = {}, onTerminosCambia = {},
            onCrear = {}, onLimpiar = {}, onVolver = {}
        )
    }
}