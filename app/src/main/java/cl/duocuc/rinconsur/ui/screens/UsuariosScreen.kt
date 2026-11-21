package cl.duocuc.rinconsur.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import kotlinx.coroutines.launch

@Composable
fun UsuariosScreen(
    usuarios: List<Colaborador>,
    idActual: Int?,
    mensaje: String?,
    onMensajeMostrado: () -> Unit,
    onCambiarActivo: (Int) -> String?,
    onCambiarRol: (Int) -> String?,
    onEditar: (Int) -> Unit,
    onNuevoUsuario: () -> Unit,
    onVolver: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            onMensajeMostrado()
        }
    }

    fun mostrarError(error: String?) {
        if (error != null) scope.launch { snackbarHostState.showSnackbar(error) }
    }

    UsuariosContent(
        usuarios = usuarios,
        idActual = idActual,
        onCambiarActivo = { mostrarError(onCambiarActivo(it)) },
        onCambiarRol = { mostrarError(onCambiarRol(it)) },
        onEditar = onEditar,
        onNuevoUsuario = onNuevoUsuario,
        onVolver = onVolver,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosContent(
    usuarios: List<Colaborador>,
    idActual: Int?,
    onCambiarActivo: (Int) -> Unit,
    onCambiarRol: (Int) -> Unit,
    onEditar: (Int) -> Unit,
    onNuevoUsuario: () -> Unit,
    onVolver: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_usuarios)) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(Dimens.espacioMedio)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                items(usuarios, key = { it.id }) { usuario ->
                    TarjetaUsuario(
                        usuario = usuario,
                        esMiCuenta = usuario.id == idActual,
                        onCambiarActivo = { onCambiarActivo(usuario.id) },
                        onCambiarRol = { onCambiarRol(usuario.id) },
                        onEditar = { onEditar(usuario.id) }
                    )
                }
            }
            BotonPrincipal(
                texto = stringResource(R.string.btn_nuevo_usuario),
                onClick = onNuevoUsuario,
                icono = R.drawable.ic_persona_agregar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.espacioMedio)
            )
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: Colaborador,
    esMiCuenta: Boolean,
    onCambiarActivo: () -> Unit,
    onCambiarRol: () -> Unit,
    onEditar: () -> Unit
) {
    TarjetaSeccion(titulo = if (esMiCuenta) stringResource(R.string.usuario_tu, usuario.nombre) else usuario.nombre) {
        Text(usuario.correo, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(
                R.string.usuario_perfil_rut,
                usuario.perfil.etiqueta,
                if (usuario.rut.isBlank()) stringResource(R.string.usuario_sin_rut) else usuario.rut
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(if (usuario.activo) R.string.usuario_activo else R.string.usuario_desactivado),
            style = MaterialTheme.typography.titleSmall,
            color = if (usuario.activo) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
            BotonSecundario(
                texto = stringResource(R.string.btn_editar),
                onClick = onEditar,
                modifier = Modifier.weight(1f)
            )

            if (!esMiCuenta) {
                BotonSecundario(
                    texto = stringResource(R.string.btn_cambiar_rol),
                    onClick = onCambiarRol,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (!esMiCuenta) {
            BotonSecundario(
                texto = stringResource(if (usuario.activo) R.string.btn_desactivar else R.string.btn_activar),
                onClick = onCambiarActivo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun UsuariosPreview() {
    RinconSurTheme {
        UsuariosContent(
            usuarios = DatosEjemplo.colaboradores,
            idActual = DatosEjemplo.colaboradores.first().id,
            onCambiarActivo = {}, onCambiarRol = {}, onEditar = {},
            onNuevoUsuario = {}, onVolver = {}
        )
    }
}