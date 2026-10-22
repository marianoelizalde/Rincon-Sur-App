package cl.duocuc.rinconsur.data



import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream

// =====================================================================
//  ConvertidorImagen.kt  -  Una imagen pasa por 3 formas distintas en esta app:
//
//   Camara ---> Bitmap -+
//                        +--> ByteArray (JPEG) ---> columna BLOB de SQLite
//   Galeria --> Uri ----+          |
//                                   +--> Bitmap ---> Image en el Perfil
//
//   Uri       = "direccion" de un archivo que pertenece a OTRA app (la galeria).
//   Bitmap    = la imagen en memoria, pixel por pixel (pesada).
//   ByteArray = la imagen comprimida en JPEG (liviana): es lo que se guarda.
//
//  "object" = una sola instancia para toda la app. Se usa asi: ConvertidorImagen.aBytes(foto)
// =====================================================================
object ConvertidorImagen {

    // Tamano maximo del lado mas largo, en pixeles: un avatar no necesita 4000 px.
    private const val LADO_MAXIMO = 400

    // Calidad JPEG de 0 a 100. 80 = buen equilibrio entre peso y calidad.
    private const val CALIDAD_JPEG = 80

    // Galeria: lee la imagen a la que apunta el Uri y la convierte en Bitmap.
    // contentResolver es el "intermediario" que nos deja leer archivos de otras apps.
    fun desdeUri(context: Context, uri: Uri): Bitmap? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Android 9+: ImageDecoder respeta la rotacion de la foto.
            // ALLOCATOR_SOFTWARE = Bitmap en memoria normal, para poder achicarlo despues.
            val origen = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(origen) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            // Android 7 y 8: se abre el archivo como un flujo de bytes y se decodifica
            context.contentResolver.openInputStream(uri).use { flujo ->
                BitmapFactory.decodeStream(flujo)
            }
        }
    } catch (e: Exception) {
        null   // archivo danado o sin acceso: no se cambia el avatar
    }

    // Bitmap -> ByteArray (JPEG achicado). Esto es lo que va a SQLite.
    fun aBytes(bitmap: Bitmap): ByteArray {
        val chico = achicar(bitmap)
        val salida = ByteArrayOutputStream()
        chico.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        return salida.toByteArray()
    }

    // ByteArray -> Bitmap, para MOSTRAR la foto leida de la base de datos
    fun aBitmap(bytes: ByteArray): Bitmap? =
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Reduce el tamano manteniendo la proporcion (no deforma la foto)
    private fun achicar(bitmap: Bitmap): Bitmap {
        val ladoMayor = maxOf(bitmap.width, bitmap.height)
        if (ladoMayor <= LADO_MAXIMO) return bitmap
        val escala = LADO_MAXIMO.toFloat() / ladoMayor
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * escala).toInt(),
            (bitmap.height * escala).toInt(),
            true    // true = suavizado (filtro bilineal)
        )
    }
}
