# Rincón Sur

App móvil Android para **segmentar el flujo de clientes por acceso** en la
Tienda Departamental Rincón Sur, una tienda dentro de un mall con 4 accesos.

Evaluación Parcial 2 · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC

## Integrantes
- Mariano Elizalde
- Lukas LLanten

## Enlaces
- Tablero Trello: [PEGAR ENLACE AQUÍ]
- Repositorio: https://github.com/marianoelizalde/Rincon-Sur-App

---

## 1. ¿De qué trata?

La tienda quiere saber cuánta gente entra por cada acceso y de qué tipo es.
Para eso hay **sesiones de observación**: una persona se para en un acceso
durante un horario y registra a cada persona que pasa.

| Acceso | Nombre | Ubicación |
|---|---|---|
| A1 | Principal | Frente al pasillo central del mall |
| A2 | Lateral | Patio de comidas |
| A3 | Estacionamiento | Señal demasiado débil |
| A4 | Conexión | Hacia la tienda vecina |

Cada persona se registra con:
- **Clasificación:** tránsito (solo pasa) o visita (viene a la tienda).
- **Rango de edad:** niño, joven, adulto, maduro o anciano.
- **Sexo:** masculino o femenino.
- **Compañía:** solo, pareja, con niños o grupo.

Con esos datos la app calcula, por sesión, el porcentaje de tránsito y la
cantidad de personas registradas frente al mínimo de la muestra.

## 2. Funcionalidades

- Inicio de sesión con validación de campos.
- **Recuperar cuenta** desde el login (correo y nombre como segunda comprobación).
- Crear usuarios (solo el supervisor).
- Programar sesiones por acceso, fecha y horario, sin cruces en el mismo acceso.
- Lista de sesiones con **filtro por fecha y estado**.
- Ciclo de la sesión: iniciar, registrar personas, deshacer el último registro,
  finalizar, y luego validar o rechazar (supervisor).
- Registro de personas con **botones grandes con ícono**.
- Perfil con **foto desde cámara o galería** y **cambio de nombre**.
- Tema claro, oscuro o del sistema.
- Vista simple de cada sesión para el observador.
- Animaciones en la interfaz.

### Estados de una sesión
Programada → En curso → Finalizada → Validada o Rechazada.
Una sesión también puede quedar Cancelada.

## 3. Cómo se usa (explicado fácil)

Imagina que quieres saber cuánta gente entra por cada puerta de la tienda.
Alguien se para en la puerta, mira y anota. Esta app es la libreta donde se anota.

Hay dos tipos de personas:
- **Observador:** el que se para en la puerta y anota a la gente.
- **Supervisor:** el jefe, que organiza el trabajo y revisa que esté bien hecho.

### Usuarios de prueba
| Quién | Correo | Clave | Tipo |
|---|---|---|---|
| Mariano Elizalde | mariano@rinconsur.cl | Rincon2026 | Supervisor |
| Lukas LLanten | lukas@rinconsur.cl | Rincon2026 | Observador |

### Paso 1: entrar
1. Abre la app. Verás el logo de Rincón Sur.
2. Escribe tu correo y tu clave.
3. Toca **Ingresar**.
4. Si no te acuerdas de tu clave, toca **¿Olvidaste tu contraseña?**,
   escribe tu correo y tu nombre, y pon una clave nueva.

### Paso 2: lo que hace el supervisor
1. **Crear usuario:** en el menú de la izquierda (las tres rayitas),
   toca **Crear usuario** para agregar a un nuevo observador.
2. **Programar sesión:** elige en qué puerta (acceso), qué día y a qué hora
   va a observar alguien.
3. **Buscar sesiones:** en **Sesiones** escribe una fecha o toca un estado
   para encontrar una sesión más rápido. **Limpiar** borra el filtro.
4. **Revisar el trabajo:** cuando el observador termina, entra a la sesión
   y toca **Validar sesión** si está bien, o **Rechazar sesión** si está mal
   (y explica por qué).

### Paso 3: lo que hace el observador
1. Entra y verás **sus** sesiones. Toca la que le toca hoy.
2. Toca **Iniciar sesión de observación** cuando ya esté parado en el acceso.
3. Toca **Registrar personas**.
4. Cada vez que pasa alguien, toca los botones con dibujitos: si solo pasa
   o viene a la tienda, su edad, si es hombre o mujer, y si viene solo,
   en pareja, con niños o en grupo. Luego toca **Registrar persona**.
5. Si te equivocas, toca **Deshacer último registro**.
6. Cuando termina la hora, toca **Finalizar sesión**.
   El supervisor la revisará después.

### Paso 4: mi perfil
- Toca **Mi perfil** en el menú.
- **Cámara** o **Galería:** para ponerte una foto.
- **Cambiar nombre:** aparece un campo para escribir el nombre nuevo y **Guardar cambios**.
- También puedes elegir el tema (claro u oscuro) y **Cerrar sesión**.

## 4. Tecnologías

| Tema | Qué se usa |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose (sin layouts XML) y Material 3 |
| Arquitectura | MVVM con `UiState` y `StateFlow` |
| Navegación | Navigation Compose |
| Almacenamiento local | SQLite (`RinconDbHelper`) y SharedPreferences |
| Recursos nativos | Cámara, galería (con permisos en tiempo de ejecución) y vibración |
| SDK | minSdk 24 (Android 7.0), versión de la app 1.0 |

## 5. Estructura del proyecto

Paquete base: `cl.duocuc.rinconsur`

```
app/src/main/java/cl/duocuc/rinconsur/
├── MainActivity.kt
├── data/         Base de datos SQLite, preferencias, datos de ejemplo
├── model/        Clases de datos y estados de pantalla (UiState)
├── viewmodel/    Login, CrearUsuario, Programar, Registro y RinconViewModel
├── navigation/   Rutas y AppNavegacion
├── nativo/       Vibración
├── permisos/     Verificar y pedir permisos
└── ui/
    ├── screens/     Una pantalla por archivo
    ├── components/  Botones, campos, tarjetas, menú lateral
    ├── styles/      Estilos de botón, campo y tarjeta
    └── theme/       Colores, tipografía y dimensiones
```

### Cómo se conectan las capas
- **Pantalla** (`ui/screens`): dibuja y avisa lo que hizo el usuario.
- **ViewModel de formulario:** valida los campos de su pantalla.
- **`RinconViewModel`:** aplica las reglas del negocio y guarda en SQLite.
- **`RinconDbHelper`:** hace las consultas y los INSERT/UPDATE.

## 6. Colores

Paleta verde pensada para una tienda de retail y para cansar menos la vista:
fondos suaves y colores de estado con contraste suficiente
(texto 4,5:1 y elementos de interfaz 3:1, según WCAG).
Los íconos toman el color del tema, así que se adaptan al modo claro y al oscuro.

## 7. Cómo ejecutarlo

1. Clonar el repositorio:
   ```
   git clone https://github.com/marianoelizalde/Rincon-Sur-App.git
   ```
2. Abrir la carpeta con **Android Studio**.
3. Esperar la sincronización de Gradle.
4. Ejecutar (**Run**) en un emulador o en un teléfono con Android 7.0 o superior.
5. Entrar con uno de los usuarios de prueba.

Si cambió la base de datos y la app se comporta raro, desinstalar la app del
dispositivo y volver a ejecutarla para que se cree de nuevo.

## 8. Permisos

| Permiso | Para qué |
|---|---|
| `CAMERA` | Tomar la foto de perfil |
| `READ_MEDIA_IMAGES` (Android 13+) | Elegir una foto de la galería |
| `READ_EXTERNAL_STORAGE` (hasta Android 12) | Elegir una foto de la galería |
| `VIBRATE` | Vibración del teléfono |

Si el usuario rechaza un permiso, la app muestra un aviso con el botón
**Ajustes** para activarlo después. No se cae si falta la cámara.

## 9. Organización del trabajo

- Tablero Trello con las tareas del equipo: [PEGAR ENLACE AQUÍ]
- Repositorio en GitHub con commits pequeños, uno por cambio.
