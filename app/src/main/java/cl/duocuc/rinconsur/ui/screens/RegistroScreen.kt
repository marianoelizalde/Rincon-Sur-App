package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.Estadisticas
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.RegistroUiState
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.TipoCompania
import cl.duocuc.rinconsur.nativo.Vibracion
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.GrupoChips
import cl.duocuc.rinconsur.ui.components.GrupoOpcionesIcono
import cl.duocuc.rinconsur.ui.components.InterruptorConTexto
import cl.duocuc.rinconsur.ui.styles.EstilosTarjeta
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.RegistroViewModel

// =====================================================================
//  RegistroScreen.kt  -  Registro de una persona: todo se elige con toques.
//  RegistroViewModel (formulario) valida y devuelve el RegistroObservacion;
//  RinconViewModel lo guarda en SQLite. Al guardar, el celular vibra (recurso nativo).
// =====================================================================
@Composable
fun RegistroScreen(
    sesion: SesionObservacion?,                        // RinconViewModel.sesionAbierta
    registros: List<RegistroObservacion>,              // RinconViewModel.registros
    accesos: List<Acceso>,                             // RinconViewModel.accesos
    onRegistrarPersona: (RegistroObservacion) -> Unit, // RinconViewModel.registrarPersona
    onDeshacerUltimo: () -> Unit,                      // RinconViewModel.deshacerUltimo
    alVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val validos = Estadisticas.registrosValidos(registros)

    RegistroContent(
        uiState = uiState,
        codigoAcceso = accesos.firstOrNull { it.id == sesion?.idAcceso }?.codigo.orEmpty(),
        enCurso = sesion?.estado == EstadoSesion.EN_CURSO,
        totalValidos = validos.size,
        totalTransito = validos.count { it.clasificacion == Clasificacion.TRANSITO },
        totalVisita = validos.count { it.clasificacion == Clasificacion.VISITA },
        onClasificacionSeleccionada = viewModel::onClasificacionSeleccionada,
        onRangoSeleccionado = viewModel::onRangoSeleccionado,
        onCompaniaSeleccionada = viewModel::onCompaniaSeleccionada,
        onCompraOtraTienda = viewModel::onCompraOtraTienda,
        onRegistrar = {
            sesion?.let { actual ->
                // 1) el formulario valida y devuelve el registro (null si falta algo)
                viewModel.onRegistrar(actual.id)?.let { registro ->
                    onRegistrarPersona(registro)       // 2) RinconViewModel lo guarda en SQLite
                    Vibracion.vibrar(contexto)         // 3) recurso nativo: vibracion
                }
            }
        },
        onDeshacerUltimo = onDeshacerUltimo,
        onVolver = alVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroContent(
    uiState: RegistroUiState,
    codigoAcceso: String,
    enCurso: Boolean,
    totalValidos: Int,
    totalTransito: Int,
    totalVisita: Int,
    onClasificacionSeleccionada: (Clasificacion) -> Unit,
    onRangoSeleccionado: (RangoEtario) -> Unit,
    onCompaniaSeleccionada: (TipoCompania) -> Unit,
    onCompraOtraTienda: (Boolean) -> Unit,
    onRegistrar: () -> Unit,
    onDeshacerUltimo: () -> Unit,
    onVolver: () -> Unit
) {
    val pideDetalle = uiState.clasificacion != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_registro, codigoAcceso)) },
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
            // Contador: el numero se anima cada vez que se registra una persona.
            Card(
                shape = EstilosTarjeta.forma,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.espacioMedio),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.espacioGrande),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState = totalValidos,
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                (slideOutVertically { -it } + fadeOut())
                        },
                        label = "contador"
                    ) { cantidad ->
                        Text(
                            text = "$cantidad",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.registro_personas),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.registro_resumen, totalTransito, totalVisita),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (!enCurso) {
                Text(
                    text = stringResource(R.string.registro_no_en_curso),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                GrupoOpcionesIcono(
                    titulo = stringResource(R.string.etiqueta_clasificacion),
                    opciones = Clasificacion.entries,
                    seleccionada = uiState.clasificacion,
                    onSeleccionar = onClasificacionSeleccionada,
                    textoDe = { it.etiqueta },
                    iconoDe = {
                        when (it) {
                            Clasificacion.TRANSITO -> R.drawable.ic_transito
                            Clasificacion.VISITA -> R.drawable.ic_visita
                        }
                    },
                    error = uiState.errorClasificacion
                )

                AnimatedVisibility(visible = pideDetalle) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)) {
                        GrupoChips(
                            titulo = stringResource(R.string.etiqueta_rango),
                            opciones = RangoEtario.entries,
                            seleccionada = uiState.rango,
                            onSeleccionar = onRangoSeleccionado,
                            textoDe = { it.etiqueta },
                            error = uiState.errorRango
                        )
                        GrupoOpcionesIcono(
                            titulo = stringResource(R.string.etiqueta_compania),
                            opciones = TipoCompania.entries,
                            seleccionada = uiState.compania,
                            onSeleccionar = onCompaniaSeleccionada,
                            textoDe = { it.etiqueta },
                            iconoDe = {
                                when (it) {
                                    TipoCompania.SOLO -> R.drawable.ic_solo
                                    TipoCompania.PAREJA -> R.drawable.ic_pareja
                                    TipoCompania.CON_NINOS -> R.drawable.ic_con_ninos
                                    TipoCompania.GRUPO -> R.drawable.ic_grupo
                                }
                            },
                            error = uiState.errorCompania
                        )
                        InterruptorConTexto(
                            texto = stringResource(R.string.opcion_compra_otra_tienda),
                            activado = uiState.compraOtraTienda,
                            onCambio = onCompraOtraTienda
                        )
                    }
                }


                BotonPrincipal(
                    texto = stringResource(R.string.btn_registrar_persona),
                    onClick = onRegistrar,
                    icono = R.drawable.ic_check,
                    modifier = Modifier.fillMaxWidth()
                )
                BotonSecundario(
                    texto = stringResource(R.string.btn_deshacer_ultimo),
                    onClick = onDeshacerUltimo,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1100, name = "En curso")
@Composable
private fun RegistroEnCursoPreview() {
    RinconSurTheme {
        RegistroContent(
            uiState = RegistroUiState(clasificacion = Clasificacion.TRANSITO),
            codigoAcceso = "A1",
            enCurso = true,
            totalValidos = 12, totalTransito = 7, totalVisita = 5,
            onClasificacionSeleccionada = {}, onRangoSeleccionado = {}, onCompaniaSeleccionada = {},
            onCompraOtraTienda = {}, onRegistrar = {},
            onDeshacerUltimo = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, name = "Sesion no iniciada")
@Composable
private fun RegistroNoEnCursoPreview() {
    RinconSurTheme {
        RegistroContent(
            uiState = RegistroUiState(),
            codigoAcceso = "A1",
            enCurso = false,
            totalValidos = 0, totalTransito = 0, totalVisita = 0,
            onClasificacionSeleccionada = {}, onRangoSeleccionado = {}, onCompaniaSeleccionada = {},
            onCompraOtraTienda = {}, onRegistrar = {},
            onDeshacerUltimo = {}, onVolver = {}
        )
    }
}
