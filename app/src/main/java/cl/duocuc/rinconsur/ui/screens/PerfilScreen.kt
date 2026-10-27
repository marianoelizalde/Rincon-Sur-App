package cl.duocuc.rinconsur.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.data.ConvertidorImagen
import cl.duocuc.rinconsur.data.DatosEjemplo
import cl.duocuc.rinconsur.data.ModoTema
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.UsuarioActual
import cl.duocuc.rinconsur.permisos.Permisos
import cl.duocuc.rinconsur.ui.components.AvatarCircular
import cl.duocuc.rinconsur.ui.components.BotonPrincipal
import cl.duocuc.rinconsur.ui.components.BotonSecundario
import cl.duocuc.rinconsur.ui.components.GrupoChips
import cl.duocuc.rinconsur.ui.components.MenuLateral
import cl.duocuc.rinconsur.ui.components.OpcionMenu
import cl.duocuc.rinconsur.ui.components.TarjetaSeccion
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duocuc.rinconsur.ui.components.CampoTexto
import androidx.compose.animation.AnimatedVisibility


@Composable
fun PerfilScreen(
    usuario: UsuarioActual?,
    alIrASesiones: () -> Unit,
    alIrACrearUsuario: () -> Unit,
    alCerrarSesion: () -> Unit,
    tema: ModoTema,
    onTemaSeleccionado: (ModoTema) -> Unit,
    onFotoTomada: (Bitmap) -> Unit,
    onImagenElegida: (Uri) -> Unit,
    onActualizarNombre: (String) -> String?
) {

    val contexto = LocalContext.current
    val colaborador = usuario?.colaborador


    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val textoCamaraDenegada = stringResource(R.string.permiso_camara_denegado)
    val textoGaleriaDenegada = stringResource(R.string.permiso_galeria_denegado)
    val textoAjustes = stringResource(R.string.btn_ajustes)
    val textoPerfilGuardado = stringResource(R.string.perfil_actualizado)


    fun avisarPermisoDenegado(mensaje: String) {
        scope.launch {
            val resultado = snackbarHostState.showSnackbar(
                message = mensaje,
                actionLabel = textoAjustes,
                duration = SnackbarDuration.Long
            )
            if (resultado == SnackbarResult.ActionPerformed) Permisos.abrirAjustes(contexto)
        }
    }


    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { foto: Bitmap? ->
        if (foto != null) onFotoTomada(foto)
    }


    fun abrirCamara() {
        try {
            lanzadorCamara.launch(null)
        } catch (e: ActivityNotFoundException) {
        }
    }


    val pedirPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->

        if (concedido) abrirCamara() else avisarPermisoDenegado(textoCamaraDenegada)
    }


    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val uri = resultado.data?.data
        if (resultado.resultCode == Activity.RESULT_OK && uri != null) onImagenElegida(uri)
    }


    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            lanzadorGaleria.launch(intent)
        } catch (e: ActivityNotFoundException) {
        }
    }


    val pedirPermisoImagenes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        if (concedido) abrirGaleria() else avisarPermisoDenegado(textoGaleriaDenegada)
    }


    val foto = remember(colaborador?.avatar) {
        colaborador?.avatar?.let { bytes -> ConvertidorImagen.aBitmap(bytes)?.asImageBitmap() }
    }

    MenuLateral(
        nombreUsuario = colaborador?.nombre,
        opcionActual = OpcionMenu.PERFIL,
        alIrASesiones = alIrASesiones,
        alIrAPerfil = {},
        esSupervisor = colaborador?.perfil == PerfilUsuario.SUPERVISOR,
        alIrACrearUsuario = alIrACrearUsuario,
        alCerrarSesion = alCerrarSesion
    ) { abrirMenu ->
        PerfilContent(
            colaborador = colaborador,
            foto = foto,
            tema = tema,
            snackbarHostState = snackbarHostState,
            onTomarFoto = {

                if (Permisos.tiene(contexto, Permisos.CAMARA)) abrirCamara()
                else pedirPermisoCamara.launch(Permisos.CAMARA)
            },
            onElegirDeGaleria = {
                if (Permisos.tiene(contexto, Permisos.IMAGENES)) abrirGaleria()
                else pedirPermisoImagenes.launch(Permisos.IMAGENES)
            },
            onGuardarNombre = { nombre ->
                val error = onActualizarNombre(nombre)
                if (error == null) {
                    scope.launch { snackbarHostState.showSnackbar(textoPerfilGuardado) }
                }
                error
            },
            onTemaSeleccionado = onTemaSeleccionado,
            onCerrarSesion = alCerrarSesion,
            onAbrirMenu = abrirMenu
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilContent(
    colaborador: Colaborador?,
    foto: ImageBitmap?,
    tema: ModoTema,
    onTomarFoto: () -> Unit,
    onElegirDeGaleria: () -> Unit,
    onGuardarNombre: (String) -> String?,
    onTemaSeleccionado: (ModoTema) -> Unit,
    onCerrarSesion: () -> Unit,
    onAbrirMenu: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var nombreEditado by remember(colaborador?.nombre) {
        mutableStateOf(colaborador?.nombre.orEmpty()) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var editando by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_perfil)) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioGrande)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            AvatarCircular(
                descripcion = stringResource(R.string.cd_foto_perfil),
                foto = foto
            )

            Text(colaborador?.nombre.orEmpty(), style = MaterialTheme.typography.headlineSmall)
            Text(colaborador?.perfil?.etiqueta.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            Text(colaborador?.correo.orEmpty(), style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioItem)) {
                BotonPrincipal(
                    texto = stringResource(R.string.btn_camara),
                    onClick = onTomarFoto,
                    icono = R.drawable.ic_camara,
                    modifier = Modifier.weight(1f)
                )
                BotonSecundario(
                    texto = stringResource(R.string.btn_galeria),
                    onClick = onElegirDeGaleria,
                    icono = R.drawable.ic_galeria,
                    modifier = Modifier.weight(1f)
                )
            }

            BotonSecundario(
                texto = stringResource(R.string.btn_cambiar_nombre),
                onClick = { editando = !editando },

                icono = R.drawable.ic_editar,
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = editando) {
                TarjetaSeccion(titulo = stringResource(R.string.seccion_editar_perfil)) {
                    CampoTexto(
                        valor = nombreEditado,
                        onValorCambia = { nombreEditado = it; errorNombre = null },
                        etiqueta = stringResource(R.string.campo_nombre_perfil),
                        icono = R.drawable.ic_persona,
                        error = errorNombre
                    )
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_guardar_cambios),
                        onClick = {
                            errorNombre = onGuardarNombre(nombreEditado)
                            if (errorNombre == null) editando = false
                        },
                        icono = R.drawable.ic_check,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            TarjetaSeccion(titulo = stringResource(R.string.seccion_apariencia)) {
                GrupoChips(
                    titulo = stringResource(R.string.etiqueta_tema),
                    opciones = ModoTema.entries.toList(),
                    seleccionada = tema,
                    onSeleccionar = onTemaSeleccionado,
                    textoDe = { it.etiqueta }
                )
            }

            Spacer(Modifier.height(Dimens.espacioGrande))

            BotonPrincipal(
                texto = stringResource(R.string.menu_cerrar_sesion),
                onClick = onCerrarSesion,
                icono = R.drawable.ic_salir,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilPreview() {
    RinconSurTheme {
        PerfilContent(
            colaborador = DatosEjemplo.colaboradores.first(),
            foto = null,
            tema = ModoTema.SISTEMA,
            onTomarFoto = {}, onElegirDeGaleria = {}, onGuardarNombre = { null },
            onTemaSeleccionado = {},
            onCerrarSesion = {}, onAbrirMenu = {}
        )
    }
}
