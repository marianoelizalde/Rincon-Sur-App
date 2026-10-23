package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.Estadisticas
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.UsuarioActual
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.components.EtiquetaEstado
import cl.duocuc.rinconsur.ui.styles.EstilosTarjeta
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme

// =====================================================================
//  SesionScreen.kt  -  Detalle de una sesion: estadisticas, ciclo de vida
//  (iniciar, finalizar, validar, rechazar) y ultimos registros.
//  Sin ViewModel propio: todo llega de RinconViewModel por lambdas (AppNavegacion).
// =====================================================================
@Composable
fun SesionScreen(
    usuario: UsuarioActual?,                          // RinconViewModel.usuarioActual
    sesion: SesionObservacion?,                       // RinconViewModel.sesionAbierta
    registros: List<RegistroObservacion>,             // RinconViewModel.registros
    accesos: List<Acceso>,                            // RinconViewModel.accesos
    nombres: Map<Int, String>,                        // RinconViewModel.nombres
    mensaje: String?,                                 // RinconViewModel.mensaje
    onMensajeMostrado: () -> Unit,                    // RinconViewModel.limpiarMensaje
    onIniciar: () -> Unit,
    onFinalizar: () -> Unit,
    onCancelar: () -> Unit,
    onValidar: (Int) -> Unit,                         // recibe el id de quien valida
    onRechazar: (Int, String) -> String?,             // id de quien rechaza + motivo; devuelve error o null
    onAnularRegistro: (RegistroObservacion) -> Unit,
    alVolver: () -> Unit,
    alIrARegistro: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    val idUsuario = usuario?.colaborador?.id

    SesionContent(
        sesion = sesion,
        acceso = accesos.firstOrNull { it.id == sesion?.idAcceso },
        nombres = nombres,
        validos = Estadisticas.registrosValidos(registros),
        porcentajeTransito = Estadisticas.porcentajeTransito(registros),
        esDuenio = sesion != null && idUsuario == sesion.idObservador,
        esSupervisor = usuario?.colaborador?.perfil == PerfilUsuario.SUPERVISOR,
        snackbarHostState = snackbarHostState,
        onIniciar = onIniciar,
        onRegistrar = { sesion?.let { alIrARegistro(it.id) } },
        onFinalizar = onFinalizar,
        onCancelar = onCancelar,
        onValidar = { idUsuario?.let { onValidar(it) } },
        onRechazar = { motivo -> idUsuario?.let { onRechazar(it, motivo) } },
        onAnularRegistro = onAnularRegistro,
        onVolver = alVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionContent(
    sesion: SesionObservacion?,
    acceso: Acceso?,
    nombres: Map<Int, String>,
    validos: List<RegistroObservacion>,
    porcentajeTransito: Double?,
    esDuenio: Boolean,
    esSupervisor: Boolean,
    onIniciar: () -> Unit,
    onRegistrar: () -> Unit,
    onFinalizar: () -> Unit,
    onCancelar: () -> Unit,
    onValidar: () -> Unit,
    onRechazar: (String) -> String?,          // devuelve el error del motivo, o null si se rechazo bien
    onAnularRegistro: (RegistroObservacion) -> Unit,
    onVolver: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var mostrarRechazo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalle_sesion)) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (sesion == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val progreso by animateFloatAsState(
            targetValue = (validos.size / Estadisticas.MINIMO_REGISTROS_VALIDOS.toFloat()).coerceAtMost(1f),
            label = "progresoMuestra"
        )

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioItem)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = EstilosTarjeta.forma,
                    colors = EstilosTarjeta.colores(),
                    elevation = EstilosTarjeta.elevacion()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioFino)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.sesion_acceso, acceso?.codigo ?: "?", acceso?.nombre ?: ""),
                                style = MaterialTheme.typography.titleLarge
                            )
                            EtiquetaEstado(sesion.estado)
                        }
                        Text(stringResource(R.string.sesion_horario, sesion.fecha, sesion.horaInicio, sesion.horaFin))
                        Text(stringResource(R.string.sesion_observador, nombres[sesion.idObservador] ?: "-"))
                        sesion.idValidador?.let {
                            Text(stringResource(R.string.sesion_validador, nombres[it] ?: "-"))
                        }
                        sesion.motivoRechazo?.let {
                            Text(
                                text = stringResource(R.string.sesion_motivo, it),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = EstilosTarjeta.forma,
                    colors = EstilosTarjeta.colores(),
                    elevation = EstilosTarjeta.elevacion()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                    ) {
                        Text(
                            stringResource(
                                R.string.sesion_muestra,
                                validos.size,
                                Estadisticas.MINIMO_REGISTROS_VALIDOS
                            )
                        )
                        LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
                        Text(
                            text = porcentajeTransito
                                ?.let { stringResource(R.string.sesion_porcentaje, "%.1f".format(it)) }
                                ?: stringResource(R.string.sesion_porcentaje_sin_datos),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            item {
                Acciones(
                    sesion = sesion,
                    esDuenio = esDuenio,
                    esSupervisor = esSupervisor,
                    onIniciar = onIniciar,
                    onRegistrar = onRegistrar,
                    onFinalizar = onFinalizar,
                    onCancelar = onCancelar,
                    onValidar = onValidar,
                    onRechazar = { mostrarRechazo = true }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.sesion_ultimos_registros),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(validos.takeLast(15).reversed(), key = { it.id }) { registro ->
                FilaRegistro(
                    registro = registro,
                    puedeAnular = esDuenio && sesion.estado == EstadoSesion.EN_CURSO,
                    onAnular = { onAnularRegistro(registro) }
                )
            }
        }

        if (mostrarRechazo) {
            DialogoRechazo(
                onCancelar = { mostrarRechazo = false },
                onConfirmar = { motivo ->
                    val error = onRechazar(motivo)
                    if (error == null) mostrarRechazo = false
                    error
                }
            )
        }
    }
}

@Composable
private fun Acciones(
    sesion: SesionObservacion,
    esDuenio: Boolean,
    esSupervisor: Boolean,
    onIniciar: () -> Unit,
    onRegistrar: () -> Unit,
    onFinalizar: () -> Unit,
    onCancelar: () -> Unit,
    onValidar: () -> Unit,
    onRechazar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico), modifier = Modifier.fillMaxWidth()) {
        when (sesion.estado) {
            EstadoSesion.PROGRAMADA -> {
                if (esDuenio) {
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_iniciar_sesion),
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (esSupervisor) {
                    BotonSecundario(
                        texto = stringResource(R.string.btn_cancelar_sesion),
                        onClick = onCancelar,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            EstadoSesion.EN_CURSO -> {
                if (esDuenio) {
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_registrar_personas),
                        onClick = onRegistrar,
                        icono = R.drawable.ic_persona_agregar,
                        modifier = Modifier.fillMaxWidth()
                    )
                    BotonSecundario(
                        texto = stringResource(R.string.btn_finalizar_sesion),
                        onClick = onFinalizar,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            EstadoSesion.FINALIZADA -> {
                if (esSupervisor) {
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_validar_sesion),
                        onClick = onValidar,
                        icono = R.drawable.ic_check,
                        modifier = Modifier.fillMaxWidth()
                    )
                    BotonSecundario(
                        texto = stringResource(R.string.btn_rechazar_sesion),
                        onClick = onRechazar,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(stringResource(R.string.sesion_esperando_validacion))
                }
            }
            else -> Text(stringResource(R.string.sesion_cerrada))
        }
    }
}

@Composable
private fun FilaRegistro(registro: RegistroObservacion, puedeAnular: Boolean, onAnular: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(
                R.string.registro_fila,
                registro.hora,
                registro.clasificacion.etiqueta,
                registro.rangoEtario?.etiqueta ?: "-",
                registro.tipoCompania?.etiqueta ?: "-"
            ),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        if (puedeAnular) {
            IconButton(onClick = onAnular) {
                Icon(
                    painter = painterResource(R.drawable.ic_cerrar),
                    contentDescription = stringResource(R.string.cd_anular_registro),
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        }
    }
}

// Dialogo con un campo validado. onConfirmar devuelve el error, o null si todo salio bien.
@Composable
private fun DialogoRechazo(onCancelar: () -> Unit, onConfirmar: (String) -> String?) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.btn_rechazar_sesion)) },
        text = {
            CampoTexto(
                valor = motivo,
                onValorCambia = { motivo = it; error = null },
                etiqueta = stringResource(R.string.campo_motivo),
                icono = R.drawable.ic_editar,
                error = error
            )
        },
        confirmButton = {
            TextButton(onClick = { error = onConfirmar(motivo) }) {
                Text(stringResource(R.string.btn_rechazar))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(stringResource(R.string.btn_cancelar)) }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Finalizada (supervisor)")
@Composable
private fun SesionFinalizadaPreview() {
    val sesion = DatosEjemplo.sesiones.first().copy(estado = EstadoSesion.FINALIZADA)
    val registros = DatosEjemplo.registros().filter { it.idSesion == sesion.id && !it.anulado }
    RinconSurTheme {
        SesionContent(
            sesion = sesion,
            acceso = DatosEjemplo.accesos.first { it.id == sesion.idAcceso },
            nombres = DatosEjemplo.colaboradores.associate { it.id to it.nombre },
            validos = registros,
            porcentajeTransito = Estadisticas.porcentajeTransito(registros),
            esDuenio = false, esSupervisor = true,
            onIniciar = {}, onRegistrar = {}, onFinalizar = {}, onCancelar = {},
            onValidar = {}, onRechazar = { null }, onAnularRegistro = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, name = "Programada (observador)")
@Composable
private fun SesionProgramadaPreview() {
    val sesion = DatosEjemplo.sesiones.first().copy(estado = EstadoSesion.PROGRAMADA)
    RinconSurTheme {
        SesionContent(
            sesion = sesion,
            acceso = DatosEjemplo.accesos.first { it.id == sesion.idAcceso },
            nombres = DatosEjemplo.colaboradores.associate { it.id to it.nombre },
            validos = emptyList(),
            porcentajeTransito = null,
            esDuenio = true, esSupervisor = false,
            onIniciar = {}, onRegistrar = {}, onFinalizar = {}, onCancelar = {},
            onValidar = {}, onRechazar = { null }, onAnularRegistro = {}, onVolver = {}
        )
    }
}
