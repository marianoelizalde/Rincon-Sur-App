package cl.duocuc.rinconsur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.R
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.RinconSurTheme

// Foto de perfil redonda. Si hay foto (leida de SQLite) la dibuja; si no, muestra el icono de persona.
@Composable
fun AvatarCircular(
    descripcion: String,
    modifier: Modifier = Modifier,
    foto: ImageBitmap? = null       // null = sin foto -> icono por defecto
) {
    Box(
        modifier = modifier
            .size(Dimens.tamanoAvatar)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(Dimens.bordeAvatar, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (foto != null) {
            // BitmapPainter dibuja una imagen que esta en memoria (la de la base de datos)
            Image(
                painter = BitmapPainter(foto),
                contentDescription = descripcion,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_persona),
                contentDescription = descripcion,
                modifier = Modifier.size(Dimens.tamanoIconoGrande),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarCircularPreview() {
    RinconSurTheme {
        AvatarCircular(descripcion = "", modifier = Modifier.padding(16.dp))
    }
}
