package cl.duocuc.rinconsur.data

import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.PerfilUsuario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class UsuarioRepositoryTest {

    private lateinit var apiFalsa: FakeUsuarioApi
    private lateinit var repositorio: UsuarioRepository

    @Before
    fun preparar() {
        apiFalsa = FakeUsuarioApi()
        repositorio = UsuarioRepository(usuarioApi = apiFalsa, perfilApi = FakePerfilApi())
    }

    @Test
    fun iniciarSesion_onvierteLaRespuestaEnColaborador() = runBlocking {
        val colaborador = repositorio.iniciarSesion("ana@duocuc.cl", "Clave123!")

        assertEquals(1, colaborador.id)
        assertEquals("Ana Perez", colaborador.nombre)
        assertEquals(PerfilUsuario.SUPERVISOR, colaborador.perfil)
        assertEquals("ana@duocuc.cl", apiFalsa.ultimoLogin?.correo)
    }

    @Test
    fun registrar_enviaElIdDelPerfilElegido() = runBlocking {
        val nuevo = Colaborador(
            nombre = "Luis Soto",
            correo = "luis@duocuc.cl",
            clave = "Clave123!",
            perfil = PerfilUsuario.OBSERVADOR,
            rut = "12345678-5"
        )

        repositorio.registrar(nuevo)

        assertEquals(20L, apiFalsa.ultimoRegistro?.perfilId)
        assertEquals("luis@duocuc.cl", apiFalsa.ultimoRegistro?.correo)
    }
    @Test
    fun registrar_usuarioSinFotoNoTieneAvatar() = runBlocking {
        val nuevo = Colaborador(
            nombre = "Luis Soto",
            correo = "luis@duocuc.cl",
            clave = "Clave123!",
            perfil = PerfilUsuario.OBSERVADOR
        )

        val creado = repositorio.registrar(nuevo)

        assertNull(creado.avatar)
    }
    @Test
    fun registrar_siElPerfilNoExisteEnElServidorRegistrarFalla() {
        val repoSinPerfiles = UsuarioRepository(usuarioApi = apiFalsa, perfilApi = FakePerfilApi(emptyList()))
        val nuevo = Colaborador(
            nombre = "Luis Soto",
            correo = "luis@duocuc.cl",
            clave = "Clave123!",
            perfil = PerfilUsuario.OBSERVADOR
        )

        assertThrows(Exception::class.java) {
            runBlocking { repoSinPerfiles.registrar(nuevo) }
        }
    }
    @Test
    fun listar_convierteTodosLosUsuariosEnColaboradores() = runBlocking {
        val lista = repositorio.listar()

        assertEquals(2, lista.size)
        assertEquals("Ana Perez", lista[0].nombre)
        assertEquals(PerfilUsuario.OBSERVADOR, lista[1].perfil)
    }

    @Test
    fun iniciarSesion_descargaLaFotoDePerfil() = runBlocking {
        val colaborador = repositorio.iniciarSesion("ana@duocuc.cl", "Clave123!")

        assertEquals(3, colaborador.avatar!!.size)
    }



    @Test
    fun recuperar_enviaCorreoRutYNuevaClave() = runBlocking {
        repositorio.recuperar("ana@duocuc.cl", "12345678-5", "Nueva123!")

        assertEquals("ana@duocuc.cl", apiFalsa.ultimaRecuperacion?.correo)
        assertEquals("12345678-5", apiFalsa.ultimaRecuperacion?.rut)
        assertEquals("Nueva123!", apiFalsa.ultimaRecuperacion?.nuevaContrasena)
    }

    @Test
    fun actualizar_enviaLosDatosDelColaborador() = runBlocking {
        val editado = Colaborador(
            id = 1,
            nombre = "Ana Perez",
            correo = "ana@duocuc.cl",
            clave = "",
            perfil = PerfilUsuario.SUPERVISOR,
            cargo = "Jefa de turno"
        )

        repositorio.actualizar(editado)

        assertEquals("Jefa de turno", apiFalsa.ultimaActualizacion?.cargo)
    }

    @Test
    fun cambiarPerfil_devuelveAlUsuarioActualizado() = runBlocking {
        val actualizado = repositorio.cambiarPerfil(2, PerfilUsuario.OBSERVADOR, 1)

        assertEquals("Luis Soto", actualizado.nombre)
        assertEquals(PerfilUsuario.OBSERVADOR, actualizado.perfil)
    }


}