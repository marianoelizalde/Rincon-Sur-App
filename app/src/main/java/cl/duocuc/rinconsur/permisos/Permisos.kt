package cl.duocuc.rinconsur.permisos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

// =====================================================================
//  Permisos.kt  -  Los 4 pasos para usar una API protegida por un permiso PELIGROSO:
//
//   1) DECLARAR   <uses-permission> en AndroidManifest.xml
//   2) VERIFICAR  esta ya concedido?              -> tiene()   (este archivo)
//   3) PEDIR      mostrar el dialogo del sistema   -> PerfilScreen (RequestPermission)
//   4) REACCIONAR concedido -> usar la API  /  rechazado -> avisar y ofrecer Ajustes
//
//  Si el usuario rechaza 2 veces, Android NO vuelve a mostrar el dialogo:
//  la unica forma de concederlo es desde Ajustes -> abrirAjustes().
// =====================================================================
object Permisos {

    // Nombre del permiso de camara (es igual en todas las versiones)
    val CAMARA: String = Manifest.permission.CAMERA

    // El permiso para leer fotos CAMBIO en Android 13 (API 33).
    // Build.VERSION.SDK_INT = version de Android del telefono donde corre la app.
    val IMAGENES: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    // PASO 2 -> VERIFICAR. Devuelve true si el usuario ya lo concedio.
    fun tiene(context: Context, permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    // Abre la pantalla de Ajustes de ESTA app (Permisos, Almacenamiento...).
    fun abrirAjustes(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
