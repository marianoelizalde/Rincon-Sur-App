package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.data.remote.NuevoRegistroRequest
import cl.duocuc.rinconsur.data.remote.RegistroApi
import cl.duocuc.rinconsur.data.remote.RetrofitClient
import cl.duocuc.rinconsur.data.remote.aRegistro
import cl.duocuc.rinconsur.model.RegistroObservacion

class RegistroRepository(
    private val registroApi: RegistroApi = RetrofitClient.registroApi
) {

    suspend fun listar(idSesion: Int): List<RegistroObservacion> =
        llamar { registroApi.listarPorSesion(idSesion.toLong()) }.map { it.aRegistro() }

    suspend fun registrar(registro: RegistroObservacion, idActor: Int): RegistroObservacion {
        val datos = NuevoRegistroRequest(
            sesionId = registro.idSesion.toLong(),
            clasificacion = registro.clasificacion.name,
            hora = registro.hora,
            rangoEtario = registro.rangoEtario?.name,
            tipoCompania = registro.tipoCompania?.name,
            compraOtraTienda = registro.compraOtraTienda,
            sexo = registro.sexo?.name
        )
        return llamar { registroApi.registrar(datos, idActor.toLong()) }.aRegistro()
    }

    suspend fun anular(id: Int, idActor: Int): RegistroObservacion =
        llamar { registroApi.anular(id.toLong(), idActor.toLong()) }.aRegistro()

    suspend fun deshacerUltimo(idSesion: Int, idActor: Int): RegistroObservacion =
        llamar { registroApi.deshacerUltimo(idSesion.toLong(), idActor.toLong()) }.aRegistro()
}
