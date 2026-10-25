package cl.duocuc.rinconsur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.UsuarioActual
import cl.duocuc.rinconsur.ui.components.EtiquetaEstado
import cl.duocuc.rinconsur.ui.components.MenuLateral
import cl.duocuc.rinconsur.ui.components.OpcionMenu
import cl.duocuc.rinconsur.ui.styles.EstilosTarjeta
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.ui.components.CampoTexto
import cl.duocuc.rinconsur.ui.components.GrupoChips

// =====================================================================
//  SesionesScreen.kt  -  Lista de sesiones (pantalla de inicio despues del login).
//  El Supervisor las ve todas y programa nuevas; el Observador ve solo las suyas.
//  No tiene ViewModel propio: recibe los datos de RinconViewModel (via AppNavegacion).
// =====================================================================
@Composable
fun SesionesScreen(
    usuario: UsuarioActual?,                       // RinconViewModel.usuarioActual
    sesiones: List<SesionObservacion>,             // RinconViewModel.sesiones
    accesos: List<Acceso>,                         // RinconViewModel.accesos
    nombres: Map<Int, String>,                     // RinconViewModel.nombres
    mensaje: String?,                              // RinconViewModel.mensaje
    onMensajeMostrado: () -> Unit,                 // RinconViewModel.limpiarMensaje
    alProgramar: () -> Unit,
    alAbrirSesion: (Int) -> Unit,
    alIrAPerfil: () -> Unit,
    alIrACrearUsuario: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    val colaborador = usuario?.colaborador
    val esSupervisor = colaborador?.perfil == PerfilUsuario.SUPERVISOR
    val visibles = when {
        colaborador == null -> emptyList()
        esSupervisor -> sesiones
        else -> sesiones.filter { it.idObservador == colaborador.id }
    }

    MenuLateral(
        nombreUsuario = colaborador?.nombre,
        opcionActual = OpcionMenu.SESIONES,
        alIrASesiones = {},
        alIrAPerfil = alIrAPerfil,
        esSupervisor = esSupervisor,
        alIrACrearUsuario = alIrACrearUsuario,
        alCerrarSesion = alCerrarSesion
    ) { abrirMenu ->
        SesionesContent(
            sesiones = visibles,
            accesos = accesos,
            nombres = nombres,
            nombreUsuario = colaborador?.nombre.orEmpty(),
            esSupervisor = esSupervisor,
            snackbarHostState = snackbarHostState,
            onProgramar = alProgramar,
            onAbrirSesion = alAbrirSesion,
            onAbrirMenu = abrirMenu
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SesionesContent(
    sesiones: List<SesionObservacion>,
    accesos: List<Acceso>,
    nombres: Map<Int, String>,
    nombreUsuario: String,
    esSupervisor: Boolean,
    onProgramar: () -> Unit,
    onAbrirSesion: (Int) -> Unit,
    onAbrirMenu: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var fechaFiltro by remember { mutableStateOf("") }
    var estadoFiltro by remember { mutableStateOf<EstadoSesion?>(null) }
    val filtradas = sesiones.filter { s ->
        (estadoFiltro == null || s.estado == estadoFiltro) &&
                s.fecha.contains(fechaFiltro.trim())
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_sesiones)) },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.cd_menu)
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
        floatingActionButton = {
            // Solo el supervisor programa sesiones; el boton aparece con animacion.
            AnimatedVisibility(visible = esSupervisor, enter = scaleIn(), exit = scaleOut()) {
                ExtendedFloatingActionButton(
                    onClick = onProgramar,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_agregar),
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.tamanoIcono)
                        )
                    },
                    text = { Text(stringResource(R.string.btn_programar)) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (sesiones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Dimens.espacioGrande),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        if (esSupervisor) R.string.sesiones_vacio_supervisor else R.string.sesiones_vacio_observador
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Dimens.espacioMedio),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioItem)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.saludo, nombreUsuario),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico))

                    {

                        CampoTexto(
                            valor = fechaFiltro,
                            onValorCambia = { fechaFiltro = it },
                            etiqueta = stringResource(R.string.filtro_fecha),
                            icono = R.drawable.ic_calendario,
                            ayuda = stringResource(R.string.filtro_fecha_ayuda)
                        )
                        GrupoChips(
                            titulo = stringResource(R.string.filtro_estado),
                            opciones = EstadoSesion.entries,
                            seleccionada = estadoFiltro,
                            onSeleccionar = { estadoFiltro = if (estadoFiltro == it) null

                            else it },

                            textoDe = { it.etiqueta }
                        )
                        if (fechaFiltro.isNotBlank() || estadoFiltro != null) {
                            TextButton(onClick = { fechaFiltro = ""; estadoFiltro = null })

                            {

                                Text(stringResource(R.string.filtro_limpiar))
                            }
                        }
                    }
                }
                if (filtradas.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.filtro_sin_resultados),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(filtradas, key = { it.id }) { sesion ->
                        TarjetaSesion(
                            sesion = sesion,
                            acceso = accesos.firstOrNull { it.id == sesion.idAcceso },
                            nombreObservador = nombres[sesion.idObservador],
                            mostrarObservador = esSupervisor,
                            onAbrir = { onAbrirSesion(sesion.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaSesion(
    sesion: SesionObservacion,
    acceso: Acceso?,
    nombreObservador: String?,
    mostrarObservador: Boolean,
    onAbrir: () -> Unit
) {
    ElevatedCard(onClick = onAbrir, shape = EstilosTarjeta.forma, modifier = Modifier.fillMaxWidth()) {
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
                    style = MaterialTheme.typography.titleMedium
                )
                EtiquetaEstado(sesion.estado)
            }
            Text(stringResource(R.string.sesion_horario, sesion.fecha, sesion.horaInicio,
                sesion.horaFin))
            if (mostrarObservador) {
                Text(
                    text = stringResource(R.string.sesion_observador, nombreObservador ?: "-"),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Supervisor")
@Composable
private fun SesionesSupervisorPreview() {
    RinconSurTheme {
        SesionesContent(
            sesiones = DatosEjemplo.sesiones,
            accesos = DatosEjemplo.accesos,
            nombres = DatosEjemplo.colaboradores.associate { it.id to it.nombre },
            nombreUsuario = "Daniela",
            esSupervisor = true,
            onProgramar = {}, onAbrirSesion = {}, onAbrirMenu = {}
        )
    }
}

@Preview(showBackground = true, name = "Observador sin sesiones")
@Composable
private fun SesionesVaciasPreview() {
    RinconSurTheme {
        SesionesContent(
            sesiones = emptyList(),
            accesos = DatosEjemplo.accesos,
            nombres = emptyMap(),
            nombreUsuario = "Mariano",
            esSupervisor = false,
            onProgramar = {}, onAbrirSesion = {}, onAbrirMenu = {}
        )
    }
}
