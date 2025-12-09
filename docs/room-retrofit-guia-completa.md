# Guía Completa: Room (BD Local) + Retrofit (APIs) en Momentum

Esta guía explica paso a paso cómo funciona la persistencia local con Room, la comunicación remota con Retrofit, y cómo ambas capas colaboran en tu arquitectura.

---

## PARTE 1: ROOM (Base de Datos Local SQLite)

### ¿Qué es Room?
Room es la biblioteca oficial de Android para trabajar con SQLite de forma tipada y segura. Proporciona:
- Validación en tiempo de compilación de queries SQL.
- Integración con coroutines y Flow (reactive).
- Menos boilerplate que SQLite directo.

### Componentes de Room

#### 1. Entity (Entidad = Tabla)
Define la estructura de una tabla. Ejemplo real de tu proyecto:

**`DiaryEntryEntity.kt`**
```kotlin
package com.momentum.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val moodEmoji: String,
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String
)
```

**Anotaciones clave:**
- `@Entity(tableName = "diary_entries")`: Declara clase como tabla.
- `@PrimaryKey(autoGenerate = true)`: ID autoincrementable.
- `@ColumnInfo(name = "custom_name")`: Renombrar columna (opcional).
- `@Ignore`: Excluir campo de la tabla.

#### 2. DAO (Data Access Object = Operaciones)
Interface que define queries. Room genera la implementación automáticamente.

**`DiaryEntryDao.kt` (extracto)**
```kotlin
@Dao
interface DiaryEntryDao {
    @Insert
    suspend fun insert(entry: DiaryEntryEntity): Long

    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllEntriesByUser(userId: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE id = :entryId")
    suspend fun getEntryById(entryId: Long): DiaryEntryEntity?

    @Delete
    suspend fun delete(entry: DiaryEntryEntity)
    
    @Query("SELECT COUNT(*) FROM diary_entries WHERE userId = :userId")
    suspend fun getTotalEntries(userId: String): Int
}
```

**Anotaciones DAO:**
- `@Insert`: Inserta filas. Retorna ID(s) generado(s).
- `@Update`: Actualiza filas existentes.
- `@Delete`: Borra filas.
- `@Query("SQL")`: Query personalizada. Compilador valida sintaxis.
- `suspend`: Para llamadas asíncronas con coroutines.
- `Flow<List<T>>`: Observa cambios reactivamente (se actualiza automático).

#### 3. Database (Clase abstracta = Configuración)
Conecta entidades y DAOs.

**`AppDatabase.kt`**
```kotlin
@Database(
    entities = [
        ClientEntity::class,
        UserEntity::class,
        DiaryEntryEntity::class,
        // ... más entidades
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun diaryEntryDao(): DiaryEntryDao
    // ... más DAOs
}
```

**Parámetros:**
- `entities`: Lista de todas las tablas.
- `version`: Incrementa cuando cambias esquema (añades/eliminas columnas).
- `exportSchema`: Guarda esquema JSON para migraciones (buena práctica activarlo en producción).

#### 4. DatabaseProvider (Singleton = Instancia única)
Crea una sola instancia de la BD para toda la app.

**`DatabaseProvider.kt`**
```kotlin
object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "momentum.db"
            )
            .fallbackToDestructiveMigration() // ⚠️ Borra BD si versión cambia (solo dev!)
            .build()
            .also { INSTANCE = it }
            instance
        }
    }
}
```

**Explicación:**
- `Room.databaseBuilder()`: Configura y construye la BD.
- `fallbackToDestructiveMigration()`: Si cambias versión, borra todo y recrea (usar solo en desarrollo; en producción usa migraciones).
- `@Volatile`: Asegura visibilidad en multi-threading.
- `synchronized(this)`: Evita crear múltiples instancias.

### Importaciones típicas de Room
```kotlin
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.RoomDatabase
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
```

### Dependencias Gradle (Room)
```kotlin
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Coroutines + Flow
    ksp("androidx.room:room-compiler:2.6.1") // Procesador de anotaciones
}
```

### Uso desde Repository
**`DiaryRepository.kt`**
```kotlin
class DiaryRepository(private val diaryEntryDao: DiaryEntryDao) {
    
    fun getAllEntriesByUser(userId: String): Flow<List<DiaryEntryEntity>> {
        return diaryEntryDao.getAllEntriesByUser(userId)
    }
    
    suspend fun insertEntry(entry: DiaryEntryEntity): Long {
        return diaryEntryDao.insert(entry)
    }
}
```

### Flujo completo Room
```
UI solicita datos
  ↓
ViewModel llama Repository
  ↓
Repository llama DAO
  ↓
Room ejecuta query en BD SQLite
  ↓
Devuelve Flow<List<Entity>> o resultado directo
  ↓
ViewModel expone como StateFlow
  ↓
Compose observa con collectAsState() y recompone
```

---

## PARTE 2: RETROFIT (Conexión a APIs REST)

### ¿Qué es Retrofit?
Biblioteca de Square que convierte APIs HTTP en interfaces Kotlin/Java. Maneja:
- Construcción de URLs y peticiones.
- Serialización/deserialización JSON ↔ objetos.
- Integración con coroutines (`suspend`).

### Componentes de Retrofit

#### 1. Service Interface (Contrato de endpoints)
Define métodos HTTP como funciones Kotlin.

**`BackendApiService.kt`**
```kotlin
interface BackendApiService {

    @POST("api/moods")
    suspend fun createMood(
        @Body request: MoodEntryRequest
    ): MoodEntryResponse

    @GET("api/moods")
    suspend fun getMoods(
        @Query("userId") userId: String
    ): List<MoodEntryResponse>

    @POST("api/diary")
    suspend fun createDiaryEntry(
        @Body request: DiaryEntryRequest
    ): DiaryEntryResponse

    @GET("api/diary")
    suspend fun getDiaryEntries(
        @Query("userId") userId: String
    ): List<DiaryEntryResponse>
}
```

**Anotaciones HTTP:**
- `@GET("path")`: Petición GET.
- `@POST("path")`: Petición POST.
- `@PUT`, `@DELETE`, `@PATCH`: Otros verbos.
- `@Body`: Objeto se serializa a JSON y va en el cuerpo.
- `@Query("name")`: Parámetro en query string `?name=value`.
- `@Path("id")`: Sustituye en URL `users/{id}` → `users/123`.
- `@Header("Authorization")`: Añade header personalizado.
- `@Headers("Content-Type: application/json")`: Headers fijos.

#### 2. Request/Response DTOs (Modelos de datos)
Clases que representan JSON enviado/recibido.

**`MoodEntryRequest.kt`**
```kotlin
data class MoodEntryRequest(
    val userId: String,
    val emotion: String,
    val note: String?,
    val date: String? = null
)
```

**`MoodEntryResponse.kt`**
```kotlin
data class MoodEntryResponse(
    val id: String?,
    val userId: String,
    val emotion: String,
    val note: String?,
    val date: String
)
```

#### 3. Retrofit Instance (Configuración del cliente)
Construye el cliente HTTP con base URL y convertidores.

**`BackendRetrofitInstance.kt`**
```kotlin
object BackendRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}
```

**Explicación:**
- `baseUrl()`: URL base; los endpoints en el service se concatenan.
- `addConverterFactory()`: Serializa/deserializa JSON. Opciones:
  - `GsonConverterFactory` (Gson)
  - `MoshiConverterFactory` (Moshi, preferido para Kotlin)
  - `JacksonConverterFactory` (Jackson)
- `.create(Service::class.java)`: Genera implementación dinámica.

#### 4. Mejoras: OkHttpClient personalizado
Añade logging, timeouts, interceptores.

**Ejemplo con logging:**
```kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object BackendRetrofitInstance {
    
    private const val BASE_URL = "http://10.0.2.2:8080/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Ver request/response completo
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val api: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // ⭐ Usa OkHttp personalizado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}
```

### Importaciones típicas de Retrofit
```kotlin
// Retrofit core
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Headers

// Convertidores JSON
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

// OkHttp (opcional pero recomendado)
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody

// Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
```

### Dependencias Gradle (Retrofit)
```kotlin
dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // o Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    
    // OkHttp (logging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
```

### Uso desde Repository
**`MoodRepository.kt`**
```kotlin
class MoodRepository(private val api: BackendApiService) {

    suspend fun enviarMood(userId: String, emotion: String, note: String?): MoodEntryResponse {
        val request = MoodEntryRequest(userId, emotion, note, date = null)
        return api.createMood(request)
    }

    suspend fun obtenerHistorial(userId: String): List<MoodEntryResponse> {
        return api.getMoods(userId)
    }
}
```

### Flujo completo Retrofit
```
Usuario dispara acción en UI
  ↓
ViewModel llama Repository.enviarMood()
  ↓
Repository construye MoodEntryRequest
  ↓
Repository llama api.createMood(request) (suspend)
  ↓
Retrofit serializa request a JSON vía Gson
  ↓
OkHttp envía POST http://10.0.2.2:8080/api/moods con JSON
  ↓
Backend Spring Boot procesa y responde JSON
  ↓
Retrofit deserializa JSON → MoodEntryResponse
  ↓
Repository devuelve al ViewModel
  ↓
ViewModel actualiza StateFlow
  ↓
Compose observa y recompone UI
```

---

## PARTE 3: COLABORACIÓN ROOM + RETROFIT (Offline-First)

### Patrón: Remote → Cache Local
Estrategia común para apps robustas:
1. Intenta obtener datos del backend (Retrofit).
2. Guarda en Room (cache).
3. UI observa Flow de Room (siempre reactivo).
4. Si falla la red, muestra datos locales.

**Ejemplo: Repositorio Híbrido**
```kotlin
class HybridDiaryRepository(
    private val api: BackendApiService,
    private val dao: DiaryEntryDao
) {
    
    // UI observa este Flow (siempre actualizado)
    fun getDiaryEntries(userId: String): Flow<List<DiaryEntry>> {
        return dao.getAllEntriesByUser(userId).map { entities ->
            entities.map { it.toDomain() } // Convertir a modelo domain
        }
    }
    
    // Sincronización: fetch remoto + guardar local
    suspend fun syncDiaryEntries(userId: String) {
        try {
            val remoteEntries = api.getDiaryEntries(userId)
            val entities = remoteEntries.map { it.toEntity(userId) }
            // Borra viejas e inserta nuevas (estrategia simple)
            dao.deleteAll(userId)
            entities.forEach { dao.insert(it) }
        } catch (e: IOException) {
            // Error de red; los datos locales siguen disponibles
        }
    }
    
    // Crear entrada: envía a backend y guarda local
    suspend fun createEntry(userId: String, content: String, emoji: String): Result<Unit> {
        return try {
            val request = DiaryEntryRequest(userId, content, emoji)
            val response = api.createDiaryEntry(request)
            val entity = response.toEntity(userId)
            dao.insert(entity)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
```

**Mappers (DTO ↔ Entity ↔ Domain)**
```kotlin
// Response → Entity
fun DiaryEntryResponse.toEntity(userId: String) = DiaryEntryEntity(
    id = 0, // Room autogenera
    content = this.content,
    moodEmoji = this.emoji,
    createdAt = parseDate(this.date),
    userId = userId
)

// Entity → Domain
fun DiaryEntryEntity.toDomain() = DiaryEntry(
    id = this.id.toString(),
    content = this.content,
    emoji = this.moodEmoji,
    createdAt = Date(this.createdAt)
)
```

### WorkManager para Sincronización Periódica
Sincroniza en background incluso si app está cerrada.

**Ejemplo Worker:**
```kotlin
class SyncDiaryWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val repo = getHybridDiaryRepository() // DI o provider
        return try {
            repo.syncDiaryEntries(userId = "currentUser")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Programar en Application.onCreate() o ViewModel
val syncRequest = PeriodicWorkRequestBuilder<SyncDiaryWorker>(15, TimeUnit.MINUTES).build()
WorkManager.getInstance(context).enqueueUniquePeriodicWork("diary_sync", ExistingPeriodicWorkPolicy.KEEP, syncRequest)
```

---

## PARTE 4: ARQUITECTURA COMPLETA (Capas)

```
┌─────────────────────────────────────────────────┐
│  UI (Compose Screens)                           │
│  - Observa StateFlow con collectAsState()      │
│  - Dispara eventos (botones, inputs)           │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  ViewModel                                      │
│  - Expone StateFlow<UiState>                   │
│  - Orquesta llamadas al Repository             │
│  - Maneja coroutines (viewModelScope)          │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│  Repository (capa de datos)                     │
│  - Decide fuente: Remote, Local o ambas        │
│  - Mapea DTOs → Entities → Domain              │
└─────────┬────────────────────────────┬──────────┘
          │                            │
┌─────────▼─────────┐      ┌───────────▼──────────┐
│  Remote (Retrofit)│      │  Local (Room)        │
│  - BackendApiServ │      │  - DAOs              │
│  - Request/Resp   │      │  - Entities          │
│  - JSON ↔ Kotlin  │      │  - SQLite queries    │
└───────────────────┘      └──────────────────────┘
          │                            │
┌─────────▼────────────────────────────▼──────────┐
│  Backend Spring Boot     │   SQLite Database    │
│  (http://10.0.2.2:8080)  │   (momentum.db)      │
└──────────────────────────┴──────────────────────┘
```

---

## PARTE 5: MANEJO DE ERRORES (NetworkResult)

Envuelve respuestas para manejo consistente.

**Sealed class:**
```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class ApiError(val code: Int, val message: String?) : NetworkResult<Nothing>()
    object NetworkError : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
```

**Uso en Repository:**
```kotlin
suspend fun getMoodsWithErrorHandling(userId: String): NetworkResult<List<MoodEntryResponse>> {
    return try {
        NetworkResult.Loading
        val data = api.getMoods(userId)
        NetworkResult.Success(data)
    } catch (e: HttpException) {
        NetworkResult.ApiError(e.code(), e.message())
    } catch (e: IOException) {
        NetworkResult.NetworkError
    }
}
```

**En ViewModel:**
```kotlin
viewModelScope.launch {
    when (val result = repository.getMoodsWithErrorHandling(userId)) {
        is NetworkResult.Success -> _uiState.value = UiState.Loaded(result.data)
        is NetworkResult.ApiError -> _uiState.value = UiState.Error("Error ${result.code}")
        is NetworkResult.NetworkError -> _uiState.value = UiState.Error("Sin conexión")
        is NetworkResult.Loading -> _uiState.value = UiState.Loading
    }
}
```

---

## PARTE 6: IMPORTACIONES RESUMEN (Copiar/Pegar)

### Room
```kotlin
// Entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Ignore

// DAO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Database
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
```

### Retrofit
```kotlin
// Core
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.Header

// Converters
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// Exceptions
import retrofit2.HttpException
import java.io.IOException
```

### Coroutines
```kotlin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
```

---

## PARTE 7: CHECKLIST DE COMPRENSIÓN

- [ ] ¿Entiendes qué es una Entity y cómo se mapea a tabla SQL?
- [ ] ¿Sabes escribir un DAO con `@Query` y `suspend`?
- [ ] ¿Identificas la diferencia entre `suspend fun` y `Flow<T>`?
- [ ] ¿Puedes crear una instancia de Room con `Room.databaseBuilder()`?
- [ ] ¿Entiendes cómo Retrofit convierte anotaciones HTTP en peticiones?
- [ ] ¿Sabes qué hace `@Body`, `@Query`, `@Path`?
- [ ] ¿Puedes configurar un OkHttpClient con logging?
- [ ] ¿Entiendes el patrón Remote → Cache Local?
- [ ] ¿Sabes mapear DTOs (Response) → Entities → Domain models?
- [ ] ¿Puedes implementar manejo de errores con `NetworkResult`?

---

## PARTE 8: EJERCICIO PRÁCTICO

**Tarea:** Implementa un repositorio híbrido para "Moods".

1. **Entity:** Crea `MoodEntity` con campos: `id`, `userId`, `emotion`, `note`, `timestamp`.
2. **DAO:** Define `MoodDao` con:
   - `insert(mood: MoodEntity)`
   - `getAllMoods(userId: String): Flow<List<MoodEntity>>`
3. **Service:** Ya tienes `BackendApiService.createMood()` y `.getMoods()`.
4. **Repository:**
   ```kotlin
   class HybridMoodRepository(
       private val api: BackendApiService,
       private val dao: MoodDao
   ) {
       fun observeMoods(userId: String): Flow<List<MoodEntity>> = dao.getAllMoods(userId)
       
       suspend fun syncMoods(userId: String) {
           val remote = api.getMoods(userId)
           remote.forEach { dao.insert(it.toEntity()) }
       }
       
       suspend fun createMood(userId: String, emotion: String, note: String?) {
           val resp = api.createMood(MoodEntryRequest(userId, emotion, note))
           dao.insert(resp.toEntity())
       }
   }
   ```
5. **ViewModel:** Observa `repository.observeMoods()` y expone a UI.
6. **UI:** Muestra lista con `LazyColumn` + `collectAsState()`.

---

## PARTE 9: DEBUGGING TIPS

### Ver queries Room en Logcat
Añade en `AppDatabase`:
```kotlin
Room.databaseBuilder(...)
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL: $sqlQuery | Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
```

### Ver peticiones HTTP en Logcat
Configura `HttpLoggingInterceptor.Level.BODY` (ya mostrado arriba).

### Inspeccionar BD en Android Studio
- Menú: View → Tool Windows → App Inspection → Database Inspector.
- Selecciona proceso de la app.
- Explora tablas, ejecuta queries manuales.

---

## PARTE 10: MEJORES PRÁCTICAS

1. **Separar capas:**
   - DTOs (remote): `MoodEntryResponse`
   - Entities (local): `MoodEntity`
   - Domain (UI): `Mood`
2. **Usar DI (Hilt):** Inyecta repositorios, DAOs, Retrofit.
3. **Migraciones de BD:** No uses `fallbackToDestructiveMigration()` en producción.
4. **Timeouts razonables:** 30s connect, 60s read para APIs lentas.
5. **Retry con exponential backoff:** Para errores transitorios.
6. **Testing:**
   - Room: BD in-memory.
   - Retrofit: Mock con fake service o MockWebServer.

---

## RESUMEN FINAL

- **Room = persistencia local** (SQLite tipado).
  - Entity → tabla.
  - DAO → queries.
  - Database → configuración.
  - Flow → reactividad.
  
- **Retrofit = cliente HTTP** (REST APIs).
  - Service interface → contrato.
  - Request/Response → DTOs.
  - Retrofit.Builder → configuración.
  - OkHttp → networking real.
  
- **Arquitectura:** UI → ViewModel → Repository → (Remote + Local) → Backend/SQLite.

- **Imports clave:**
  - Room: `androidx.room.*`, `kotlinx.coroutines.flow.Flow`
  - Retrofit: `retrofit2.*`, `okhttp3.*`, convertidores JSON

---

Fin de la guía. ¡Guarda este archivo y consúltalo siempre que necesites recordar cómo funciona cada capa!
