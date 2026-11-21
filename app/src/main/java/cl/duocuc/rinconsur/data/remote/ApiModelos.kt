package cl.duocuc.rinconsur.data.remote

import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoBd
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.Sexo
import cl.duocuc.rinconsur.model.TipoCompania

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class RegistroUsuarioRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rut: String,
    val cargo: String,
    val telefono: String,
    val direccion: String,
    val comuna: String,
    val perfilId: Long
)

data class ActualizarUsuarioRequest(
    val nombre: String,
    val rut: String,
    val cargo: String,
    val telefono: String,
    val direccion: String,
    val comuna: String
)

data class RecuperarRequest(
    val correo: String,
    val rut: String,
    val nuevaContrasena: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rut: String?,
    val cargo: String?,
    val telefono: String?,
    val direccion: String?,
    val comuna: String?,
    val activo: Boolean,
    val perfilId: Long,
    val perfilNombre: String,
    val avatarUrl: String?
)

data class PerfilResponse(
    val id: Long,
    val nombre: String
)

data class AccesoResponse(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val observacion: String?
)

data class SesionRequest(
    val accesoId: Long,
    val observadorId: Long,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String
)

data class RechazoRequest(
    val idValidador: Long,
    val motivo: String
)

data class SesionResponse(
    val id: Long,
    val accesoId: Long,
    val accesoCodigo: String?,
    val accesoNombre: String?,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val turno: String? = null,
    val observadorId: Long,
    val observador: String?,
    val estado: String,
    val estadoEtiqueta: String?,
    val validadorId: Long?,
    val motivoRechazo: String?,
    val registrosValidos: Long?
)

data class EstadoSesionResponse(
    val id: Long,
    val codigo: String,
    val etiqueta: String
)

fun EstadoSesionResponse.aEstado() = EstadoBd(codigo = codigo, etiqueta = etiqueta)

data class NuevoRegistroRequest(
    val sesionId: Long,
    val clasificacion: String,
    val hora: String,
    val rangoEtario: String?,
    val tipoCompania: String?,
    val compraOtraTienda: Boolean,
    val sexo: String?
)

data class RegistroResponse(
    val id: Long,
    val sesionId: Long,
    val hora: String,
    val clasificacion: String,
    val rangoEtario: String?,
    val tipoCompania: String?,
    val compraOtraTienda: Boolean,
    val anulado: Boolean,
    val sexo: String?
)

fun UsuarioResponse.aColaborador(avatar: ByteArray? = null) = Colaborador(
    id = id.toInt(),
    nombre = nombre,
    correo = correo,
    clave = "",
    perfil = if (perfilNombre.equals(PerfilUsuario.SUPERVISOR.etiqueta, ignoreCase = true)) {
        PerfilUsuario.SUPERVISOR
    } else {
        PerfilUsuario.OBSERVADOR
    },
    avatar = avatar,
    telefono = telefono.orEmpty(),
    rut = rut.orEmpty(),
    cargo = cargo.orEmpty(),
    direccion = direccion.orEmpty(),
    comuna = comuna.orEmpty(),
    activo = activo
)

fun AccesoResponse.aAcceso() = Acceso(
    id = id.toInt(),
    codigo = codigo,
    nombre = nombre,
    observacion = observacion
)

fun SesionResponse.aSesion() = SesionObservacion(
    id = id.toInt(),
    idAcceso = accesoId.toInt(),
    fecha = fecha,
    horaInicio = horaInicio,
    horaFin = horaFin,
    idObservador = observadorId.toInt(),
    estado = EstadoSesion.entries.firstOrNull { it.name == estado } ?: EstadoSesion.PROGRAMADA,
    estadoEtiqueta = estadoEtiqueta,
    turno = turno.orEmpty(),
    idValidador = validadorId?.toInt(),
    motivoRechazo = motivoRechazo
)

fun RegistroResponse.aRegistro() = RegistroObservacion(
    id = id.toInt(),
    idSesion = sesionId.toInt(),
    hora = hora,
    clasificacion = Clasificacion.entries.firstOrNull { it.name == clasificacion } ?: Clasificacion.TRANSITO,
    rangoEtario = RangoEtario.entries.firstOrNull { it.name == rangoEtario },
    tipoCompania = TipoCompania.entries.firstOrNull { it.name == tipoCompania },
    compraOtraTienda = compraOtraTienda,
    anulado = anulado,
    sexo = Sexo.entries.firstOrNull { it.name == sexo }
)
