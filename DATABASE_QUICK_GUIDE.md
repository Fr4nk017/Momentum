# 🚀 Guía Rápida de Uso de la Base de Datos

## Configuración Inicial

### 1. Obtener Referencia a la Base de Datos

```kotlin
// En un Activity o Fragment
val db = DatabaseProvider.getDatabase(requireContext())

// En un ViewModel (AndroidViewModel)
class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val repository = DatabaseProvider.clientRepository(application)
}
```

## Operaciones CRUD Comunes

### CREATE (Insertar)

```kotlin
// Usando Repository
viewModelScope.launch {
    val clientId = repository.insert(
        name = "María García",
        email = "maria@example.com",
        age = 28,
        sex = "F"
    )
    println("Cliente insertado con ID: $clientId")
}

// Usando DAO directamente
viewModelScope.launch {
    val entry = DiaryEntryEntity(
        content = "Hoy me siento muy bien",
        moodEmoji = "😊",
        userId = "user123"
    )
    val entryId = db.diaryEntryDao().insert(entry)
}
```

### READ (Leer)

#### Lectura única (suspend function)

```kotlin
viewModelScope.launch {
    // Buscar por email
    val client = repository.getByEmail("maria@example.com")
    if (client != null) {
        println("Cliente encontrado: ${client.name}")
    }
    
    // Obtener última entrada de diario
    val lastEntry = db.diaryEntryDao().getLatestEntry("user123")
    
    // Obtener sesión activa de caminata
    val activeSession = db.hikingSessionDao().getActiveSession()
}
```

#### Lectura reactiva (Flow)

```kotlin
// En el ViewModel
class MyViewModel : ViewModel() {
    private val repository = // ... obtener repository
    
    // Flow que emite cada vez que cambian los datos
    val allClients: StateFlow<List<ClientEntity>> = 
        repository.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}

// En el Composable
@Composable
fun ClientListScreen(viewModel: MyViewModel) {
    val clients by viewModel.allClients.collectAsState()
    
    LazyColumn {
        items(clients) { client ->
            Text(text = client.name)
        }
    }
}
```

### UPDATE (Actualizar)

```kotlin
viewModelScope.launch {
    // Actualizar sesión de caminata
    val session = db.hikingSessionDao().getSessionById(sessionId)
    session?.let {
        val updated = it.copy(
            endTime = System.currentTimeMillis(),
            totalDistance = 5000f,
            totalSteps = 6500,
            isCompleted = true
        )
        db.hikingSessionDao().update(updated)
    }
    
    // Upsert (actualizar si existe, insertar si no)
    repository.upsert(
        name = "María García",
        email = "maria@example.com",
        age = 29  // edad actualizada
    )
}
```

### DELETE (Eliminar)

```kotlin
viewModelScope.launch {
    // Eliminar entrada de diario
    val entry = db.diaryEntryDao().getEntryById(entryId)
    entry?.let {
        db.diaryEntryDao().delete(it)
    }
    
    // Eliminar sesión de caminata
    db.hikingSessionDao().deleteSession(sessionId)
    // Los LocationPoints se eliminan automáticamente (CASCADE)
}
```

## Casos de Uso Específicos

### 📝 Diario Emocional

#### Guardar nueva entrada

```kotlin
suspend fun saveNewDiaryEntry(
    content: String,
    mood: String,
    userId: String
) {
    val entry = DiaryEntryEntity(
        content = content,
        moodEmoji = mood,
        userId = userId
    )
    db.diaryEntryDao().insert(entry)
}
```

#### Obtener historial de entradas

```kotlin
// En el ViewModel
val diaryEntries: StateFlow<List<DiaryEntryEntity>> = 
    db.diaryEntryDao()
        .getAllEntriesByUser("user123")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

// En la UI
@Composable
fun DiaryScreen(viewModel: DiaryViewModel) {
    val entries by viewModel.diaryEntries.collectAsState()
    
    LazyColumn {
        items(entries) { entry ->
            DiaryEntryCard(
                emoji = entry.moodEmoji,
                content = entry.content,
                date = Date(entry.createdAt)
            )
        }
    }
}
```

#### Estadísticas de estado de ánimo

```kotlin
suspend fun getMoodStats(userId: String): List<MoodStatistic> {
    return db.diaryEntryDao().getMoodStatistics(userId)
}

// Uso en UI
viewModelScope.launch {
    val stats = getMoodStats("user123")
    stats.forEach { stat ->
        println("${stat.moodEmoji}: ${stat.count} veces")
    }
}
```

### 🥾 Tracking de Caminatas

#### Iniciar sesión de caminata

```kotlin
suspend fun startHiking(): Long {
    val session = HikingSessionEntity(
        startTime = System.currentTimeMillis(),
        isCompleted = false
    )
    return db.hikingSessionDao().insert(session)
}
```

#### Guardar punto GPS

```kotlin
suspend fun saveLocationPoint(
    sessionId: Long,
    latitude: Double,
    longitude: Double,
    speed: Float?
) {
    val point = LocationPointEntity(
        sessionId = sessionId,
        latitude = latitude,
        longitude = longitude,
        speed = speed,
        timestamp = System.currentTimeMillis()
    )
    db.locationPointDao().insert(point)
}
```

#### Finalizar sesión

```kotlin
suspend fun finishHiking(
    sessionId: Long,
    distance: Float,
    steps: Int,
    calories: Float,
    avgSpeed: Float,
    maxSpeed: Float
) {
    val session = db.hikingSessionDao().getSessionById(sessionId)
    session?.let {
        val duration = System.currentTimeMillis() - it.startTime
        val updated = it.copy(
            endTime = System.currentTimeMillis(),
            totalDistance = distance,
            totalSteps = steps,
            totalCalories = calories,
            averageSpeed = avgSpeed,
            maxSpeed = maxSpeed,
            duration = duration,
            isCompleted = true
        )
        db.hikingSessionDao().update(updated)
    }
}
```

#### Obtener historial de caminatas

```kotlin
// En el ViewModel
val completedHikes: StateFlow<List<HikingSessionEntity>> = 
    db.hikingSessionDao()
        .getCompletedSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

// En la UI
@Composable
fun HikingHistoryScreen(viewModel: HikingViewModel) {
    val hikes by viewModel.completedHikes.collectAsState()
    
    LazyColumn {
        items(hikes) { session ->
            HikingCard(
                distance = session.totalDistance,
                steps = session.totalSteps,
                duration = session.duration,
                date = Date(session.startTime)
            )
        }
    }
}
```

#### Obtener ruta de una caminata

```kotlin
suspend fun getHikingRoute(sessionId: Long): List<LocationPointEntity> {
    return db.locationPointDao().getPointsForSession(sessionId)
}

// Usar en un mapa
viewModelScope.launch {
    val route = getHikingRoute(selectedSessionId)
    route.forEach { point ->
        addMarkerToMap(
            LatLng(point.latitude, point.longitude)
        )
    }
}
```

### 💬 Chat con IA

#### Crear nueva sesión de chat

```kotlin
suspend fun createChatSession(userId: String): Long {
    val session = ChatSessionEntity(
        userId = userId,
        startTime = System.currentTimeMillis()
    )
    return db.chatMessageDao().insertSession(session)
}
```

#### Enviar mensaje

```kotlin
suspend fun sendMessage(
    sessionId: Long,
    userId: String,
    content: String,
    isUser: Boolean
) {
    val message = ChatMessageEntity(
        sessionId = sessionId,
        userId = userId,
        content = content,
        isUser = isUser,
        timestamp = System.currentTimeMillis(),
        messageType = "TEXT"
    )
    db.chatMessageDao().insert(message)
}
```

#### Obtener mensajes de una sesión

```kotlin
// En el ViewModel
val chatMessages: StateFlow<List<ChatMessageEntity>> = 
    db.chatMessageDao()
        .getMessagesForSession(currentSessionId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

// En la UI
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    
    LazyColumn {
        items(messages) { message ->
            ChatBubble(
                content = message.content,
                isUser = message.isUser,
                timestamp = message.timestamp
            )
        }
    }
}
```

### 👥 Red Social

#### Enviar solicitud de amistad

```kotlin
suspend fun sendFriendRequest(userId: Long, friendUserId: Long) {
    val friendship = FriendEntity(
        userId = userId,
        friendUserId = friendUserId,
        status = "pending"
    )
    db.friendDao().insert(friendship)
}
```

#### Aceptar solicitud

```kotlin
suspend fun acceptFriendRequest(friendshipId: Long) {
    val friendship = db.friendDao().getFriendshipById(friendshipId)
    friendship?.let {
        val updated = it.copy(status = "accepted")
        db.friendDao().update(updated)
    }
}
```

#### Crear publicación en comunidad

```kotlin
suspend fun createPost(
    userId: Long,
    userName: String,
    content: String,
    emotionalState: String
) {
    val post = CommunityPostEntity(
        userId = userId,
        userName = userName,
        content = content,
        emotionalState = emotionalState
    )
    db.communityPostDao().insert(post)
}
```

#### Obtener feed de publicaciones

```kotlin
// En el ViewModel
val communityFeed: StateFlow<List<CommunityPostEntity>> = 
    db.communityPostDao()
        .getAllPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
```

## Consultas Avanzadas

### Filtrar por rango de fechas

```kotlin
suspend fun getEntriesInRange(
    userId: String,
    startDate: Long,
    endDate: Long
): List<DiaryEntryEntity> {
    return db.diaryEntryDao()
        .getEntriesInDateRange(userId, startDate, endDate)
}

// Ejemplo: Entradas del último mes
val calendar = Calendar.getInstance()
val endDate = calendar.timeInMillis
calendar.add(Calendar.MONTH, -1)
val startDate = calendar.timeInMillis

viewModelScope.launch {
    val lastMonthEntries = getEntriesInRange("user123", startDate, endDate)
}
```

### Estadísticas mensuales

```kotlin
suspend fun getMonthlyMoodStats(
    userId: String,
    month: Int,
    year: Int
): List<MoodStatistic> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1, 0, 0, 0)
    val startOfMonth = calendar.timeInMillis
    
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    val endOfMonth = calendar.timeInMillis
    
    return db.diaryEntryDao()
        .getMoodStatisticsForMonth(userId, startOfMonth, endOfMonth)
}
```

## Testing

### Ejemplo de test con Room

```kotlin
@RunWith(AndroidJUnit4::class)
class DiaryDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var diaryDao: DiaryEntryDao
    
    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        diaryDao = database.diaryEntryDao()
    }
    
    @After
    fun closeDatabase() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveDiaryEntry() = runBlocking {
        // Given
        val entry = DiaryEntryEntity(
            content = "Test entry",
            moodEmoji = "😊",
            userId = "testUser"
        )
        
        // When
        val entryId = diaryDao.insert(entry)
        val retrieved = diaryDao.getEntryById(entryId)
        
        // Then
        assertNotNull(retrieved)
        assertEquals("Test entry", retrieved?.content)
        assertEquals("😊", retrieved?.moodEmoji)
    }
}
```

## Tips y Mejores Prácticas

### ✅ Hacer

1. **Siempre usar corrutinas para operaciones de DB**
   ```kotlin
   viewModelScope.launch {
       repository.insert(...)
   }
   ```

2. **Usar Flow para datos reactivos**
   ```kotlin
   val data = dao.observeAll().stateIn(...)
   ```

3. **Validar datos antes de insertar**
   ```kotlin
   if (email.isValidEmail()) {
       repository.insert(...)
   }
   ```

4. **Manejar errores**
   ```kotlin
   try {
       repository.insert(...)
   } catch (e: SQLiteConstraintException) {
       // Email duplicado
   }
   ```

### ❌ Evitar

1. **No llamar operaciones de DB en el Main thread**
   ```kotlin
   // ❌ Incorrecto
   val data = database.dao().getData()  // Crash!
   
   // ✅ Correcto
   viewModelScope.launch {
       val data = database.dao().getData()
   }
   ```

2. **No crear múltiples instancias de la DB**
   ```kotlin
   // ❌ Incorrecto
   val db1 = Room.databaseBuilder(...).build()
   val db2 = Room.databaseBuilder(...).build()
   
   // ✅ Correcto
   val db = DatabaseProvider.getDatabase(context)
   ```

3. **No ignorar valores null sin verificar**
   ```kotlin
   // ❌ Incorrecto
   val client = repository.getByEmail(email)
   println(client.name)  // NullPointerException!
   
   // ✅ Correcto
   val client = repository.getByEmail(email)
   client?.let {
       println(it.name)
   }
   ```

## Recursos Adicionales

- [Documentación completa de la base de datos](DATABASE.md)
- [Diagramas de arquitectura](DATABASE_DIAGRAM.md)
- [Documentación oficial de Room](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
