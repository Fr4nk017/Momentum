# 📊 Documentación de la Base de Datos - Momentum App

## Índice
1. [Introducción](#introducción)
2. [Arquitectura de la Base de Datos](#arquitectura-de-la-base-de-datos)
3. [Entidades (Tablas)](#entidades-tablas)
4. [DAOs (Data Access Objects)](#daos-data-access-objects)
5. [Repositorios](#repositorios)
6. [Flujo de Datos](#flujo-de-datos)
7. [Ejemplos de Uso](#ejemplos-de-uso)

---

## Introducción

La aplicación **Momentum** utiliza **Room Database**, una biblioteca de persistencia de datos de Android que proporciona una capa de abstracción sobre SQLite. La base de datos se llama `momentum.db` y está diseñada para almacenar información relacionada con el bienestar mental, seguimiento de actividades físicas, y funcionalidades sociales.

### Tecnologías Utilizadas
- **Room Database**: Biblioteca de persistencia de Android
- **Kotlin Coroutines**: Para operaciones asíncronas
- **Flow**: Para observar cambios en tiempo real
- **Repository Pattern**: Patrón de diseño para separar la lógica de datos

---

## Arquitectura de la Base de Datos

### Estructura General

```
AppDatabase (momentum.db)
├── DatabaseProvider (Singleton)
│   └── Proporciona instancia única de la base de datos
│
├── Entidades (Tablas)
│   ├── clients
│   ├── users
│   ├── friends
│   ├── community_posts
│   ├── recent_places
│   ├── hiking_sessions
│   ├── location_points
│   ├── diary_entries
│   ├── chat_messages
│   └── chat_sessions
│
├── DAOs (Acceso a Datos)
│   └── Interfaces con métodos para CRUD
│
└── Repositorios
    └── Capa de abstracción sobre DAOs
```

### DatabaseProvider

El `DatabaseProvider` es un objeto singleton que gestiona la creación y acceso a la base de datos:

```kotlin
object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
        // Patrón Double-checked locking para thread-safety
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "momentum.db"
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
```

**Características:**
- **Thread-safe**: Usa sincronización para evitar múltiples instancias
- **Singleton**: Solo existe una instancia de la base de datos
- **Migración destructiva**: Al cambiar la versión, recrea las tablas (útil en desarrollo)

---

## Entidades (Tablas)

### 1. ClientEntity (`clients`)

**Propósito**: Almacena información de clientes registrados en la aplicación.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| name | String | Nombre del cliente |
| email | String | Email único del cliente |
| age | Int? | Edad (opcional) |
| sex | String? | Sexo (opcional) |
| maritalStatus | String? | Estado civil (opcional) |
| occupation | String? | Ocupación (opcional) |
| phone | String? | Teléfono (opcional) |
| createdAt | Long | Timestamp de creación |

**Uso**: Registro e inicio de sesión de clientes.

---

### 2. UserEntity (`users`)

**Propósito**: Perfil de usuario para funcionalidades sociales y de comunidad.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| name | String | Nombre del usuario |
| email | String | Email del usuario |
| bio | String | Biografía del usuario |
| avatarUrl | String | URL del avatar |
| totalPosts | Int | Número total de publicaciones |
| friendsCount | Int | Cantidad de amigos |
| currentMood | String | Estado de ánimo actual |
| lastDiaryDate | Long | Fecha de última entrada de diario |
| totalEntries | Int | Total de entradas de diario |
| createdAt | Long | Timestamp de creación |

**Uso**: Perfil de usuario en la sección de comunidad.

---

### 3. FriendEntity (`friends`)

**Propósito**: Gestión de relaciones de amistad entre usuarios.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| userId | Long | ID del usuario que envía la solicitud |
| friendUserId | Long | ID del usuario receptor |
| status | String | Estado: "pending", "accepted", "blocked" |
| createdAt | Long | Timestamp de creación |

**Uso**: Sistema de amigos y conexiones sociales.

---

### 4. CommunityPostEntity (`community_posts`)

**Propósito**: Publicaciones en la comunidad de usuarios.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| userId | Long | ID del autor |
| userName | String | Nombre del autor |
| content | String | Contenido de la publicación |
| emotionalState | String | Estado emocional del autor |
| likes | Int | Número de likes |
| comments | Int | Número de comentarios |
| createdAt | Long | Timestamp de creación |

**Uso**: Feed de publicaciones en la comunidad.

---

### 5. RecentPlaceEntity (`recent_places`)

**Propósito**: Historial de lugares buscados por el usuario.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| name | String | Nombre del lugar |
| category | String | Categoría: "park", "trail", "psychologist", "support_center" |
| searchedAt | Long | Timestamp de búsqueda |

**Uso**: Sugerencias de lugares recientes para búsquedas.

---

### 6. HikingSessionEntity (`hiking_sessions`)

**Propósito**: Sesiones de caminata o ejercicio físico.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| startTime | Long | Hora de inicio |
| endTime | Long? | Hora de fin (null si está activa) |
| totalDistance | Float | Distancia total en metros |
| totalSteps | Int | Pasos totales |
| totalCalories | Float | Calorías quemadas |
| averageSpeed | Float | Velocidad promedio (m/s) |
| maxSpeed | Float | Velocidad máxima |
| duration | Long | Duración en milisegundos |
| isCompleted | Boolean | Si la sesión está completada |

**Uso**: Tracking de actividad física del usuario.

---

### 7. LocationPointEntity (`location_points`)

**Propósito**: Puntos de GPS durante una sesión de caminata.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| sessionId | Long | ID de la sesión (clave foránea) |
| latitude | Double | Latitud GPS |
| longitude | Double | Longitud GPS |
| altitude | Double? | Altitud (opcional) |
| speed | Float? | Velocidad en ese punto |
| accuracy | Float? | Precisión del GPS |
| timestamp | Long | Timestamp del punto |

**Relaciones:**
- **Foreign Key**: sessionId → HikingSessionEntity.id
- **Cascade Delete**: Si se elimina una sesión, se eliminan sus puntos

**Uso**: Recrear rutas de caminata en el mapa.

---

### 8. DiaryEntryEntity (`diary_entries`)

**Propósito**: Entradas del diario emocional del usuario.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| content | String | Contenido de la entrada |
| moodEmoji | String | Emoji representando el estado de ánimo |
| createdAt | Long | Timestamp de creación |
| userId | String | ID del usuario |

**Uso**: Diario personal para tracking emocional.

---

### 9. ChatMessageEntity (`chat_messages`)

**Propósito**: Mensajes del chat con el asistente de IA.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| sessionId | Long | ID de la sesión de chat |
| userId | String | ID del usuario |
| content | String | Contenido del mensaje |
| isUser | Boolean | Si es mensaje del usuario o IA |
| timestamp | Long | Timestamp del mensaje |
| messageType | String | Tipo: TEXT, BREATHING_EXERCISE, etc. |
| metadata | String? | JSON con metadata adicional |

**Uso**: Historial de conversaciones con el chatbot.

---

### 10. ChatSessionEntity (`chat_sessions`)

**Propósito**: Sesiones de chat con el asistente.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Clave primaria auto-incremental |
| userId | String | ID del usuario |
| startTime | Long | Hora de inicio |
| endTime | Long? | Hora de fin (null si está activa) |
| userMood | String? | Estado de ánimo del usuario |
| topicTags | String? | Temas tratados (JSON array) |
| messageCount | Int | Cantidad de mensajes |

**Uso**: Organizar historial de conversaciones.

---

## DAOs (Data Access Objects)

Los DAOs son interfaces que definen las operaciones de acceso a datos. Room genera la implementación automáticamente.

### Operaciones Comunes

#### ClientDao
```kotlin
@Dao
interface ClientDao {
    @Insert
    suspend fun insert(client: ClientEntity): Long
    
    @Query("SELECT * FROM clients WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): ClientEntity?
    
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ClientEntity>>
    
    @Upsert
    suspend fun upsert(client: ClientEntity)
}
```

**Métodos:**
- `insert()`: Inserta un nuevo cliente
- `findByEmail()`: Busca cliente por email
- `observeAll()`: Observa todos los clientes (Flow reactivo)
- `upsert()`: Inserta o actualiza si existe

#### DiaryEntryDao
```kotlin
@Dao
interface DiaryEntryDao {
    @Insert
    suspend fun insert(entry: DiaryEntryEntity): Long
    
    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllEntriesByUser(userId: String): Flow<List<DiaryEntryEntity>>
    
    @Query("SELECT * FROM diary_entries WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestEntry(userId: String): DiaryEntryEntity?
    
    @Delete
    suspend fun delete(entry: DiaryEntryEntity)
    
    @Query("SELECT moodEmoji, COUNT(*) as count FROM diary_entries WHERE userId = :userId GROUP BY moodEmoji ORDER BY count DESC")
    suspend fun getMoodStatistics(userId: String): List<MoodStatistic>
}
```

**Características especiales:**
- Queries con filtros por rango de fechas
- Estadísticas de estados de ánimo
- Agrupación de datos

#### HikingSessionDao
```kotlin
@Dao
interface HikingSessionDao {
    @Insert
    suspend fun insert(session: HikingSessionEntity): Long
    
    @Update
    suspend fun update(session: HikingSessionEntity)
    
    @Query("SELECT * FROM hiking_sessions WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedSessions(): Flow<List<HikingSessionEntity>>
    
    @Query("SELECT * FROM hiking_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveSession(): HikingSessionEntity?
    
    @Query("DELETE FROM hiking_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
```

---

## Repositorios

Los repositorios actúan como una capa de abstracción entre los DAOs y la capa de UI (ViewModels).

### Patrón de Diseño

```
ViewModel → Repository → DAO → Room Database → SQLite
```

### Ejemplo: ClientRepository

```kotlin
class ClientRepository(private val dao: ClientDao) {
    suspend fun insert(
        name: String,
        email: String,
        age: Int? = null,
        sex: String? = null,
        maritalStatus: String? = null,
        occupation: String? = null,
        phone: String? = null
    ): Long {
        return dao.insert(
            ClientEntity(
                name = name,
                email = email,
                age = age,
                sex = sex,
                maritalStatus = maritalStatus,
                occupation = occupation,
                phone = phone
            )
        )
    }
    
    suspend fun getByEmail(email: String): ClientEntity? = 
        dao.findByEmail(email)
    
    fun observeAll(): Flow<List<ClientEntity>> = 
        dao.observeAll()
}
```

**Ventajas:**
- Encapsula la lógica de creación de entidades
- Simplifica las llamadas desde el ViewModel
- Facilita el testing y mantenimiento

### Acceso a Repositorios

```kotlin
// Desde un ViewModel o Activity
val repository = DatabaseProvider.clientRepository(context)
val userRepository = DatabaseProvider.userRepository(context)
val diaryRepository = DiaryRepository(
    DatabaseProvider.getDatabase(context).diaryEntryDao()
)
```

---

## Flujo de Datos

### 1. Inserción de Datos

```
Usuario → UI (Composable) → ViewModel → Repository → DAO → Room → SQLite
```

**Ejemplo:**
```kotlin
// En el ViewModel
viewModelScope.launch {
    val clientId = repository.insert(
        name = "Juan Pérez",
        email = "juan@example.com",
        age = 30
    )
    // clientId contiene el ID generado
}
```

### 2. Lectura de Datos (Una vez)

```
ViewModel → Repository → DAO → Room → SQLite
SQLite → Room → DAO → Repository → ViewModel → UI
```

**Ejemplo:**
```kotlin
// Lectura única
viewModelScope.launch {
    val client = repository.getByEmail("juan@example.com")
    if (client != null) {
        // Usar datos del cliente
    }
}
```

### 3. Observación de Datos (Reactivo)

```
UI → ViewModel → Repository → DAO → Flow<List<T>>
Cambios en DB → Flow emite → ViewModel actualiza → UI se redibuja
```

**Ejemplo:**
```kotlin
// En el ViewModel
val allClients: StateFlow<List<ClientEntity>> = 
    repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

// En el Composable
val clients by viewModel.allClients.collectAsState()
```

---

## Ejemplos de Uso

### Ejemplo 1: Registro de Usuario

```kotlin
// En RegisterViewModel
suspend fun registerClient(
    name: String,
    email: String,
    age: Int
): Result<Long> {
    return try {
        // Verificar si el email ya existe
        val existing = repository.getByEmail(email)
        if (existing != null) {
            Result.failure(Exception("Email ya registrado"))
        } else {
            // Insertar nuevo cliente
            val id = repository.insert(name, email, age)
            Result.success(id)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Ejemplo 2: Guardar Entrada de Diario

```kotlin
// En DiaryViewModel
suspend fun saveEntry(content: String, moodEmoji: String, userId: String) {
    val entry = DiaryEntryEntity(
        content = content,
        moodEmoji = moodEmoji,
        userId = userId,
        createdAt = System.currentTimeMillis()
    )
    diaryRepository.insertEntry(entry)
}
```

### Ejemplo 3: Tracking de Caminata

```kotlin
// En HikingViewModel
suspend fun startHikingSession(): Long {
    val session = HikingSessionEntity(
        startTime = System.currentTimeMillis(),
        isCompleted = false
    )
    return hikingDao.insert(session)
}

suspend fun addLocationPoint(sessionId: Long, lat: Double, lon: Double) {
    val point = LocationPointEntity(
        sessionId = sessionId,
        latitude = lat,
        longitude = lon,
        timestamp = System.currentTimeMillis()
    )
    locationPointDao.insert(point)
}

suspend fun completeSession(sessionId: Long, stats: HikingStats) {
    val session = hikingDao.getSessionById(sessionId)
    session?.let {
        val updated = it.copy(
            endTime = System.currentTimeMillis(),
            totalDistance = stats.distance,
            totalSteps = stats.steps,
            totalCalories = stats.calories,
            averageSpeed = stats.avgSpeed,
            maxSpeed = stats.maxSpeed,
            duration = System.currentTimeMillis() - it.startTime,
            isCompleted = true
        )
        hikingDao.update(updated)
    }
}
```

### Ejemplo 4: Estadísticas de Estado de Ánimo

```kotlin
// En AnalyticsViewModel
suspend fun getMoodDistribution(userId: String): Map<String, Int> {
    val statistics = diaryRepository.getMoodStatistics(userId)
    return statistics.associate { it.moodEmoji to it.count }
}
```

### Ejemplo 5: Chat con IA

```kotlin
// En ChatViewModel
suspend fun sendMessage(sessionId: Long, userId: String, content: String) {
    // Guardar mensaje del usuario
    val userMessage = ChatMessageEntity(
        sessionId = sessionId,
        userId = userId,
        content = content,
        isUser = true,
        timestamp = System.currentTimeMillis(),
        messageType = "TEXT"
    )
    chatMessageDao.insert(userMessage)
    
    // Obtener respuesta de IA (simulado)
    val aiResponse = getAIResponse(content)
    
    // Guardar respuesta de IA
    val aiMessage = ChatMessageEntity(
        sessionId = sessionId,
        userId = userId,
        content = aiResponse,
        isUser = false,
        timestamp = System.currentTimeMillis(),
        messageType = "TEXT"
    )
    chatMessageDao.insert(aiMessage)
}
```

---

## Consideraciones Importantes

### 1. Operaciones Asíncronas
Todas las operaciones de base de datos deben ejecutarse en un contexto de corrutina:
```kotlin
viewModelScope.launch {
    // Operaciones de DB aquí
}
```

### 2. Thread Safety
- Room garantiza thread-safety en operaciones de DB
- Los DAOs con `Flow` emiten en el dispatcher de IO automáticamente

### 3. Migraciones
La versión actual usa `.fallbackToDestructiveMigration()`, que elimina todos los datos al cambiar la versión. En producción, deberías implementar migraciones:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE clients ADD COLUMN phone TEXT")
    }
}
```

### 4. Índices
`LocationPointEntity` usa índices para mejorar rendimiento:
```kotlin
@Entity(
    indices = [Index("sessionId")]
)
```

### 5. Foreign Keys
Las claves foráneas garantizan integridad referencial:
```kotlin
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = HikingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
```

---

## Diagrama de Relaciones

```
ClientEntity (1) ─────────────── (N) N/A

UserEntity (1) ─────────────── (N) DiaryEntryEntity
           (1) ─────────────── (N) CommunityPostEntity
           (1) ─────────────── (N) FriendEntity (como userId)
           (1) ─────────────── (N) FriendEntity (como friendUserId)
           (1) ─────────────── (N) ChatSessionEntity
           
HikingSessionEntity (1) ──────── (N) LocationPointEntity [CASCADE DELETE]

ChatSessionEntity (1) ──────────── (N) ChatMessageEntity

RecentPlaceEntity (independiente)
```

---

## Resumen

La base de datos de **Momentum** está bien estructurada usando el patrón Repository y Room Database. Proporciona:

✅ **Persistencia local** de datos del usuario  
✅ **Tracking** de actividades físicas con GPS  
✅ **Diario emocional** con estadísticas  
✅ **Chat con IA** con historial  
✅ **Red social** con amigos y publicaciones  
✅ **Operaciones reactivas** con Flow  
✅ **Thread-safe** con corrutinas  

Esta arquitectura facilita el desarrollo, testing y mantenimiento de la aplicación.
