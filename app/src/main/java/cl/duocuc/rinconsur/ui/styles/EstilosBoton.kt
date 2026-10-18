package cl.duocuc.rinconsur.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import cl.duocuc.rinconsur.ui.theme.Blanco
import cl.duocuc.rinconsur.ui.theme.Dimens
import cl.duocuc.rinconsur.ui.theme.GrisDeshabilitado

// =====================================================================
//  EstilosBoton.kt  -  "HOJA DE ESTILOS" DE LOS BOTONES
//  Compose no usa CSS: se agrupan los estilos en un object.
//     background-color / color  -> ButtonColors
//     border-radius             -> Shape (RoundedCornerShape)
//     border: 2px solid         -> BorderStroke
//     padding                   -> PaddingValues
//     box-shadow                -> ButtonElevation
//     height: 52px              -> Modifier.height(...)
// =====================================================================
object EstilosBoton {

    val forma: Shape = RoundedCornerShape(Dimens.radioBoton)               // border-radius
    val relleno = PaddingValues(horizontal = 20.dp, vertical = 12.dp)       // padding

    /** Boton principal: relleno con el color primario del tema, texto blanco. */
    @Composable
    fun coloresPrincipal(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Blanco,
        disabledContainerColor = GrisDeshabilitado,
        disabledContentColor = Blanco
    )

    /** Boton secundario: transparente, texto del color primario del tema. */
    @Composable
    fun coloresSecundario(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    /** Borde del boton secundario. */
    @Composable
    fun bordeSecundario(): BorderStroke =
        BorderStroke(Dimens.bordeBoton, MaterialTheme.colorScheme.primary)

    /** Sombra del boton principal (al presionar "se hunde"). */
    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = 1.dp
    )
}


fun Modifier.estiloAltoBoton(): Modifier = this.height(Dimens.alturaBoton)