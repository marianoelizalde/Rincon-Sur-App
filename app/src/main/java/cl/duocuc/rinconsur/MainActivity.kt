package cl.duocuc.rinconsur

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duocuc.rinconsur.data.ModoTema
import cl.duocuc.rinconsur.data.Textos
import cl.duocuc.rinconsur.navigation.AppNavegacion
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme
import cl.duocuc.rinconsur.viewmodel.RinconViewModel


class MainActivity : ComponentActivity() {

    private val rinconViewModel: RinconViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Textos.iniciar(applicationContext)
        setContent {
            val modo by rinconViewModel.tema.collectAsStateWithLifecycle()
            val oscuro = when (modo) {
                ModoTema.SISTEMA -> isSystemInDarkTheme()
                ModoTema.CLARO -> false
                ModoTema.OSCURO -> true
            }

            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro }
                )
                onDispose { }
            }

            RinconSurTheme(darkTheme = oscuro) {
                AppNavegacion(rinconViewModel)
            }
        }
    }
}
