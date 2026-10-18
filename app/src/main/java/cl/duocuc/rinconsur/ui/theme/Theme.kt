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
    primary = Burdeo,
    onPrimary = Color.White,
    primaryContainer = BurdeoClaro,
    onPrimaryContainer = Color(0xFF3F0019),
    secondary = Terracota,
    onSecondary = Color.White,
    secondaryContainer = TerracotaClaro,
    onSecondaryContainer = Color(0xFF3A0B00),
    background = FondoClaro,
    surface = FondoClaro,
    surfaceVariant = Color(0xFFF4DDE0)
)

private val EsquemaOscuro = darkColorScheme(
    primary = BurdeoOscuro,
    onPrimary = Color(0xFF5E1129),
    onPrimaryContainer = Color(0xFFFFD9E1),
    primaryContainer = Burdeo,
    secondary = TerracotaOscuro,
    onSecondary = Color(0xFF5B1A05),
    secondaryContainer = Color(0xFF7A3418),
    onSecondaryContainer = Color(0xFFFFDBCF),
    background = FondoOscuro,
    onBackground = Color(0xFFF1DEE0),
    surface = FondoOscuro,
    onSurface = Color(0xFFF1DEE0),
    surfaceVariant = Color(0xFF3A2A2D),
    onSurfaceVariant = Color(0xFFD7C1C4)
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