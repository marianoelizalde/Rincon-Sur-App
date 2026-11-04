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
    primary = AzulMontana,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5E0E8),
    onPrimaryContainer = MarronRincon,
    secondary = NaranjaSol,
    onSecondary = MarronRincon,
    secondaryContainer = Color(0xFFFFDCC2),
    onSecondaryContainer = MarronRincon,
    background = CremaRincon,
    onBackground = MarronRincon,
    surface = CremaRincon,
    onSurface = MarronRincon,
    surfaceVariant = Color(0xFFEFE6D2)
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF5B7B90),
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFFE6D5C8),
    primaryContainer = Color(0xFF966930),
    secondary = NaranjaSol,
    onSecondary = MarronRincon,
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