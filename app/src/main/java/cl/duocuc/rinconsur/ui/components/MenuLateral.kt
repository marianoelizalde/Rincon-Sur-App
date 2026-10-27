package cl.duocuc.rinconsur.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import kotlinx.coroutines.launch


enum class OpcionMenu { SESIONES, PERFIL, CREAR_USUARIO }


@Composable
fun MenuLateral(
    nombreUsuario: String?,
    opcionActual: OpcionMenu,
    alIrASesiones: () -> Unit,
    alIrAPerfil: () -> Unit,
    esSupervisor: Boolean,
    alIrACrearUsuario: () -> Unit,
    alCerrarSesion: () -> Unit,
    contenido: @Composable (abrirMenu: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(stringResource(R.string.dialogo_titulo)) },
            text = { Text(stringResource(R.string.dialogo_mensaje)) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    alCerrarSesion()
                }) { Text(stringResource(R.string.menu_cerrar_sesion)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text(stringResource(R.string.dialogo_cancelar))
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = if (nombreUsuario != null) stringResource(R.string.saludo, nombreUsuario)
                    else stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(Dimens.espacioMedio)
                )
                HorizontalDivider()

                ItemMenu(
                    texto = stringResource(R.string.menu_sesiones),
                    icono = R.drawable.ic_lista,
                    seleccionado = opcionActual == OpcionMenu.SESIONES,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (opcionActual != OpcionMenu.SESIONES) alIrASesiones()
                    }
                )
                ItemMenu(
                    texto = stringResource(R.string.menu_mi_perfil),
                    icono = R.drawable.ic_persona,
                    seleccionado = opcionActual == OpcionMenu.PERFIL,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (opcionActual != OpcionMenu.PERFIL) alIrAPerfil()
                    }
                )

                if (esSupervisor) {
                    ItemMenu(
                        texto = stringResource(R.string.menu_crear_usuario),
                        icono = R.drawable.ic_persona_agregar,
                        seleccionado = opcionActual == OpcionMenu.CREAR_USUARIO,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (opcionActual != OpcionMenu.CREAR_USUARIO) alIrACrearUsuario()
                        }
                    )
                }
                ItemMenu(
                    texto = stringResource(R.string.menu_cerrar_sesion),
                    icono = R.drawable.ic_salir,
                    seleccionado = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        mostrarDialogo = true
                    }
                )
            }
        }
    ) {
        contenido { scope.launch { drawerState.open() } }
    }
}

@Composable
private fun ItemMenu(
    texto: String,
    icono: Int,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(texto) },
        selected = seleccionado,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(icono),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        modifier = Modifier.padding(horizontal = Dimens.espacioChico)
    )
}

@Preview(showBackground = true)
@Composable
private fun MenuLateralPreview() {
    RinconSurTheme {
        MenuLateral(
            nombreUsuario = "Daniela Rojas",
            opcionActual = OpcionMenu.SESIONES,
            alIrASesiones = {},
            alIrAPerfil = {},
            esSupervisor = true,
            alIrACrearUsuario = {},
            alCerrarSesion = {}
        ) { abrirMenu ->
            Column(Modifier.padding(16.dp)) {
                BotonPrincipal(texto = "Abrir menu", onClick = abrirMenu)
            }
        }
    }
}