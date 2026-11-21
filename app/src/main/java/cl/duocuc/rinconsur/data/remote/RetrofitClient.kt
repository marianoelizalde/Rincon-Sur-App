package cl.duocuc.rinconsur.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsuarioEnSesion {
    @Volatile
    var id: Long? = null
}

object RetrofitClient {

    private const val URL_BASE = "http://10.0.2.2:9090/"

    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val pedido = cadena.request()
                val idActor = UsuarioEnSesion.id
                if (idActor != null && pedido.header("X-Usuario-Id") == null) {
                    cadena.proceed(pedido.newBuilder().header("X-Usuario-Id", idActor.toString()).build())
                } else {
                    cadena.proceed(pedido)
                }
            }
            .build()
    }

    private fun crear(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val retrofit: Retrofit by lazy { crear(URL_BASE) }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
    val perfilApi: PerfilApi by lazy { retrofit.create(PerfilApi::class.java) }
    val accesoApi: AccesoApi by lazy { retrofit.create(AccesoApi::class.java) }
    val sesionApi: SesionApi by lazy { retrofit.create(SesionApi::class.java) }
    val estadoApi: EstadoApi by lazy { retrofit.create(EstadoApi::class.java) }
    val registroApi: RegistroApi by lazy { retrofit.create(RegistroApi::class.java) }
}
