package cl.duocuc.rinconsur.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.GrisDeshabilitado

object EstilosCampo {

    val forma: Shape = RoundedCornerShape(Dimens.radioCampo)

    @Composable
    fun colores(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,      // input:focus
        unfocusedBorderColor = GrisDeshabilitado,                    // input normal
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        errorBorderColor = MaterialTheme.colorScheme.error,          // input.error
        cursorColor = MaterialTheme.colorScheme.primary
    )
}