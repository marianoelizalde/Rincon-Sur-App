package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
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
import cl.duocuc.rinconsur.ui.components.GrupoChips
import cl.duocuc.rinconsur.ui.components.SelectorPaso
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.ProgramarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
        onHoraCambiaEn = viewModel::onHoraCambiaEn,
        onMinutosCambiaEn = viewModel::onMinutosCambiaEn,
        onDuracionCambiaEn = viewModel::onDuracionCambiaEn,
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
    onHoraCambiaEn: (Int) -> Unit,
    onMinutosCambiaEn: (Int) -> Unit,
    onDuracionCambiaEn: (Int) -> Unit,
    onProgramar: () -> Unit,
    onVolver: () -> Unit
) {
    val fechas = remember { proximasFechas() }
    val posicionFecha = fechas.indexOf(uiState.fecha).coerceAtLeast(0)
    val duracion = uiState.duracion ?: 45
    val errorHorario = uiState.errorFecha ?: uiState.errorInicio ?: uiState.errorFin

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
                SelectorPaso(
                    titulo = stringResource(R.string.campo_fecha),
                    valor = when (posicionFecha) {
                        0 -> "Hoy"
                        1 -> "Mañana"
                        else -> fechas[posicionFecha].substring(8, 10) + "/" +
                                fechas[posicionFecha].substring(5, 7)
                    },
                    onMenos = { onFechaCambia(fechas[posicionFecha - 1]) },
                    onMas = { onFechaCambia(fechas[posicionFecha + 1]) },
                    menosHabilitado = posicionFecha > 0,
                    masHabilitado = posicionFecha < fechas.lastIndex,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.campo_hora_inicio),
                    style = MaterialTheme.typography.titleSmall
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    SelectorPaso(
                        titulo = "Hora",
                        valor = uiState.horaInicio.take(2),
                        onMenos = { onHoraCambiaEn(-1) },
                        onMas = { onHoraCambiaEn(1) },
                        modifier = Modifier.weight(1f)
                    )
                    SelectorPaso(
                        titulo = "Minutos",
                        valor = uiState.horaInicio.takeLast(2),
                        onMenos = { onMinutosCambiaEn(-1) },
                        onMas = { onMinutosCambiaEn(1) },
                        modifier = Modifier.weight(1f)
                    )
                }

                SelectorPaso(
                    titulo = stringResource(R.string.campo_duracion),
                    valor = "$duracion min",
                    onMenos = { onDuracionCambiaEn(-1) },
                    onMas = { onDuracionCambiaEn(1) },
                    menosHabilitado = duracion > 30,
                    masHabilitado = duracion < 60,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.horaFin.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.texto_hora_fin, uiState.horaFin),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (errorHorario != null) {
                    Text(
                        text = errorHorario,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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

// Los proximos 7 dias en formato AAAA-MM-DD (el primero es hoy)
private fun proximasFechas(dias: Int = 7): List<String> {
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val calendario = Calendar.getInstance()
    return List(dias) {
        val fecha = formato.format(calendario.time)
        calendario.add(Calendar.DAY_OF_YEAR, 1)
        fecha
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ProgramarVacioPreview() {
    RinconSurTheme {
        ProgramarContent(
            uiState = ProgramarUiState(horaInicio = "10:00", duracion = 45, horaFin = "10:45"),
            accesos = DatosEjemplo.accesos,
            observadores = DatosEjemplo.colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR },
            onAccesoSeleccionado = {}, onObservadorSeleccionado = {}, onFechaCambia = {},
            onHoraCambiaEn = {}, onMinutosCambiaEn = {}, onDuracionCambiaEn = {},
            onProgramar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1200, name = "Con errores")
@Composable
private fun ProgramarConErroresPreview() {
    RinconSurTheme {
        ProgramarContent(
            uiState = ProgramarUiState(
                horaInicio = "20:45",
                duracion = 60,
                horaFin = "21:45",
                errorAcceso = "Elige un acceso",
                errorObservador = "Elige un observador",
                errorFin = "La tienda cierra a las 21:00",
                errorGeneral = "Ya hay una sesion en ese acceso y horario"
            ),
            accesos = DatosEjemplo.accesos,
            observadores = DatosEjemplo.colaboradores.filter { it.perfil == PerfilUsuario.OBSERVADOR },
            onAccesoSeleccionado = {}, onObservadorSeleccionado = {}, onFechaCambia = {},
            onHoraCambiaEn = {}, onMinutosCambiaEn = {}, onDuracionCambiaEn = {},
            onProgramar = {}, onVolver = {}
        )
    }
}