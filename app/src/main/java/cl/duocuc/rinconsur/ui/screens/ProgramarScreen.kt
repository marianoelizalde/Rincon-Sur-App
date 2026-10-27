package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.ProgramarUiState
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.components.GrupoChips
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.ProgramarViewModel


@Composable
fun ProgramarScreen(
    accesos: List<Acceso>,
    observadores: List<Colaborador>,
    onProgramarSesion: (SesionObservacion) -> Boolean,
    onSesionProgramada: () -> Unit,
    onVolver: () -> Unit,
    viewModel: ProgramarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProgramarContent(
        uiState = uiState,
        accesos = accesos,
        observadores = observadores,
        onAccesoSeleccionado = viewModel::onAccesoSeleccionado,
        onObservadorSeleccionado = viewModel::onObservadorSeleccionado,
        onFechaCambia = viewModel::onFechaCambia,
        onHoraInicioCambia = viewModel::onHoraInicioCambia,
        onHoraFinCambia = viewModel::onHoraFinCambia,
        onProgramar = {

            viewModel.onProgramar()?.let { sesion ->

                if (onProgramarSesion(sesion)) onSesionProgramada() else viewModel.onSolapamiento()
            }
        },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramarContent(
    uiState: ProgramarUiState,
    accesos: List<Acceso>,
    observadores: List<Colaborador>,
    onAccesoSeleccionado: (Int) -> Unit,
    onObservadorSeleccionado: (Int) -> Unit,
    onFechaCambia: (String) -> Unit,
    onHoraInicioCambia: (String) -> Unit,
    onHoraFinCambia: (String) -> Unit,
    onProgramar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_programar)) },
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
                .padding(Dimens.espacioMedio)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            TarjetaSeccion(titulo = stringResource(R.string.seccion_donde_quien)) {
                GrupoChips(
                    titulo = stringResource(R.string.etiqueta_acceso),
                    opciones = accesos,
                    seleccionada = accesos.firstOrNull { it.id == uiState.idAcceso },
                    onSeleccionar = { onAccesoSeleccionado(it.id) },
                    textoDe = { "${it.codigo} ${it.nombre}" },
                    error = uiState.errorAcceso
                )
                GrupoChips(
                    titulo = stringResource(R.string.etiqueta_observador),
                    opciones = observadores,
                    seleccionada = observadores.firstOrNull { it.id == uiState.idObservador },
                    onSeleccionar = { onObservadorSeleccionado(it.id) },
                    textoDe = { it.nombre },
                    error = uiState.errorObservador
                )
            }

            TarjetaSeccion(titulo = stringResource(R.string.seccion_cuando)) {
                CampoTexto(
                    valor = uiState.fecha,
                    onValorCambia = onFechaCambia,
                    etiqueta = stringResource(R.string.campo_fecha),
                    icono = R.drawable.ic_calendario,
                    error = uiState.errorFecha,
                    ayuda = stringResource(R.string.ayuda_fecha)
                )
                CampoTexto(
                    valor = uiState.horaInicio,
                    onValorCambia = onHoraInicioCambia,
                    etiqueta = stringResource(R.string.campo_hora_inicio),
                    icono = R.drawable.ic_reloj,
                    error = uiState.errorInicio,
                    ayuda = stringResource(R.string.ayuda_hora_inicio)
                )
                CampoTexto(
                    valor = uiState.horaFin,
                    onValorCambia = onHoraFinCambia,
                    etiqueta = stringResource(R.string.campo_hora_fin),
                    icono = R.drawable.ic_reloj,
                    error = uiState.errorFin,
                    ayuda = stringResource(R.string.ayuda_hora_fin)
                )
            }

            AnimatedVisibility(visible = uiState.errorGeneral != null) {
                Text(
                    text = uiState.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BotonPrincipal(
                texto = stringResource(R.string.btn_programar_sesion),
                onClick = onProgramar,
                icono = R.drawable.ic_check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ProgramarVacioPreview() {
    RinconSurTheme {
        ProgramarContent(
            uiState = ProgramarUiState(),
            accesos = DatosEjemplo.accesos,
            observadores = DatosEjemplo.colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR },
            onAccesoSeleccionado = {}, onObservadorSeleccionado = {}, onFechaCambia = {},
            onHoraInicioCambia = {}, onHoraFinCambia = {}, onProgramar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1200, name = "Con errores")
@Composable
private fun ProgramarConErroresPreview() {
    RinconSurTheme {
        ProgramarContent(
            uiState = ProgramarUiState(
                fecha = "15/09",
                horaInicio = "09:00",
                errorAcceso = "Elige un acceso",
                errorObservador = "Elige un observador",
                errorFecha = "Usa el formato AAAA-MM-DD",
                errorFin = "Ingresa la hora",
                errorGeneral = "Ya hay una sesion en ese acceso y horario"
            ),
            accesos = DatosEjemplo.accesos,
            observadores = DatosEjemplo.colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR },
            onAccesoSeleccionado = {}, onObservadorSeleccionado = {}, onFechaCambia = {},
            onHoraInicioCambia = {}, onHoraFinCambia = {}, onProgramar = {}, onVolver = {}
        )
    }
}
