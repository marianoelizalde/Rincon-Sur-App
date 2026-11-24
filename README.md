# Rincón Sur — App móvil

Aplicación Android para registrar y validar sesiones de observación de flujo de personas (tránsito y visitas) en los accesos de un centro comercial. Los supervisores programan y validan las sesiones; los observadores registran en terreno a cada persona observada.

Curso DSY1105 Desarrollo de Aplicaciones Móviles, Duoc UC.

## Integrantes

- Mariano Elizalde
- Lukas LLanten

## Tecnologías

- Kotlin y Jetpack Compose
- Arquitectura MVVM (ViewModel, StateFlow, repositorios)
- Navigation Compose
- Retrofit 3.0.0 con converter Gson y OkHttp
- Splash screen (core-splashscreen)
- minSdk 24, targetSdk 37
- Pruebas unitarias con JUnit 4

## Funcionalidades

Sesión de usuario
- Inicio de sesión y recuperación de contraseña con correo y RUT.
- Perfil con foto (cámara o galería), edición de datos y cambio de tema claro u oscuro.

Supervisor
- Crear, editar, activar o desactivar usuarios y cambiar su rol.
- Programar sesiones de observación eligiendo acceso, observador, fecha y horario.
- Validar o rechazar sesiones finalizadas, indicando el motivo.

Observador
- Iniciar y finalizar sesiones.
- Registrar personas observadas (tránsito o visita, rango de edad, compañía, sexo y si compra en otra tienda).
- Deshacer el último registro o anular uno puntual.

## Arquitectura de la app

```
cl.duocuc.rinconsur
├── data            repositorios, preferencias y utilidades
│   └── remote      interfaces Retrofit, modelos de la API y RetrofitClient
├── model           modelos y estados de pantalla (UiState)
├── navigation      rutas y navegación
├── nativo          vibración
├── permisos        permisos de cámara y galería
├── ui              pantallas, componentes y tema
└── viewmodel       ViewModels
```

## Microservicios y API externa

La app consume un único punto de entrada: el API Gateway, en `http://10.0.2.2:9090/` (la dirección del PC vista desde el emulador). El gateway resuelve cada microservicio en Eureka, por lo que la app no conoce los puertos de los servicios. La conexión se declara en `data/remote/RetrofitClient.kt`.

Endpoints que usa la app, todos a través del gateway:

| Servicio | Endpoint |
|---|---|
| Perfiles | `GET /api/perfiles` |
| Usuarios | `GET /api/usuarios`, `GET /api/usuarios/{id}`, `POST /api/usuarios`, `POST /api/usuarios/login`, `POST /api/usuarios/recuperar`, `PUT /api/usuarios/{id}`, `PATCH /api/usuarios/{id}/activo`, `PATCH /api/usuarios/{id}/perfil`, `POST /api/usuarios/{id}/avatar`, `GET /api/usuarios/{id}/avatar` |
| Sesiones | `GET /api/sesiones`, `GET /api/sesiones/{id}`, `POST /api/sesiones`, `PATCH /api/sesiones/{id}/iniciar`, `/finalizar`, `/cancelar`, `/validar`, `/rechazar` |
| Accesos y estados | `GET /api/accesos`, `GET /api/estados-sesion` |
| Registros | `POST /api/registros`, `PATCH /api/registros/{id}/anular`, `GET /api/registros/sesion/{sesionId}`, `POST /api/registros/sesion/{sesionId}/deshacer-ultimo` |

Las acciones que requieren saber quién las hace llevan el encabezado `X-Usuario-Id`, que un interceptor de OkHttp agrega después del inicio de sesión.

El código y la documentación de los microservicios están en el repositorio del backend: https://github.com/marianoelizalde/Rincon-Sur-Backend

## Cómo usar la app (paso a paso, muy fácil)

### 0. Antes de empezar
1. Prende el backend (los microservicios) en el computador.
2. Abre la app **Rincón Sur** en el celular o emulador.

### 1. Entrar
1. Escribe tu correo.
2. Escribe tu contraseña.
3. Toca el botón **Ingresar**.

Usuarios de prueba:
- Supervisor: `supervisor@rinconsur.cl` / `Admin123!`
- Observador: `observador@rinconsur.cl` / `Obser123!`

Si olvidaste la contraseña, toca **¿Olvidaste tu contraseña?**, escribe tu correo y tu RUT, y elige una nueva.

### 2. Moverte por la app
1. Toca las tres rayitas de arriba (**Abrir menú**).
2. Ahí están: **Sesiones**, **Mi perfil**, **Crear usuario** (solo el supervisor) y **Cerrar sesión**.

### 3. Si eres Supervisor
**Crear a una persona nueva**
1. Menú → **Crear usuario**.
2. Llena nombre, correo, contraseña y rol.
3. Toca **Crear usuario**.

**Programar un día de observación**
1. Menú → **Sesiones**.
2. Toca **Programar sesión**.
3. Elige el acceso (Principal, Lateral, Estacionamiento o Conexión).
4. Escribe la fecha (ejemplo `2026-12-01`), la hora de inicio (desde las 10:00) y la hora de término (entre 30 y 60 minutos después, hasta las 21:00).
5. Elige al observador y escribe el motivo.
6. Toca **Programar**.

**Revisar el trabajo terminado**
1. En **Sesiones**, toca una sesión que diga **Finalizada**.
2. Si la muestra tiene al menos 30 personas, toca **Validar sesión**.
3. Si no te convence, toca **Rechazar sesión**.

### 4. Si eres Observador
**Contar personas**
1. Menú → **Sesiones**.
2. Toca tu sesión (solo se puede empezar el día y a la hora que se programó).
3. Toca **Iniciar sesión de observación**.
4. Toca **Registrar personas**.

**Anotar a cada persona que pasa**
1. Elige si es **Tránsito** (solo pasa) o **Visita** (entra).
2. Elige su edad: Niño, Joven, Adulto, Maduro o Anciano.
3. Elige con quién va: Solo, Pareja, Con niños o Grupo.
4. Elige Masculino o Femenino.
5. Si compró en otra tienda, marca **Compró en otra tienda**.
6. Toca **Registrar persona**.
7. Si te equivocaste, toca **Deshacer último registro**.

**Terminar**
1. Vuelve al detalle de la sesión.
2. Toca **Finalizar sesión**.
3. Espera a que el supervisor la valide.

### 5. Mi perfil
1. Menú → **Mi perfil**.
2. Para poner tu foto toca **Cámara** o **Galería**.
3. Para cambiar tus datos toca **Cambiar datos**, escribe lo nuevo y toca **Guardar cambios**.
4. En **Apariencia** elige el tema: Sistema, Claro u Oscuro.

### 6. Buscar sesiones
1. En **Sesiones** usa los filtros: fecha, estado, acceso, turno u observador.
2. Para borrar los filtros toca **Limpiar filtros**.

### 7. Salir
1. Menú → **Cerrar sesión**.
2. Confirma en el mensaje que aparece.

## Pasos para ejecutar

1. Levantar el backend siguiendo el README del repositorio del backend (MySQL, Eureka, los 4 microservicios y el gateway).
2. Abrir este proyecto en Android Studio y esperar la sincronización de Gradle.
3. Elegir un emulador y presionar Run. La URL `http://10.0.2.2:9090/` funciona en cualquier PC.
4. Con un celular físico, cambiar `URL_BASE` en `RetrofitClient.kt` por `http://IP-DEL-PC:9090/` (misma red Wi-Fi), o conectar por USB y ejecutar `adb reverse tcp:9090 tcp:9090` usando `http://localhost:9090/`.

Usuarios de prueba que crea el backend la primera vez:

| Rol | Correo | Contraseña |
|---|---|---|
| Supervisor | supervisor@rinconsur.cl | Admin123! |
| Observador | observador@rinconsur.cl | Obser123! |

## Pruebas unitarias

Hay 10 pruebas en `app/src/test`, en tres archivos: `ProgramarViewModelTest`, `RegistroViewModelTest` y `SesionRepositoryTest`. Usan APIs falsas (`FakeSesionApi`, `FakeAccesoApi`, `FakeEstadoApi`) en lugar de la red.

Para ejecutarlas: clic derecho en la carpeta `test` y Run Tests, o `./gradlew testDebugUnitTest`.

## APK firmado

El APK y el AAB se generan con Build > Generate Signed App Bundle or APK, usando una llave `.jks` propia (alias `rinconsur`, validez de 25 años). Los archivos entregados son `app-release.apk`, `app-release.aab` y `rinconsur.jks`. El `.jks` no se sube a GitHub: está en `.gitignore` junto con `*.keystore`, `keystore.properties` y `/app/release`, y se entrega aparte con el APK.

La versión es `versionName = "1.0.0"` y `versionCode = 1`.

Captura de los archivos firmados:

![APK firmado y llave jks](docs/apk-firmado-y-jks.png)

## Organización del equipo

La planificación de tareas está en el tablero de Trello del equipo, y el trabajo de cada integrante se ve en los commits de este repositorio y del repositorio del backend.
