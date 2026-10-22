package cl.duocuc.rinconsur.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.rinconsur.model.Acceso
import cl.duocuc.rinconsur.model.Clasificacion
import cl.duocuc.rinconsur.model.Colaborador
import cl.duocuc.rinconsur.model.EstadoSesion
import cl.duocuc.rinconsur.model.PerfilUsuario
import cl.duocuc.rinconsur.model.RangoEtario
import cl.duocuc.rinconsur.model.RegistroObservacion
import cl.duocuc.rinconsur.model.SesionObservacion
import cl.duocuc.rinconsur.model.TipoCompania

// Base de datos SQLite (rinconsur.db) con CINCO tablas relacionadas:
//
//   perfiles <---- colaboradores <---- sesiones <---- registros
//                                         |
//                                      accesos
//
// clave PRIMARIA (id_xxx)  <------  clave FORANEA (REFERENCES ...)
// UN helper por base de datos: todas las tablas se crean en el mismo onCreate().
class RinconDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "rinconsur.db"
        private const val VERSION_BD = 3

        // Tabla perfiles (1 = Supervisor, 2 = Observador)
        private const val TABLA_PERFILES = "perfiles"
        private const val PER_ID = "id_perfil"
        private const val PER_NOMBRE = "nombre_perfil"



        // Tabla colaboradores (los usuarios de la app)
        private const val TABLA_COLABORADORES = "colaboradores"
        private const val COL_ID = "id_colaborador"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_CLAVE = "clave"
        private const val COL_PERFIL = "id_perfil"
        private const val COL_AVATAR = "avatar"           // foto de perfil (BLOB)

        // Tabla accesos (A1 a A4)
        private const val TABLA_ACCESOS = "accesos"
        private const val ACC_ID = "id_acceso"
        private const val ACC_CODIGO = "codigo"
        private const val ACC_NOMBRE = "nombre_acceso"
        private const val ACC_OBS = "observacion"

        // Tabla sesiones
        private const val TABLA_SESIONES = "sesiones"
        private const val SES_ID = "id_sesion"
        private const val SES_ACCESO = "id_acceso"
        private const val SES_FECHA = "fecha"
        private const val SES_INICIO = "hora_inicio"
        private const val SES_FIN = "hora_fin"
        private const val SES_OBSERVADOR = "id_observador"
        private const val SES_ESTADO = "estado"
        private const val SES_VALIDADOR = "id_validador"
        private const val SES_MOTIVO = "motivo_rechazo"

        // Tabla registros (personas observadas, anonimas)
        private const val TABLA_REGISTROS = "registros"
        private const val REG_ID = "id_registro"
        private const val REG_SESION = "id_sesion"
        private const val REG_HORA = "hora"
        private const val REG_CLASIFICACION = "clasificacion"
        private const val REG_RANGO = "rango_etario"
        private const val REG_COMPANIA = "tipo_compania"
        private const val REG_OTRA_TIENDA = "compra_otra_tienda"
        private const val REG_DESTINO = "id_acceso_destino"
        private const val REG_ANULADO = "anulado"

        private const val SQL_CREAR_PERFILES =
            "CREATE TABLE $TABLA_PERFILES (" +
                    "$PER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$PER_NOMBRE TEXT NOT NULL UNIQUE)"

        // REFERENCES crea la CLAVE FORANEA hacia perfiles(id_perfil)
        private const val SQL_CREAR_COLABORADORES =
            "CREATE TABLE $TABLA_COLABORADORES (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_NOMBRE TEXT NOT NULL, " +
                    "$COL_CORREO TEXT NOT NULL UNIQUE, " +
                    "$COL_CLAVE TEXT NOT NULL, " +
                    "$COL_PERFIL INTEGER NOT NULL REFERENCES $TABLA_PERFILES($PER_ID), " +
                    "$COL_AVATAR BLOB)"      // BLOB = guarda bytes tal cual (la foto)

        private const val SQL_CREAR_ACCESOS =
            "CREATE TABLE $TABLA_ACCESOS (" +
                    "$ACC_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ACC_CODIGO TEXT NOT NULL UNIQUE, " +
                    "$ACC_NOMBRE TEXT NOT NULL, " +
                    "$ACC_OBS TEXT)"

        private const val SQL_CREAR_SESIONES =
            "CREATE TABLE $TABLA_SESIONES (" +
                    "$SES_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$SES_ACCESO INTEGER NOT NULL REFERENCES $TABLA_ACCESOS($ACC_ID), " +
                    "$SES_FECHA TEXT NOT NULL, " +
                    "$SES_INICIO TEXT NOT NULL, " +
                    "$SES_FIN TEXT NOT NULL, " +
                    "$SES_OBSERVADOR INTEGER NOT NULL REFERENCES $TABLA_COLABORADORES($COL_ID), " +
                    "$SES_ESTADO TEXT NOT NULL, " +
                    "$SES_VALIDADOR INTEGER REFERENCES $TABLA_COLABORADORES($COL_ID), " +
                    "$SES_MOTIVO TEXT)"

        private const val SQL_CREAR_REGISTROS =
            "CREATE TABLE $TABLA_REGISTROS (" +
                    "$REG_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$REG_SESION INTEGER NOT NULL REFERENCES $TABLA_SESIONES($SES_ID), " +
                    "$REG_HORA TEXT NOT NULL, " +
                    "$REG_CLASIFICACION TEXT NOT NULL, " +
                    "$REG_RANGO TEXT, " +
                    "$REG_COMPANIA TEXT, " +
                    "$REG_OTRA_TIENDA INTEGER NOT NULL DEFAULT 0, " +
                    "$REG_DESTINO INTEGER REFERENCES $TABLA_ACCESOS($ACC_ID), " +
                    "$REG_ANULADO INTEGER NOT NULL DEFAULT 0)"

        // SELECT base con INNER JOIN: une cada colaborador con SU perfil.
        private const val SQL_SELECT_COLABORADORES =
            "SELECT c.*, p.$PER_NOMBRE AS $PER_NOMBRE " +
                    "FROM $TABLA_COLABORADORES c " +
                    "INNER JOIN $TABLA_PERFILES p ON c.$COL_PERFIL = p.$PER_ID"
    }

    // Se ejecuta CADA VEZ que se abre la conexion: SQLite trae las claves foraneas APAGADAS.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Una sola vez, cuando rinconsur.db no existe. Primero las tablas "padre", despues las "hijas".
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_PERFILES)
        db.execSQL(SQL_CREAR_COLABORADORES)
        db.execSQL(SQL_CREAR_ACCESOS)
        db.execSQL(SQL_CREAR_SESIONES)
        db.execSQL(SQL_CREAR_REGISTROS)
        precargar(db)
    }

    // Si sube VERSION_BD: version simple, borra y recrea (se pierden los datos). Orden inverso.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_REGISTROS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SESIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_ACCESOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_COLABORADORES")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_PERFILES")
        onCreate(db)
    }

    // Ejecuta un SELECT y recorre el Cursor fila por fila. El SQL cambia, la lectura es la misma.
    private fun <T> consultar(sql: String, args: Array<String>?, leer: (Cursor) -> T): List<T> {
        val lista = mutableListOf<T>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) lista.add(leer(cursor))
        }
        return lista
    }

    private fun Cursor.texto(columna: String): String = getString(getColumnIndexOrThrow(columna))

    private fun Cursor.textoONulo(columna: String): String? {
        val i = getColumnIndexOrThrow(columna)
        return if (isNull(i)) null else getString(i)
    }

    private fun Cursor.bytesONulo(columna: String): ByteArray? {
        val i = getColumnIndexOrThrow(columna)
        return if (isNull(i)) null else getBlob(i)
    }

    private fun Cursor.entero(columna: String): Int = getInt(getColumnIndexOrThrow(columna))

    private fun Cursor.enteroONulo(columna: String): Int? {
        val i = getColumnIndexOrThrow(columna)
        return if (isNull(i)) null else getInt(i)
    }

    // Convierte la fila actual del Cursor en un objeto. nombre_perfil llega gracias al JOIN.
    private fun leerColaborador(c: Cursor) = Colaborador(
        id = c.entero(COL_ID),
        nombre = c.texto(COL_NOMBRE),
        correo = c.texto(COL_CORREO),
        clave = c.texto(COL_CLAVE),
        perfil = PerfilUsuario.entries.first { it.etiqueta == c.texto(PER_NOMBRE) },
        avatar = c.bytesONulo(COL_AVATAR)
    )

    private fun leerAcceso(c: Cursor) = Acceso(
        id = c.entero(ACC_ID),
        codigo = c.texto(ACC_CODIGO),
        nombre = c.texto(ACC_NOMBRE),
        observacion = c.textoONulo(ACC_OBS)
    )

    private fun leerSesion(c: Cursor) = SesionObservacion(
        id = c.entero(SES_ID),
        idAcceso = c.entero(SES_ACCESO),
        fecha = c.texto(SES_FECHA),
        horaInicio = c.texto(SES_INICIO),
        horaFin = c.texto(SES_FIN),
        idObservador = c.entero(SES_OBSERVADOR),
        estado = EstadoSesion.valueOf(c.texto(SES_ESTADO)),
        idValidador = c.enteroONulo(SES_VALIDADOR),
        motivoRechazo = c.textoONulo(SES_MOTIVO)
    )

    private fun leerRegistro(c: Cursor) = RegistroObservacion(
        id = c.entero(REG_ID),
        idSesion = c.entero(REG_SESION),
        hora = c.texto(REG_HORA),
        clasificacion = Clasificacion.valueOf(c.texto(REG_CLASIFICACION)),
        rangoEtario = c.textoONulo(REG_RANGO)?.let { RangoEtario.valueOf(it) },
        tipoCompania = c.textoONulo(REG_COMPANIA)?.let { TipoCompania.valueOf(it) },
        compraOtraTienda = c.entero(REG_OTRA_TIENDA) == 1,
        idAccesoDestino = c.enteroONulo(REG_DESTINO),
        anulado = c.entero(REG_ANULADO) == 1
    )

    // Datos de ejemplo (escritos en el codigo, en DatosEjemplo). Todo en una transaccion.
    private fun precargar(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            PerfilUsuario.entries.forEachIndexed { indice, perfil ->
                db.insert(TABLA_PERFILES, null, ContentValues().apply {
                    put(PER_ID, indice + 1)
                    put(PER_NOMBRE, perfil.etiqueta)
                })
            }
            DatosEjemplo.colaboradores.forEach { db.insert(TABLA_COLABORADORES, null, valoresDe(it, conId = true)) }
            DatosEjemplo.accesos.forEach { db.insert(TABLA_ACCESOS, null, valoresDe(it)) }
            DatosEjemplo.sesiones.forEach { db.insert(TABLA_SESIONES, null, valoresDe(it, conId = true)) }
            DatosEjemplo.registros().forEach { db.insert(TABLA_REGISTROS, null, valoresDe(it, conId = true)) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    // ContentValues = mapa columna -> valor (para INSERT y UPDATE)
    private fun valoresDe(c: Colaborador, conId: Boolean) = ContentValues().apply {
        if (conId) put(COL_ID, c.id)
        put(COL_NOMBRE, c.nombre)
        put(COL_CORREO, c.correo)
        put(COL_CLAVE, c.clave)
        put(COL_PERFIL, c.perfil.ordinal + 1)
    }

    private fun valoresDe(a: Acceso) = ContentValues().apply {
        put(ACC_ID, a.id)
        put(ACC_CODIGO, a.codigo)
        put(ACC_NOMBRE, a.nombre)
        put(ACC_OBS, a.observacion)
    }

    private fun valoresDe(s: SesionObservacion, conId: Boolean) = ContentValues().apply {
        if (conId) put(SES_ID, s.id)
        put(SES_ACCESO, s.idAcceso)
        put(SES_FECHA, s.fecha)
        put(SES_INICIO, s.horaInicio)
        put(SES_FIN, s.horaFin)
        put(SES_OBSERVADOR, s.idObservador)
        put(SES_ESTADO, s.estado.name)
        put(SES_VALIDADOR, s.idValidador)
        put(SES_MOTIVO, s.motivoRechazo)
    }

    private fun valoresDe(r: RegistroObservacion, conId: Boolean) = ContentValues().apply {
        if (conId) put(REG_ID, r.id)
        put(REG_SESION, r.idSesion)
        put(REG_HORA, r.hora)
        put(REG_CLASIFICACION, r.clasificacion.name)
        put(REG_RANGO, r.rangoEtario?.name)
        put(REG_COMPANIA, r.tipoCompania?.name)
        put(REG_OTRA_TIENDA, if (r.compraOtraTienda) 1 else 0)
        put(REG_DESTINO, r.idAccesoDestino)
        put(REG_ANULADO, if (r.anulado) 1 else 0)
    }

    // CREATE -> INSERT. Devuelve el id generado (o -1 si falla).
    fun insertarColaborador(colaborador: Colaborador): Long =
        writableDatabase.insert(TABLA_COLABORADORES, null, valoresDe(colaborador, conId = false))

    // READ -> todos los colaboradores con su perfil, ordenados por nombre
    fun listarColaboradores(): List<Colaborador> =
        consultar("$SQL_SELECT_COLABORADORES ORDER BY c.$COL_NOMBRE", null, ::leerColaborador)

    // READ con filtro: el correo viaja en el "?" (nunca concatenado) para evitar inyeccion SQL.
    fun buscarColaboradorPorCorreo(correo: String): Colaborador? =
        consultar("$SQL_SELECT_COLABORADORES WHERE c.$COL_CORREO = ?", arrayOf(correo), ::leerColaborador)
            .firstOrNull()

    fun buscarColaboradorPorId(id: Int): Colaborador? =
        consultar("$SQL_SELECT_COLABORADORES WHERE c.$COL_ID = ?", arrayOf(id.toString()), ::leerColaborador)
            .firstOrNull()

    // UPDATE de UNA sola columna: ContentValues acepta ByteArray y SQLite lo guarda como BLOB.
    // valoresDe() NO incluye el avatar: al crear un usuario no hay foto y nunca se pisa por error.
    fun actualizarAvatar(id: Int, avatar: ByteArray): Int =
        writableDatabase.update(
            TABLA_COLABORADORES,
            ContentValues().apply { put(COL_AVATAR, avatar) },
            "$COL_ID = ?", arrayOf(id.toString())
        )

    fun listarAccesos(): List<Acceso> =
        consultar("SELECT * FROM $TABLA_ACCESOS ORDER BY $ACC_CODIGO", null, ::leerAcceso)

    fun insertarSesion(sesion: SesionObservacion): Long =
        writableDatabase.insert(TABLA_SESIONES, null, valoresDe(sesion, conId = false))

    fun listarSesiones(): List<SesionObservacion> =
        consultar(
            "SELECT * FROM $TABLA_SESIONES ORDER BY $SES_FECHA DESC, $SES_INICIO DESC",
            null, ::leerSesion
        )

    fun buscarSesion(id: Int): SesionObservacion? =
        consultar("SELECT * FROM $TABLA_SESIONES WHERE $SES_ID = ?", arrayOf(id.toString()), ::leerSesion)
            .firstOrNull()

    // UPDATE: cambia SOLO la fila cuyo id_sesion coincide.
    fun actualizarSesion(sesion: SesionObservacion): Int =
        writableDatabase.update(
            TABLA_SESIONES, valoresDe(sesion, conId = false), "$SES_ID = ?", arrayOf(sesion.id.toString())
        )

    // Hay solapamiento si otra sesion del mismo acceso y dia se cruza con el horario pedido
    fun haySolapamiento(idAcceso: Int, fecha: String, inicio: String, fin: String): Boolean =
        readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLA_SESIONES WHERE $SES_ACCESO = ? AND $SES_FECHA = ? " +
                    "AND $SES_ESTADO <> ? AND $SES_INICIO < ? AND $SES_FIN > ? LIMIT 1",
            arrayOf(idAcceso.toString(), fecha, EstadoSesion.CANCELADA.name, fin, inicio)
        ).use { it.moveToFirst() }

    fun insertarRegistro(registro: RegistroObservacion): Long =
        writableDatabase.insert(TABLA_REGISTROS, null, valoresDe(registro, conId = false))

    // Registros de UNA sesion (filtro con "?")
    fun listarRegistros(idSesion: Int): List<RegistroObservacion> =
        consultar(
            "SELECT * FROM $TABLA_REGISTROS WHERE $REG_SESION = ? ORDER BY $REG_ID",
            arrayOf(idSesion.toString()), ::leerRegistro
        )

    // Los registros no se borran: se marcan como anulados UPDATE de una columna.
    fun anularRegistro(id: Int): Int =
        writableDatabase.update(
            TABLA_REGISTROS,
            ContentValues().apply { put(REG_ANULADO, 1) },
            "$REG_ID = ?", arrayOf(id.toString())
        )
}