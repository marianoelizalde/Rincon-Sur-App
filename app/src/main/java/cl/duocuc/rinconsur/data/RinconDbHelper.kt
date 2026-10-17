package cl.duocuc.rinconsur.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        private const val VERSION_BD = 1

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
                    "$COL_PERFIL INTEGER NOT NULL REFERENCES $TABLA_PERFILES($PER_ID))"

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
}