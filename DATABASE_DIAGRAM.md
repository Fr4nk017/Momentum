# 🗂️ Diagrama de Arquitectura de Base de Datos - Momentum

## Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────────────┐
│                        UI LAYER (Compose)                        │
│                    Screens & Composables                         │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────────┐
│                     VIEWMODEL LAYER                              │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐        │
│  │LoginViewModel│  │HikingViewModel│  │BienestarViewModel│       │
│  └──────┬──────┘  └──────┬───────┘  └────────┬────────┘        │
└─────────┼─────────────────┼──────────────────┼──────────────────┘
          │                 │                  │
          ▼                 ▼                  ▼
┌──────────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                              │
│  ┌────────────┐  ┌────────────┐  ┌─────────────┐               │
│  │  Client    │  │  Hiking    │  │   Diary     │  ┌─────────┐  │
│  │ Repository │  │ Repository │  │ Repository  │  │ Friend  │  │
│  └─────┬──────┘  └─────┬──────┘  └──────┬──────┘  │   Repo  │  │
│        │               │                │         └────┬────┘  │
└────────┼───────────────┼────────────────┼──────────────┼────────┘
         │               │                │              │
         ▼               ▼                ▼              ▼
┌──────────────────────────────────────────────────────────────────┐
│                       DAO LAYER                                  │
│  ┌──────────┐  ┌─────────────┐  ┌────────────┐  ┌───────────┐  │
│  │ClientDao │  │ HikingDao   │  │ DiaryDao   │  │ FriendDao │  │
│  └────┬─────┘  └──────┬──────┘  └─────┬──────┘  └─────┬─────┘  │
└───────┼────────────────┼────────────────┼───────────────┼────────┘
        │                │                │               │
        └────────────────┴────────────────┴───────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────┐
│                      DATABASE PROVIDER                           │
│              (Singleton - Thread Safe)                           │
│                                                                  │
│              getDatabase(context): AppDatabase                   │
│              clientRepository(context)                           │
│              userRepository(context)                             │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────────┐
│                   ROOM DATABASE                                  │
│                  AppDatabase (momentum.db)                       │
│                     Version: 7                                   │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────────┐
│                      SQLITE DATABASE                             │
│                      (Device Storage)                            │
└──────────────────────────────────────────────────────────────────┘
```

## Diagrama de Entidades (ERD)

```
┌─────────────────────┐
│   ClientEntity      │
├─────────────────────┤
│ 🔑 id: Long         │
│    name: String     │
│    email: String    │
│    age: Int?        │
│    sex: String?     │
│    maritalStatus    │
│    occupation       │
│    phone            │
│    createdAt        │
└─────────────────────┘


┌─────────────────────┐         ┌──────────────────────┐
│    UserEntity       │◄───┐    │  DiaryEntryEntity    │
├─────────────────────┤    │    ├──────────────────────┤
│ 🔑 id: Long         │    │    │ 🔑 id: Long          │
│    name: String     │    └────│ 🔗 userId: String    │
│    email: String    │         │    content: String   │
│    bio: String      │         │    moodEmoji: String │
│    avatarUrl        │         │    createdAt: Long   │
│    totalPosts       │         └──────────────────────┘
│    friendsCount     │
│    currentMood      │         ┌──────────────────────┐
│    lastDiaryDate    │         │ CommunityPostEntity  │
│    totalEntries     │         ├──────────────────────┤
│    createdAt        │    ┌────│ 🔑 id: Long          │
└─────────────────────┘    │    │ 🔗 userId: Long      │
         ▲                 └────│    userName: String  │
         │                      │    content: String   │
         │                      │    emotionalState    │
┌────────┴────────┐             │    likes: Int        │
│  FriendEntity   │             │    comments: Int     │
├─────────────────┤             │    createdAt         │
│ 🔑 id: Long     │             └──────────────────────┘
│ 🔗 userId       │
│ 🔗 friendUserId │
│    status       │
│    createdAt    │
└─────────────────┘


┌───────────────────────┐         ┌────────────────────────┐
│  HikingSessionEntity  │◄────────│  LocationPointEntity   │
├───────────────────────┤ 1    N  ├────────────────────────┤
│ 🔑 id: Long           │         │ 🔑 id: Long            │
│    startTime: Long    │         │ 🔗 sessionId: Long     │
│    endTime: Long?     │         │    latitude: Double    │
│    totalDistance      │         │    longitude: Double   │
│    totalSteps         │         │    altitude: Double?   │
│    totalCalories      │         │    speed: Float?       │
│    averageSpeed       │         │    accuracy: Float?    │
│    maxSpeed           │         │    timestamp: Long     │
│    duration           │         └────────────────────────┘
│    isCompleted        │              [CASCADE DELETE]
└───────────────────────┘


┌───────────────────────┐         ┌────────────────────────┐
│  ChatSessionEntity    │◄────────│  ChatMessageEntity     │
├───────────────────────┤ 1    N  ├────────────────────────┤
│ 🔑 id: Long           │         │ 🔑 id: Long            │
│ 🔗 userId: String     │         │ 🔗 sessionId: Long     │
│    startTime: Long    │         │ 🔗 userId: String      │
│    endTime: Long?     │         │    content: String     │
│    userMood: String?  │         │    isUser: Boolean     │
│    topicTags: String? │         │    timestamp: Long     │
│    messageCount: Int  │         │    messageType: String │
└───────────────────────┘         │    metadata: String?   │
                                  └────────────────────────┘


┌─────────────────────┐
│ RecentPlaceEntity   │  (Independiente)
├─────────────────────┤
│ 🔑 id: Long         │
│    name: String     │
│    category: String │
│    searchedAt       │
└─────────────────────┘

Leyenda:
🔑 = Clave Primaria (Primary Key)
🔗 = Clave Foránea (Foreign Key)
? = Campo Opcional (Nullable)
```

## Flujo de Datos: Inserción

```
┌──────────────┐
│   Usuario    │
│  (Interacción)│
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│  UI Composable   │  onClick / onSubmit
│  (Button/Form)   │
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   ViewModel      │  viewModelScope.launch {
│  (State Logic)   │    repository.insert(...)
│                  │  }
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   Repository     │  suspend fun insert(...) {
│ (Business Logic) │    dao.insert(Entity(...))
│                  │  }
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│      DAO         │  @Insert
│  (Data Access)   │  suspend fun insert(entity)
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Room Database   │  SQL Generation
│  (ORM Layer)     │  Transaction Management
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│     SQLite       │  INSERT INTO table...
│  (Storage)       │  COMMIT
└──────────────────┘
```

## Flujo de Datos: Observación (Flow)

```
┌──────────────────┐
│  SQLite Database │  Datos cambian
└──────┬───────────┘
       │ ↑
       ▼ │ Trigger
┌──────────────────┐
│  Room Database   │  Detecta cambios
│  (Observer)      │  Notifica a Flow
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│      DAO         │  fun observeAll(): Flow<List<T>>
│  (Flow Emitter)  │  Emite nueva lista
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   Repository     │  Pasa Flow sin modificar
│  (Pass-through)  │  o transforma datos
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   ViewModel      │  .stateIn() → StateFlow
│  (State Mgmt)    │  Actualiza UI State
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  UI Composable   │  collectAsState()
│  (Recomposition) │  UI se redibuja automáticamente
└──────────────────┘
```

## Patrón de Thread Safety

```
╔══════════════════════════════════════════════════╗
║          DatabaseProvider (Singleton)            ║
╠══════════════════════════════════════════════════╣
║                                                  ║
║  @Volatile                                       ║
║  private var INSTANCE: AppDatabase? = null       ║
║                                                  ║
║  fun getDatabase(context): AppDatabase {         ║
║    return INSTANCE ?: synchronized(this) {       ║
║      INSTANCE ?: Room.databaseBuilder(...)       ║
║        .build()                                  ║
║        .also { INSTANCE = it }                   ║
║    }                                             ║
║  }                                               ║
║                                                  ║
╚══════════════════════════════════════════════════╝
         │
         ├─► Thread 1 ──┐
         │              ├─► synchronized { ... } ◄─ Solo uno pasa
         ├─► Thread 2 ──┤
         │              │
         └─► Thread 3 ──┘
```

## Ejemplo: Tracking de Caminata Completo

```
1. INICIO DE SESIÓN
   ┌────────────────────┐
   │ Usuario presiona   │
   │ "Iniciar Caminata" │
   └─────────┬──────────┘
             │
             ▼
   ┌────────────────────────────────┐
   │ HikingViewModel                │
   │ startHikingSession()           │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ HikingDao.insert(              │
   │   HikingSessionEntity(         │
   │     startTime = now,           │
   │     isCompleted = false        │
   │   )                            │
   │ )                              │
   │ ↓ Retorna sessionId            │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ Room guarda en SQLite          │
   │ INSERT INTO hiking_sessions... │
   └────────────────────────────────┘

2. TRACKING GPS (cada 5 segundos)
   ┌────────────────────┐
   │ GPS actualiza      │
   │ nueva ubicación    │
   └─────────┬──────────┘
             │
             ▼
   ┌────────────────────────────────┐
   │ LocationPointDao.insert(       │
   │   LocationPointEntity(         │
   │     sessionId = activeSession, │
   │     latitude = lat,            │
   │     longitude = lon,           │
   │     timestamp = now            │
   │   )                            │
   │ )                              │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ Room guarda punto GPS          │
   │ INSERT INTO location_points... │
   └────────────────────────────────┘

3. FIN DE SESIÓN
   ┌────────────────────┐
   │ Usuario presiona   │
   │ "Finalizar"        │
   └─────────┬──────────┘
             │
             ▼
   ┌────────────────────────────────┐
   │ HikingViewModel                │
   │ completeSession(stats)         │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ HikingDao.update(              │
   │   session.copy(                │
   │     endTime = now,             │
   │     totalDistance = 5000m,     │
   │     totalSteps = 6500,         │
   │     isCompleted = true         │
   │   )                            │
   │ )                              │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ Room actualiza SQLite          │
   │ UPDATE hiking_sessions         │
   │ SET endTime=..., distance=...  │
   └────────────────────────────────┘

4. VISUALIZACIÓN
   ┌────────────────────────────────┐
   │ HikingHistoryScreen            │
   │ observa completedSessions()    │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ Flow<List<HikingSession>>      │
   │ emite cuando hay cambios       │
   └────────┬───────────────────────┘
            │
            ▼
   ┌────────────────────────────────┐
   │ UI se actualiza automáticamente│
   │ Muestra lista de caminatas     │
   └────────────────────────────────┘
```

## Estructura de Archivos del Proyecto

```
app/src/main/java/com/momentum/app/
│
├── data/
│   ├── local/                    # Capa de persistencia local
│   │   ├── AppDatabase.kt        # Configuración principal de Room
│   │   ├── ClientEntity.kt       # Entidad de clientes
│   │   ├── ClientDao.kt          # DAO de clientes
│   │   ├── UserEntity.kt         # Entidad de usuarios
│   │   ├── UserDao.kt            # DAO de usuarios
│   │   ├── DiaryEntryEntity.kt   # Entidad de diario
│   │   ├── DiaryEntryDao.kt      # DAO de diario
│   │   ├── HikingSessionEntity.kt
│   │   ├── HikingDao.kt
│   │   ├── LocationPointEntity.kt
│   │   ├── ChatMessageEntity.kt
│   │   ├── ChatMessageDao.kt
│   │   ├── FriendEntity.kt
│   │   ├── FriendDao.kt
│   │   └── ... (otras entidades y DAOs)
│   │
│   ├── repository/               # Capa de repositorios
│   │   ├── ClientRepository.kt
│   │   ├── UserRepository.kt
│   │   ├── DiaryRepository.kt
│   │   ├── HikingRepository.kt
│   │   ├── ChatRepository.kt
│   │   └── ... (otros repositorios)
│   │
│   └── DatabaseProvider.kt       # Singleton provider
│
├── ui/
│   ├── screens/                  # Pantallas de UI
│   │   ├── login/
│   │   │   └── LoginViewModel.kt
│   │   ├── register/
│   │   │   └── RegisterViewModel.kt
│   │   └── BienestarViewModel.kt
│   └── ...
│
└── service/
    └── HikingService.kt          # Servicio en background
```

---

**Nota**: Todos los diagramas están simplificados para mayor claridad. La implementación real puede incluir más detalles y componentes.
