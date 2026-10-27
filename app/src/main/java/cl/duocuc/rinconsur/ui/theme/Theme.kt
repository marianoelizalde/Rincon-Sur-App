package cl.duocuc.rinconsur.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = Verde,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = Color(0xFF3D250B),
    secondary = VerdeOliva,
    onSecondary = Color.White,
    secondaryContainer = VerdeOlivaClaro,
    onSecondaryContainer = Color(0xFF3A200A),
    background = FondoClaro,
    surface = FondoClaro,
    surfaceVariant = Color(0xFFEBE5DD)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeSuave,
    onPrimary = Color(0xFF250E01),
    onPrimaryContainer = Color(0xFFE6D5C8),
    primaryContainer = Color(0xFF966930),
    secondary = VerdeOlivaSuave,
    onSecondary = Color(0xFF8C581E),
    secondaryContainer = Color(0xFF884825),
    onSecondaryContainer = Color(0xFFDCEDC8),
    background = FondoOscuro,
    onBackground = Color(0xFFDFEBDF),
    surface = FondoOscuro,
    onSurface = Color(0xFFDFEBDF),
    surfaceVariant = Color(0xFF3A322A),
    onSurfaceVariant = Color(0xFFD2C9BF)
)


private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun RinconSurTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}