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
    onPrimaryContainer = Color(0xFF0B3D12),
    secondary = VerdeOliva,
    onSecondary = Color.White,
    secondaryContainer = VerdeOlivaClaro,
    onSecondaryContainer = Color(0xFF1B3A0A),
    background = FondoClaro,
    surface = FondoClaro,
    surfaceVariant = Color(0xFFDDEBDD)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeSuave,
    onPrimary = Color(0xFF0A3818),
    onPrimaryContainer = Color(0xFFC8E6C9),
    primaryContainer = Color(0xFF1F5E27),
    secondary = VerdeOlivaSuave,
    onSecondary = Color(0xFF1F3A0D),
    secondaryContainer = Color(0xFF3F6B24),
    onSecondaryContainer = Color(0xFFDCEDC8),
    background = FondoOscuro,
    onBackground = Color(0xFFDFEBDF),
    surface = FondoOscuro,
    onSurface = Color(0xFFDFEBDF),
    surfaceVariant = Color(0xFF2A3A2C),
    onSurfaceVariant = Color(0xFFBFD2C0)
)

// Formas (equivale a border-radius): botones, campos y tarjetas toman estas esquinas.
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