package cl.duocuc.rinconsur.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


object Contrasenas {
    private const val ITERACIONES = 65_536
    private const val LARGO_HASH_BITS = 256
    private const val LARGO_SAL_BYTES = 16


    fun hashear(clave: String): String {
        val sal = ByteArray(LARGO_SAL_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derivar(clave, sal, ITERACIONES)
        return "$ITERACIONES:${codificar(sal)}:${codificar(hash)}"
    }


    fun verificar(clave: String, guardado: String): Boolean {
        val partes = guardado.split(":")
        if (partes.size != 3) return false
        val iteraciones = partes[0].toIntOrNull() ?: return false
        val sal = decodificar(partes[1])
        val esperado = decodificar(partes[2])
        val calculado = derivar(clave, sal, iteraciones)
        return MessageDigest.isEqual(esperado, calculado)
    }

    private fun derivar(clave: String, sal: ByteArray, iteraciones: Int): ByteArray {
        val especificacion = PBEKeySpec(clave.toCharArray(), sal, iteraciones, LARGO_HASH_BITS)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(especificacion).encoded
    }

    private fun codificar(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decodificar(texto: String): ByteArray = Base64.decode(texto, Base64.NO_WRAP)
}