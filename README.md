Información del Proyecto

Nombre: MyBodega
Integrantes:
- Maximiliano Martinez
- Yetro Valenzuela

Descripción: Aplicación móvil Android para gestión de inventario doméstico con sincronización en la nube, desarrollada en Kotlin con Jetpack Compose.

---

## Funcionalidades Principales

### Autenticación
- Registro de nuevos usuarios
- Login con validación de credenciales
- Persistencia de sesión con DataStore
- Pantalla de bienvenida adaptativa

### Gestión de Inventario
- Crear productos con foto desde cámara
- Editar información de productos
- Eliminar productos con confirmación
- Visualizar inventario completo
- Consumir y reabastecer stock
- Búsqueda por nombre o categoría
- Indicadores visuales de stock bajo

### Sincronización
- Modo offline: los productos se guardan localmente
- Modo online: sincronización automática con servidor
- Botón manual de sincronización
- Indicadores visuales de productos sin sincronizar
- Subir productos locales al servidor individualmente

### Reportes y Estadísticas
- Dashboard con métricas generales
- Productos con stock bajo y agotados
- Distribución por categorías
- Top 5 productos con mayor stock
- Gráficos visuales informativos

### Historial de Movimientos
- Registro de todas las operaciones
- Filtros por tipo de movimiento
- Vista cronológica
- Estadísticas de actividad

### Diseño Adaptativo
- Soporte para móviles (Compact)
- Soporte para tablets (Medium)
- Soporte para pantallas grandes (Expanded)
- Tema claro y oscuro con transiciones suaves

---

## Endpoints Utilizados

### Endpoints Propios (Backend MyBodega)

Base URL: `http://10.0.2.2:8080` (emulador) o `http://TU_IP:8080` (dispositivo físico)

#### Productos
```
GET    /api/productos
POST   /api/productos
PUT    /api/productos/{id}
DELETE /api/productos/{id}
POST   /api/productos/consumir
POST   /api/productos/reabastecer
```

#### Usuarios
```
POST   /api/usuarios/login
POST   /api/usuarios/register
```

#### Movimientos
```
GET    /api/movimientos/recientes
DELETE /api/movimientos/limpiar
Servicios Externos

Coil: Carga de imágenes (https://coil-kt.github.io)
FileProvider: Captura de fotos con cámara (Android SDK)


Requisitos del Sistema
Desarrollo

Android Studio: Hedgehog (2023.1.1) o superior
JDK: 17 o superior
Gradle: 8.10.2
Min SDK: 24 (Android 7.0)
Target SDK: 34 (Android 14)

Dispositivo

Android: 7.0 (Nougat) o superior
Permisos: Cámara (para fotos de productos)
Conexión: Internet para sincronización


Instrucciones de Instalación
1. Clonar el Repositorio
bashgit clone <url-repositorio-android>
cd mybodega_grupo9
2. Configurar URL del Backend
Editar app/src/main/java/.../data/remote/RetrofitClient.kt:
kotlin// Para emulador
private const val BASE_URL = "http://10.0.2.2:8080/"

// Para dispositivo físico (reemplazar con tu IP local)
private const val BASE_URL = "http://192.168.1.100:8080/"
3. Abrir en Android Studio

File → Open → Seleccionar carpeta del proyecto
Esperar sincronización de Gradle
Build → Make Project

4. Ejecutar la Aplicación
En Emulador:
bash# Crear AVD (Android Virtual Device) desde AVD Manager
# Ejecutar con botón "Run" o Shift+F10
En Dispositivo Físico:

Habilitar "Opciones de desarrollador" en el dispositivo
Activar "Depuración USB"
Conectar dispositivo por USB
Autorizar conexión
Ejecutar desde Android Studio


Generar APK
APK de Depuración (Debug)
bash# Opción 1: Desde Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)

# Opción 2: Desde terminal
./gradlew assembleDebug

# APK generado en:
app/build/outputs/apk/debug/app-debug.apk
APK Firmado (Release)
1. Generar Keystore
bashkeytool -genkey -v -keystore mybodega.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias mybodega-key
Guardar información:

Ruta del archivo: mybodega.jks
Contraseña del keystore: [TU_CONTRASEÑA]
Alias: mybodega-key
Contraseña del alias: [TU_CONTRASEÑA]

2. Configurar Firma
Crear archivo keystore.properties en la raíz del proyecto:
propertiesstorePassword=TU_CONTRASEÑA
keyPassword=TU_CONTRASEÑA
keyAlias=mybodega-key
storeFile=../mybodega.jks
Editar app/build.gradle.kts:
kotlinandroid {
    signingConfigs {
        create("release") {
            storeFile = file("../mybodega.jks")
            storePassword = "TU_CONTRASEÑA"
            keyAlias = "mybodega-key"
            keyPassword = "TU_CONTRASEÑA"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... resto de configuración
        }
    }
}
3. Generar APK Release
bash# Desde terminal
./gradlew assembleRelease

# APK generado en:
app/build/outputs/apk/release/app-release.apk
4. Instalar APK en Dispositivo
bash# Via ADB
adb install app/build/outputs/apk/release/app-release.apk

# O copiar APK al dispositivo e instalar manualmente
```

---

## Ubicación de Archivos Importantes
```
mybodega_grupo9/
├── mybodega.jks                    # Keystore para firma (generar)
├── keystore.properties             # Configuración de firma (crear)
├── app/
│   ├── build.gradle.kts           # Configuración de app
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/.../
│           │   ├── data/          # Repositorios y modelos
│           │   ├── ui/            # Pantallas Compose
│           │   ├── viewmodel/     # ViewModels
│           │   └── navigation/    # Navegación
│           └── res/               # Recursos (layouts, strings)
└── build/
    └── outputs/
        └── apk/
            ├── debug/             # APKs de depuración
            └── release/           # APKs firmados
```

---

## Arquitectura de la App

### Patrón MVVM
```
View (Compose UI)
    ↓
ViewModel (Estado y lógica)
    ↓
Repository (Acceso a datos)
    ↓
Data Sources (Room + Retrofit)
Capas
Presentación (UI):

ui/screen/: Pantallas Compose
ui/theme/: Temas y estilos

Lógica (ViewModel):

viewmodel/: ViewModels con StateFlow

Datos (Data):

data/local/: Room Database (offline)
data/remote/: Retrofit (API)
data/: Repositorios (coordinan local + remoto)


Tecnologías Utilizadas
Android

Kotlin 2.0.21
Jetpack Compose - UI moderna
Material 3 - Design system
Navigation Compose - Navegación

Persistencia

Room 2.6.1 - Base de datos local
DataStore - Preferencias de usuario

Red

Retrofit 2.11.0 - Cliente HTTP
OkHttp 4.12.0 - Logging de red
Gson 2.11.0 - Serialización JSON

Imágenes

Coil 2.5.0 - Carga de imágenes
CameraX - Captura de fotos

Testing

JUnit5 - Tests unitarios
MockK 1.13.13 - Mocking
Kotest 5.9.1 - Assertions


Funcionalidades de Sincronización
Estrategia Offline-First

Creación Offline:

Producto se guarda en Room con ID temporal (> 1000000)
Badge "OFFLINE" visible en la card
Botón "Subir al Servidor" habilitado


Sincronización Manual:

Usuario presiona botón "Subir" en cada producto
O botón de sincronización general en toolbar
Progreso visible durante subida


Sincronización Automática:

Al abrir DetailsScreen si hay conexión
Productos locales se suben automáticamente
ID temporal se reemplaza por ID del servidor



Indicadores Visuales

Badge OFFLINE: Producto no sincronizado
Botón CloudUpload: Subir producto individual
Icono Sync en toolbar: Sincronizar todos
Contador "Sin Subir": Cantidad de productos locales


Estructura de Base de Datos Local
ProductoEntity
kotlin- id: Int (PK, autoincremental)
- nombre: String
- categoria: String
- cantidad: Int
- descripcion: String?
- ubicacion: String?
- imagenUri: String?
MovimientoEntity
kotlin- id: Int (PK, autoincremental)
- tipo: String
- producto: String
- fecha: Long

Permisos Requeridos
xml<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

Solución de Problemas
Error de conexión a API
Problema: No se puede conectar al backend
Soluciones:

Verificar que el backend esté ejecutándose
Emulador: usar 10.0.2.2 en lugar de localhost
Dispositivo físico: usar IP local de tu PC
Verificar firewall y permisos de red

La cámara no funciona
Problema: Crash al tomar foto
Soluciones:

Verificar permisos de cámara en configuración
Reinstalar app para solicitar permisos nuevamente
Verificar que FileProvider esté configurado correctamente

Productos no se sincronizan
Problema: Badge OFFLINE permanece
Soluciones:

Verificar conexión a internet
Revisar logs de Retrofit en Logcat
Confirmar que backend responde correctamente
Usar botón de sincronización manual

Build fallido
Problema: Error al compilar
Soluciones:
bash# Limpiar proyecto
./gradlew clean

# Invalidar caché de Android Studio
File → Invalidate Caches → Invalidate and Restart

# Sincronizar Gradle
File → Sync Project with Gradle Files

Testing
Ejecutar Tests
bash# Todos los tests
./gradlew test

# Solo tests unitarios
./gradlew testDebugUnitTest

# Ver reporte
# Abrir: app/build/reports/tests/testDebugUnitTest/index.html
Cobertura de Tests

DTOConversionTest: Validación de DTOs
ProductoEntityTest: Modelo de producto
MovimientoEntityTest: Modelo de movimiento
ProductoRepositoryTest: Lógica de repositorio
ProductoViewModelTest: Lógica de ViewModel
AuthViewModelTest: Autenticación
MovimientoViewModelTest: Historial


Modo Offline vs Online
Modo Offline

✅ Crear productos localmente
✅ Editar productos
✅ Eliminar productos
✅ Ver inventario desde Room
❌ No sincroniza con servidor
❌ No se comparten entre dispositivos

Modo Online

✅ Todas las funciones offline
✅ Sincronización bidireccional
✅ Datos compartidos entre dispositivos
✅ Backup en la nube
✅ Historial de movimientos del servidor


Características de UI/UX
Diseño Responsivo

Compact (< 600dp): Layout vertical, navegación por pantallas
Medium (600-840dp): Layout mixto, dos columnas
Expanded (> 840dp): Layout horizontal, grid de cards grandes

Tema

Modo Claro: Fondos blancos, acentos azules
Modo Oscuro: Fondos negros, acentos cyan
Transiciones: Animaciones suaves entre temas
Botón FAB: Cambio de tema en MainActivity

Animaciones

Transiciones entre pantallas
Aparición/desaparición de elementos
Feedback visual en botones
Indicadores de carga

Evidencia de Trabajo Colaborativo
El proyecto fue desarrollado colaborativamente por Maximiliano Martinez y Yetro Valenzuela. La evidencia de participación se encuentra en:

Commits del repositorio: Ambos integrantes realizaron commits en diferentes componentes
Distribución de trabajo:

Maximiliano: Backend API, sincronización, testing
Yetro: UI/UX, pantallas Compose, ViewModels


Revisión de código: Pull requests y code reviews entre integrantes


Licencia
Este proyecto es de carácter académico desarrollado para el curso de Desarrollo Móvil.

Contacto
Para consultas sobre el proyecto:

Maximiliano Martinez
Yetro Valenzuela

Universidad: [Nombre de tu universidad]
Curso: Desarrollo de Aplicaciones Móviles
Año: 2025
