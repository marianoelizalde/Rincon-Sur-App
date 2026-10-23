package cl.duocuc.rinconsur.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.ui.screens.CrearUsuarioScreen
import cl.duocuc.rinconsur.ui.screens.LoginScreen
import cl.duocuc.rinconsur.ui.screens.PerfilScreen
import cl.duocuc.rinconsur.ui.screens.ProgramarScreen
import cl.duocuc.rinconsur.ui.screens.RegistroScreen
import cl.duocuc.rinconsur.ui.screens.SesionScreen
import cl.duocuc.rinconsur.ui.screens.SesionesScreen
import cl.duocuc.rinconsur.viewmodel.RinconViewModel

// =====================================================================
//  AppNavegacion.kt  -  El NavController vive aqui: las pantallas solo
//  reciben funciones (lambdas), nunca el controlador.
//  RinconViewModel se crea UNA vez (en MainActivity) y desde aqui se reparten
//  sus datos y sus funciones a cada pantalla, igual que UsuariosViewModel del profesor.
// =====================================================================
@Composable
fun AppNavegacion(rincon: RinconViewModel) {
    val navController = rememberNavController()

    // Estado que lee la interfaz (cada vez que cambia, las pantallas se redibujan)
    val usuarioActual by rincon.usuarioActual.collectAsStateWithLifecycle()
    val tema by rincon.tema.collectAsStateWithLifecycle()
    val sesiones by rincon.sesiones.collectAsStateWithLifecycle()
    val accesos by rincon.accesos.collectAsStateWithLifecycle()
    val observadores by rincon.observadores.collectAsStateWithLifecycle()
    val nombres by rincon.nombres.collectAsStateWithLifecycle()
    val sesionAbierta by rincon.sesionAbierta.collectAsStateWithLifecycle()
    val registros by rincon.registros.collectAsStateWithLifecycle()
    val mensaje by rincon.mensaje.collectAsStateWithLifecycle()

    // Si SharedPreferences ya tenia una sesion iniciada se parte en la lista; si no, en el login.
    val inicio = remember { if (rincon.usuarioActual.value == null) Ruta.Login.ruta else Ruta.Sesiones.ruta }

    // Cerrar sesion desde el menu o desde el perfil: borra la sesion guardada y vuelve al login.
    val cerrarSesion: () -> Unit = {
        rincon.cerrarSesion()
        navController.navigate(Ruta.Login.ruta) { popUpTo(0) { inclusive = true } }
    }

    val irACrearUsuario: () -> Unit = {
        navController.navigate(Ruta.CrearUsuario.ruta) { launchSingleTop = true }
    }

    NavHost(
        navController = navController,
        startDestination = inicio,
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }) + fadeOut() }
    ) {
        composable(Ruta.Login.ruta) {
            LoginScreen(
                onIniciarSesion = rincon::iniciarSesion,
                onLoginExitoso = { navController.irASesionesLimpiando(Ruta.Login.ruta) }
            )
        }

        // Solo el Supervisor crea cuentas: si otro perfil llega aqui, se devuelve a la lista.
        composable(Ruta.CrearUsuario.ruta) {
            if (usuarioActual?.colaborador?.perfil == PerfilUsuario.SUPERVISOR) {
                CrearUsuarioScreen(
                    onCrearUsuario = rincon::registrarUsuario,
                    onUsuarioCreado = { navController.popBackStack() },
                    onVolver = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(Ruta.Sesiones.ruta) {
            SesionesScreen(
                usuario = usuarioActual,
                sesiones = sesiones,
                accesos = accesos,
                nombres = nombres,
                mensaje = mensaje,
                onMensajeMostrado = rincon::limpiarMensaje,
                alProgramar = { navController.navigate(Ruta.Programar.ruta) },
                alAbrirSesion = { id -> navController.navigate(Ruta.Sesion.crear(id)) },
                alIrAPerfil = { navController.navigate(Ruta.Perfil.ruta) { launchSingleTop = true } },
                alIrACrearUsuario = irACrearUsuario,
                alCerrarSesion = cerrarSesion
            )
        }

        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                usuario = usuarioActual,
                alIrASesiones = { navController.popBackStack(Ruta.Sesiones.ruta, inclusive = false) },
                alIrACrearUsuario = irACrearUsuario,
                alCerrarSesion = cerrarSesion,
                tema = tema,
                onTemaSeleccionado = rincon::onTemaSeleccionado,
                onFotoTomada = rincon::cambiarAvatarDesdeCamara,
                onImagenElegida = rincon::cambiarAvatarDesdeGaleria
            )
        }

        composable(Ruta.Programar.ruta) {
            ProgramarScreen(
                accesos = accesos,
                observadores = observadores,
                onProgramarSesion = rincon::programarSesion,
                onSesionProgramada = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Ruta.Sesion.ruta,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            // Le dice a RinconViewModel cual sesion mirar (lee la sesion y sus registros de SQLite)
            LaunchedEffect(id) { rincon.abrirSesion(id) }
            SesionScreen(
                usuario = usuarioActual,
                sesion = sesionAbierta?.takeIf { it.id == id },   // evita mostrar la sesion anterior
                registros = registros,
                accesos = accesos,
                nombres = nombres,
                mensaje = mensaje,
                onMensajeMostrado = rincon::limpiarMensaje,
                onIniciar = rincon::iniciar,
                onFinalizar = rincon::finalizar,
                onCancelar = rincon::cancelar,
                onValidar = rincon::validar,
                onRechazar = rincon::rechazar,
                onAnularRegistro = rincon::anularRegistro,
                alVolver = { navController.popBackStack() },
                alIrARegistro = { idSesion -> navController.navigate(Ruta.Registro.crear(idSesion)) }
            )
        }

        composable(
            route = Ruta.Registro.ruta,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            LaunchedEffect(id) { rincon.abrirSesion(id) }
            RegistroScreen(
                sesion = sesionAbierta?.takeIf { it.id == id },
                registros = registros,
                accesos = accesos,
                onRegistrarPersona = rincon::registrarPersona,
                onDeshacerUltimo = rincon::deshacerUltimo,
                alVolver = { navController.popBackStack() }
            )
        }
    }
}

// Entra a la lista de sesiones y borra de la pila la pantalla de ingreso (para no volver a ella con "atras").
private fun NavHostController.irASesionesLimpiando(rutaABorrar: String) {
    navigate(Ruta.Sesiones.ruta) {
        popUpTo(rutaABorrar) { inclusive = true }
    }
}
