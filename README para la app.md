# DJSM Player 🎧

Reproductor de música **100% offline** para Android, construido con **Kotlin + Jetpack Compose + Media3/ExoPlayer**.

El objetivo de DJSM Player es funcionar como un reproductor moderno estilo YouTube Music en experiencia de uso, pero **sin depender de Internet, servidores, APIs externas ni cuentas de usuario**. Toda la biblioteca, reproducción, metadata y portadas se obtienen directamente del dispositivo.

> Este README funciona también como documento de continuidad del proyecto. Si se abre el repositorio en otro IDE o se continúa con un agente de desarrollo, este archivo describe la arquitectura, lo ya implementado, lo pendiente y las reglas que no deben romperse.

---

# 1. Objetivo del proyecto

Construir un reproductor de música Android profesional que:

- Funcione completamente offline.
- Detecte automáticamente la música almacenada en el dispositivo.
- Reproduzca archivos locales mediante Media3/ExoPlayer.
- Mantenga reproducción en segundo plano.
- Se integre con los controles multimedia de Android.
- Soporte cola de reproducción.
- Permita Play, Pause, Seek, Previous, Next, Shuffle y Repeat.
- Muestre metadata y portadas locales cuando existan.
- Funcione con bibliotecas grandes.
- Permita organizar música por canciones, álbumes, artistas, géneros y carpetas.
- Permita favoritos, playlists, historial y estadísticas locales.
- No dependa de Spotify, YouTube, Last.fm, Firebase ni servicios externos.
- Mantenga una arquitectura limpia, testeable y escalable.

---

# 2. Filosofía del proyecto

DJSM Player NO es una demo.

Las decisiones deben priorizar:

1. **Correctitud**
2. **Arquitectura mantenible**
3. **Compatibilidad con Android moderno**
4. **Experiencia offline**
5. **Rendimiento**
6. **Privacidad**
7. **Diseño profesional**

Regla principal:

```text
Funcionalidad
    ↓
Build
    ↓
Prueba real
    ↓
Edge cases
    ↓
Commit
    ↓
Siguiente feature
```

No avanzar dejando errores de compilación pendientes.

---

# 3. Stack actual

## Lenguaje

- Kotlin

## UI

- Jetpack Compose
- Material 3

## Reproducción

- AndroidX Media3
- ExoPlayer
- MediaSession
- MediaSessionService
- MediaController

## Navegación

- Navigation 3

## Dependency Injection

- Hilt
- KSP

## Arquitectura

- Repository Pattern
- Use Cases
- ViewModel
- StateFlow
- UI State
- Clean separation entre Data / Domain / UI

## Biblioteca local

- Android MediaStore
- ContentResolver

## Portadas y metadata

- TagLib
- MediaMetadataRetriever
- MediaStore thumbnails
- Caché LRU en memoria

---

# 4. Versiones configuradas actualmente

El proyecto se ha trabajado con aproximadamente estas versiones:

```text
AGP:             9.3.2
Kotlin:          2.2.10
Compose BOM:     2026.08.00
Media3:          1.11.0
Navigation 3:    1.1.6
Hilt:            2.60.1
KSP:             2.3.10
TagLib:          1.0.6
Lifecycle:       2.11.0
AndroidX Hilt:   1.3.0
```

No cambiar versiones sin una razón concreta.

---

# 5. Package principal

```text
com.djsm.player
```

Este package NO debe cambiarse durante el desarrollo normal.

---

# 6. Arquitectura objetivo

```text
com.djsm.player
│
├── core
│   ├── permission
│   ├── common
│   └── designsystem          # futuro
│
├── data
│   ├── local
│   │   ├── MediaStoreAudioDataSource.kt
│   │   └── ArtworkLoader.kt
│   │
│   ├── mapper                # futuro / según necesidad
│   │
│   └── repository
│       └── MediaStoreMusicRepository.kt
│
├── domain
│   ├── model
│   │   └── Song.kt
│   │
│   ├── repository
│   │   └── MusicRepository.kt
│   │
│   └── usecase
│       └── GetSongsUseCase.kt
│
├── di
│   └── RepositoryModule.kt
│
├── navigation
│   └── AppRoute.kt
│
├── playback
│   ├── PlaybackService.kt
│   ├── PlaybackController.kt
│   ├── PlaybackUiState.kt
│   ├── PlaybackState.kt
│   └── MediaItemMapper.kt
│
├── ui
│   ├── components
│   │   ├── ArtworkImage.kt
│   │   ├── MiniPlayer.kt
│   │   └── SongListItem.kt
│   │
│   ├── library
│   │   ├── LibraryScreen.kt
│   │   ├── LibraryRoute.kt
│   │   ├── LibraryUiState.kt
│   │   └── LibraryViewModel.kt
│   │
│   ├── permission
│   │   └── AudioPermissionScreen.kt
│   │
│   ├── player
│   │   └── NowPlayingScreen.kt
│   │
│   ├── theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   │
│   └── DJSMPlayerApp.kt
│
├── DJSMPlayerApplication.kt
└── MainActivity.kt
```

---

# 7. Dirección de dependencias

La dirección correcta debe ser:

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository interface
 ↓
Repository implementation
 ↓
DataSource
 ↓
Android APIs
```

Ejemplo real:

```text
LibraryRoute
    ↓
LibraryViewModel
    ↓
GetSongsUseCase
    ↓
MusicRepository
    ↓
MediaStoreMusicRepository
    ↓
MediaStoreAudioDataSource
    ↓
MediaStore
```

Evitar:

```text
LibraryScreen → MediaStore
LibraryScreen → ContentResolver
Domain → Context
Domain → android.*
ViewModel → ExoPlayer directo
```

---

# 8. Estado actual confirmado

## Proyecto base

- [x] Proyecto Android creado.
- [x] Kotlin.
- [x] Jetpack Compose.
- [x] Package `com.djsm.player`.
- [x] Git inicializado.
- [x] Rama principal `main`.
- [x] Build por Gradle funcionando.

## Permisos

- [x] `READ_MEDIA_AUDIO` para Android 13+.
- [x] `READ_EXTERNAL_STORAGE` hasta Android 12L.
- [x] Runtime permission.
- [x] Pantalla de solicitud de permiso.
- [x] Helper `audioPermission()`.
- [x] Sin permiso `INTERNET`.

## Biblioteca local

- [x] Lectura de MediaStore.
- [x] Modelo `Song`.
- [x] `MediaStoreAudioDataSource`.
- [x] Lectura de:
  - título
  - artista
  - álbum
  - albumId
  - duración
  - track
  - año
  - MIME type
  - date added
  - content URI
- [x] Filtro para contenido musical.
- [x] Lista de canciones con `LazyColumn`.
- [x] Metadata desconocida normalizada.
- [x] Probado con biblioteca real en teléfono Samsung.
- [x] Probado con decenas de archivos locales.

## Reproducción

- [x] Media3.
- [x] ExoPlayer.
- [x] `PlaybackService`.
- [x] `MediaSessionService`.
- [x] `MediaSession`.
- [x] `MediaController`.
- [x] Play.
- [x] Pause.
- [x] Seek.
- [x] Position tracking.
- [x] Duration tracking.
- [x] Reproducción en segundo plano.
- [x] Controles multimedia de Android.
- [x] Metadata visible en controles del sistema.
- [x] Reproducción continúa al salir de la app.
- [x] Mini Player.
- [x] Now Playing.
- [x] Slider de progreso.
- [x] Navegación Library → Now Playing.
- [x] Previous.
- [x] Next.
- [x] Queue real con toda la biblioteca.
- [x] Shuffle.
- [x] Repeat All.
- [x] Repeat One.

## Artwork

- [x] Artwork real cuando está disponible.
- [x] TagLib.
- [x] MediaMetadataRetriever.
- [x] MediaStore thumbnail.
- [x] Album MediaStore thumbnail.
- [x] Fallback cuando no existe artwork.
- [x] Caché LRU en memoria.
- [x] Caché de "sin artwork".
- [x] Artwork en Now Playing.
- [x] Artwork en Mini Player.
- [x] Artwork en Song List.
- [x] Probado con archivos reales que sí contienen portada.

## Arquitectura

- [x] `MusicRepository`.
- [x] `MediaStoreMusicRepository`.
- [x] `GetSongsUseCase`.
- [x] `LibraryViewModel`.
- [x] `LibraryUiState`.
- [x] StateFlow.
- [x] Hilt.
- [x] KSP.
- [x] `@HiltAndroidApp`.
- [x] `@AndroidEntryPoint`.
- [x] `@HiltViewModel`.
- [x] Repository binding con Hilt.
- [x] ViewModel conectado a Compose.
- [x] Carga de biblioteca sacada de `DJSMPlayerApp`.

---

# 9. Modelo Song

El modelo de dominio debe mantenerse independiente de Android.

Conceptualmente:

```kotlin
data class Song(
    val id: Long,
    val contentUri: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val trackNumber: Int?,
    val year: Int?,
    val mimeType: String?,
    val dateAddedSeconds: Long
)
```

No convertir `Song` en una clase Android-specific.

Evitar almacenar `android.net.Uri` directamente en Domain si no es necesario.

---

# 10. MediaStore

MediaStore es la fuente principal de archivos musicales disponibles en Android.

Flujo:

```text
ContentResolver
      ↓
MediaStore.Audio
      ↓
Cursor
      ↓
Song
      ↓
List<Song>
```

Usar `content://` URIs.

NO depender de rutas absolutas tipo:

```text
/storage/emulated/0/Music/song.mp3
```

Android moderno trabaja mejor mediante Content URIs.

---

# 11. Permisos

## Android 13+

```xml
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

## Android 8 - Android 12L

```xml
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

## Playback foreground service

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

No agregar:

```text
MANAGE_EXTERNAL_STORAGE
READ_MEDIA_IMAGES
READ_MEDIA_VIDEO
INTERNET
```

salvo que una feature futura tenga una justificación real.

---

# 12. Reproducción

La UI NO debe poseer directamente un `ExoPlayer`.

Arquitectura:

```text
Compose UI
    ↓
MediaController
    ↓
MediaSession
    ↓
PlaybackService
    ↓
ExoPlayer
```

Esto permite:

- reproducción en background
- lock screen
- media notification
- Bluetooth
- headset controls
- integración con Android
- UI desacoplada del player

---

# 13. Queue

Cuando se selecciona una canción, NO cargar solo esa canción.

Crear `MediaItem` para toda la biblioteca:

```text
Song 1
Song 2
Song 3 ← seleccionada
Song 4
Song 5
```

Usar el índice de la canción seleccionada como `startIndex`.

Esto permite:

- Previous
- Next
- Shuffle
- Repeat
- reproducción automática al terminar una canción

---

# 14. MediaItemMapper

La conversión:

```text
Song → MediaItem
```

debe vivir fuera de `DJSMPlayerApp`.

Debe incluir:

- mediaId
- contentUri
- title
- artist
- album
- albumId en extras
- metadata necesaria para Android MediaSession

---

# 15. Artwork

Estrategia offline:

```text
Audio
 ↓
1. TagLib
 ↓
2. MediaMetadataRetriever
 ↓
3. MediaStore thumbnail
 ↓
4. Album MediaStore thumbnail
 ↓
5. Placeholder
```

Nunca descargar portadas de Internet.

## Importante

Que un archivo sea `.mp3`, `.flac`, `.m4a`, etc. NO significa que obligatoriamente tenga artwork.

La app debe:

- mostrar artwork si existe
- soportar ausencia de artwork
- no fallar por metadata incompleta
- usar placeholder consistente

---

# 16. Caché de artwork

No releer los tags del audio cada vez que Compose recompone.

Usar:

```text
LruCache<String, Bitmap>
```

Y mantener un cache de claves donde ya se comprobó que no existe artwork.

Objetivo:

```text
Primera carga
audio → metadata → bitmap → cache

Siguientes cargas
cache → bitmap
```

---

# 17. Navigation

Rutas actuales:

```text
LibraryRoute
NowPlayingRoute
```

Flujo:

```text
Library
  ↓
Song tap
  ↓
Playback starts
  ↓
Mini Player
  ↓ tap
Now Playing
  ↓ Back
Library
```

No crear rutas para features que todavía no existen.

---

# 18. UI State

La biblioteca debe representar estados explícitos.

Objetivo:

```kotlin
sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class Success(val songs: List<Song>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
```

Esto evita estados ambiguos.

---

# 19. Funcionalidades obligatorias para versión completa

## Biblioteca

- [x] Canciones
- [ ] Álbumes
- [ ] Artistas
- [ ] Géneros
- [ ] Carpetas
- [ ] Búsqueda
- [ ] Ordenar por título
- [ ] Ordenar por artista
- [ ] Ordenar por álbum
- [ ] Ordenar por duración
- [ ] Ordenar por fecha añadida
- [ ] Orden ascendente / descendente
- [ ] Escaneo/refresh manual
- [ ] Refresh automático cuando cambia MediaStore

## Reproductor

- [x] Play
- [x] Pause
- [x] Seek
- [x] Previous
- [x] Next
- [x] Shuffle
- [x] Repeat All
- [x] Repeat One
- [x] Queue
- [ ] Visualización y edición de queue
- [ ] Reordenar queue
- [ ] Eliminar elementos de queue
- [ ] Añadir "Play next"
- [ ] Añadir "Add to queue"
- [ ] Restaurar queue después de cerrar proceso
- [ ] Restaurar canción y posición después de reiniciar app

## Playback profesional

- [ ] Audio Focus correcto
- [ ] Pausa/reducción por llamadas
- [ ] Ducking cuando corresponda
- [ ] Headset unplug handling
- [ ] Bluetooth handling
- [ ] Noisy audio intent
- [ ] Gapless playback
- [ ] Crossfade configurable
- [ ] Playback speed
- [ ] ReplayGain si se decide soportar
- [ ] Ecualizador / integración con efectos Android
- [ ] Sleep timer

## Now Playing

- [x] Artwork
- [x] Título
- [x] Artista
- [x] Progress
- [x] Tiempo actual
- [x] Duración
- [x] Previous
- [x] Play/Pause
- [x] Next
- [x] Shuffle
- [x] Repeat
- [ ] Favorite
- [ ] Queue button
- [ ] Más opciones
- [ ] Animaciones
- [ ] Gesture swipe down
- [ ] Gesture previous/next si se decide

## Mini Player

- [x] Artwork
- [x] Título
- [x] Artista
- [x] Progreso
- [x] Play/Pause
- [x] Abre Now Playing
- [ ] Next opcional
- [ ] Gestos
- [ ] Animaciones

---

# 20. Base de datos local futura

Usar Room para datos propios de DJSM Player.

NO usar Room para duplicar los archivos físicos de MediaStore.

Room debe manejar:

- favoritos
- playlists
- playlist songs
- historial
- recently played
- most played
- play count
- last played
- exclusions
- user metadata overrides
- settings complejos si aplica

---

# 21. Playlists

Debe existir soporte para:

- Crear playlist
- Renombrar playlist
- Eliminar playlist
- Añadir canción
- Eliminar canción
- Reordenar canciones
- Reproducir playlist
- Shuffle playlist
- Añadir playlist completa a queue

Todo almacenado localmente.

---

# 22. Favoritos

Requisitos:

- marcar/desmarcar una canción
- vista "Favoritos"
- persistencia con Room
- disponible offline
- favorito visible desde Now Playing y list items

---

# 23. Historial

Guardar localmente:

- canciones reproducidas
- última fecha/hora reproducida
- cantidad de reproducciones
- recently played
- most played

Definir criterio para contar una reproducción real, por ejemplo:

```text
reproducida durante X segundos
o
reproducido >= determinado porcentaje
```

No contar un tap accidental como reproducción completa.

---

# 24. MediaStore reactivo

Pendiente importante:

Implementar `ContentObserver`.

Objetivo:

```text
Usuario agrega canción
      ↓
MediaStore cambia
      ↓
ContentObserver
      ↓
Repository / Flow
      ↓
ViewModel
      ↓
UI actualizada
```

La app no debería requerir reinicio para detectar música nueva/eliminada.

---

# 25. Archivos eliminados o inaccesibles

La aplicación debe tolerar:

- archivo eliminado
- SD card desmontada
- URI inválida
- archivo corrupto
- codec no soportado
- metadata corrupta
- artwork corrupto
- permiso revocado

Nunca provocar crash por un archivo malo.

---

# 26. Soporte de formatos

La filosofía es soportar todos los formatos que el stack Android/Media3 pueda reproducir en el dispositivo.

Ejemplos comunes:

```text
MP3
AAC
M4A
FLAC
OGG
Opus
WAV
AMR
3GP audio
otros soportados por Media3 / codecs del dispositivo
```

No afirmar soporte absoluto para "cualquier extensión imaginable".

El soporte real depende de:

- container
- codec
- Android version
- decoder disponible
- dispositivo

Si un formato no puede reproducirse:

- mostrar error amigable
- continuar funcionando
- no romper la queue completa

---

# 27. Diseño futuro

La lógica debe mantenerse independiente del diseño.

Después de estabilizar arquitectura se puede rediseñar completamente la app sin cambiar:

- repositories
- use cases
- player service
- MediaStore
- queue
- Hilt graph

Objetivo visual:

- moderno
- limpio
- oscuro
- AMOLED friendly
- rápido
- inspirado en reproductores modernos
- NO copiar exactamente YouTube Music
- crear identidad propia DJSM Player

---

# 28. Design System futuro

Crear:

```text
core/designsystem
```

Debe centralizar:

- colors
- typography
- spacing
- shapes
- elevations
- iconography
- artwork shapes
- animations
- components

Evitar números mágicos repetidos en cada pantalla.

---

# 29. Temas

Implementar:

- Dark
- Light
- System
- AMOLED Black

Opcional:

- colores derivados del artwork actual

Todo debe seguir funcionando sin Internet.

---

# 30. Settings

Persistir settings con DataStore.

Opciones futuras:

- theme
- AMOLED
- sort order
- default library tab
- crossfade
- playback speed
- remember queue
- show WhatsApp audio
- show short audio
- excluded folders
- minimum duration
- artwork behavior

---

# 31. Filtrado de audios no deseados

En una biblioteca real aparecen:

- WhatsApp Audio
- recordings
- notification sounds
- clips de segundos
- voice notes

Agregar configuración para:

- excluir carpetas
- excluir audios menores de X segundos
- incluir/excluir voice notes
- incluir/excluir WhatsApp
- mostrar solo música

No imponer una sola política al usuario.

---

# 32. Búsqueda

Debe funcionar 100% offline.

Buscar por:

- título
- artista
- álbum
- filename si se agrega al modelo
- género

Búsqueda reactiva y eficiente.

---

# 33. Álbumes

Crear entidad/agrupación de presentación basada en:

- albumId
- album
- artist
- artwork
- tracks

Pantalla:

```text
Album
├── artwork
├── title
├── artist
├── year
├── Play
├── Shuffle
└── tracks
```

---

# 34. Artistas

Agrupar canciones por artista.

Pantalla:

```text
Artist
├── name
├── song count
├── albums
└── songs
```

Manejar correctamente:

```text
Artista desconocido
```

---

# 35. Carpetas

Permitir navegar por ubicación lógica de archivos.

Feature útil para bibliotecas offline.

Debe respetar Scoped Storage.

No depender de acceso irrestricto al filesystem.

---

# 36. Notificación multimedia

Mantener MediaSession como fuente de metadata.

La notificación debe reflejar:

- artwork
- title
- artist
- play/pause
- previous
- next
- progress si Android lo permite

---

# 37. Lockscreen y dispositivos externos

Validar:

- lock screen
- Bluetooth
- wired headset
- headset buttons
- car media controls compatibles
- Android system media panel

La MediaSession debe ser siempre la autoridad.

---

# 38. Performance

DJSM Player debe funcionar con:

```text
100 canciones
1,000 canciones
5,000 canciones
10,000+ canciones
```

Evitar:

- cargar bitmaps gigantes
- ejecutar MediaStore en main thread
- leer artwork repetidamente
- crear todos los elementos de lista de golpe
- usar rutas absolutas innecesarias
- trabajo pesado dentro de Composables

Usar:

- LazyColumn
- Dispatchers.IO
- cache
- StateFlow
- lifecycle-aware collection
- pagination solo si realmente se necesita

---

# 39. Lifecycle

La UI puede desaparecer.

El player no debe depender de la UI.

```text
Activity muere
    ↓
PlaybackService puede seguir

UI vuelve
    ↓
MediaController reconecta
    ↓
estado actual se reconstruye
```

Esto es obligatorio.

---

# 40. Process death

Pendiente:

- restaurar estado de navegación
- restaurar queue
- restaurar canción actual
- restaurar posición
- restaurar shuffle/repeat
- restaurar UI

No confiar únicamente en `remember`.

---

# 41. Errores

Crear eventualmente tipos de error propios.

Ejemplos:

```text
PermissionDenied
MediaStoreUnavailable
SongNotFound
PlaybackFailed
UnsupportedFormat
ArtworkFailed
StorageUnavailable
```

Evitar mostrar excepciones crudas al usuario.

---

# 42. Testing

## Unit tests

Prioridad:

- GetSongsUseCase
- repository logic
- metadata normalization
- LibraryViewModel
- sorting
- filtering
- queue rules
- format duration helpers

## UI tests

- Permission screen
- Empty library
- Error library
- Library list
- Mini Player
- Now Playing
- Search

## Integration tests

- MediaStore
- Hilt graph
- playback
- queue

---

# 43. Fake repositories

Crear para tests:

```text
FakeMusicRepository
```

Esto permitirá probar ViewModels sin depender de Android MediaStore.

---

# 44. CI

GitHub Actions futuro:

```text
push / pull request
        ↓
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

Opcional:

- Detekt
- Ktlint

---

# 45. Git workflow

Commits claros:

```text
feat:
fix:
refactor:
chore:
test:
docs:
perf:
```

Ejemplos:

```text
feat: add local audio playback
feat: scan local audio library
feat: add playback queue navigation
refactor: move library loading to ViewModel architecture
fix: restore media controller after navigation
```

No guardar código generated.

---

# 46. Generated code

Carpetas como:

```text
java (generated)
GetSongsUseCase_Factory
Hilt_MainActivity
GeneratedInjector
hilt_aggregated_deps
```

son normales.

Son generadas por:

```text
Hilt + KSP
```

Nunca editarlas manualmente.

---

# 47. Build

Desde Windows:

```powershell
.\gradlew.bat assembleDebug
```

Resultado esperado:

```text
BUILD SUCCESSFUL
```

APK debug:

```text
app\build\outputs\apk\debug\app-debug.apk
```

---

# 48. JAVA_HOME

En el entorno actual se ha usado el JBR incluido con Android Studio:

```text
C:\Program Files\Android\Android Studio\jbr
```

Si PowerShell no reconoce Java:

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Comprobar:

```powershell
java -version
```

---

# 49. Device testing

Priorizar teléfono físico para:

- MediaStore
- portadas
- Bluetooth
- notificaciones
- background playback
- lock screen
- SD card
- llamadas/interrupciones

El emulador sigue siendo útil para:

- UI
- APIs distintas
- tamaños de pantalla
- edge cases controlados

---

# 50. Privacidad

DJSM Player debe poder afirmar:

```text
No requiere cuenta.
No requiere Internet.
No sube tu biblioteca.
No analiza música en servidores.
No vende datos.
Toda la reproducción permanece en tu dispositivo.
```

No agregar analytics remotos por defecto.

---

# 51. No objetivos actuales

NO implementar durante la fase offline:

- streaming
- YouTube API
- Spotify API
- login
- backend
- cloud sync
- descarga online de artwork
- publicidad
- tracking remoto

Si alguna vez se agregan, deben ser módulos opcionales y no romper el modo offline.

---

# 52. Definition of Done para DJSM Player 1.0

Una versión 1.0 seria debería cumplir como mínimo:

## Core

- [ ] biblioteca actualizable automáticamente
- [x] background playback
- [x] MediaSession
- [x] queue
- [x] artwork local
- [x] metadata
- [x] previous/next
- [x] shuffle/repeat
- [x] seek

## Organización

- [ ] albums
- [ ] artists
- [ ] folders
- [ ] search
- [ ] favorites
- [ ] playlists

## Persistencia

- [ ] Room
- [ ] DataStore
- [ ] queue restore
- [ ] playback state restore

## Robustez

- [ ] deleted files
- [ ] corrupt files
- [ ] SD removal
- [ ] permission revoked
- [ ] process death
- [ ] 10k songs
- [ ] audio focus
- [ ] headset events

## Diseño

- [ ] design system
- [ ] responsive UI
- [ ] dark/light
- [ ] AMOLED
- [ ] accessibility
- [ ] polished animations

## Calidad

- [ ] unit tests
- [ ] UI tests
- [ ] lint clean
- [ ] CI
- [ ] signed release

---

# 53. Prioridad recomendada desde el estado actual

Orden recomendado para continuar:

```text
1. Terminar LibraryUiState robusto
2. ContentObserver / MediaStore reactivo
3. Sorting + filtering
4. Search
5. Albums
6. Artists
7. Folders
8. Room
9. Favorites
10. Playlists
11. History / most played
12. Queue persistence
13. Playback state restore
14. Audio focus + interruptions
15. Settings / DataStore
16. Design System
17. Rediseño completo
18. Tests
19. CI
20. Release
```

---

# 54. Instrucciones para continuar con un agente/IDE

Cuando se continúe DJSM Player:

1. Leer este README completo.
2. Inspeccionar el estado real del código antes de modificarlo.
3. No reescribir arquitectura ya funcional sin una razón.
4. No cambiar package.
5. No agregar Internet.
6. No meter ExoPlayer dentro de Compose.
7. No saltarse Repository / UseCase / ViewModel.
8. Mantener MediaStore como fuente de archivos.
9. Mantener Room solo para datos propios.
10. Compilar después de cambios importantes.
11. No asumir que "compile" significa que la feature está probada.
12. Probar audio en dispositivo físico.
13. Mantener commits pequeños y descriptivos.
14. Separar lógica y diseño.
15. No introducir dependencias sin necesidad real.

---

# 55. Principio final

DJSM Player debe sentirse como una aplicación moderna, pero su principal ventaja debe ser simple:

> **Tu música, en tu dispositivo, sin depender de Internet.**

La interfaz puede cambiar completamente.

La arquitectura y la lógica deben permanecer sólidas.

---

## DJSM Player

```text
100% LOCAL
100% OFFLINE
PRIVADO
RÁPIDO
MODERNO
MANTENIBLE
```
