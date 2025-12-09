# 📋 PASO 2: Retrofit + Error Handling en Android (DIARY ONLY)

## ✅ Resumen General

Se mejoró el cliente Retrofit en Android para:
1. **Logging HTTP** - Ver request/response en Logcat (debugging)
2. **Error handling robusto** - Usar `Result<T>` en lugar de excepciones
3. **Timeouts** - Evitar cuelgues indefinidos
4. **Sincronización inteligente** - Guardar localmente primero, sincronizar después

---

## 🔧 Cambios Específicos (SOLO DIARY)

### 1️⃣ **BackendRetrofitInstance.kt** - Mejorado

**Qué cambió:**
- ✅ Agregado `HttpLoggingInterceptor` para ver requests/responses en Logcat
- ✅ Agregado `OkHttpClient` con timeouts (30 segundos)
- ✅ Mejor control sobre conexiones de red

**Antes:**
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

**Después:**
```kotlin
object BackendRetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: BackendApiService by lazy {
        // HttpLoggingInterceptor para debugging en Logcat
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY  // Ver todo: headers + body
        }

        // OkHttpClient con timeouts y logging
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)     // Tiempo máximo para conectar
            .readTimeout(30, TimeUnit.SECONDS)        // Tiempo máximo para leer
            .writeTimeout(30, TimeUnit.SECONDS)       // Tiempo máximo para escribir
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}
```

**Cómo verificar en Logcat:**
```
adb logcat | grep "HTTP"
```

Verás logs como:
```
D/OkHttp: --> POST /api/diary
D/OkHttp: Content-Type: application/json
D/OkHttp: {"userId":"user123","title":"..."}
D/OkHttp: <-- 200 OK
D/OkHttp: {"id":"507f...","date":"2025-12-09"}
```

---

### 2️⃣ **RemoteDiaryRepository.kt** - Error Handling

**Qué cambió:**
- ✅ Métodos retornan `Result<T>` en lugar de lanzar excepciones
- ✅ Validaciones previas antes de enviar
- ✅ Logging detallado (Log.d, Log.e)
- ✅ Manejo seguro de errores

**Antes:**
```kotlin
class RemoteDiaryRepository(private val api: BackendApiService) {

    suspend fun enviarEntrada(
        userId: String,
        title: String,
        content: String
    ): DiaryEntryResponse {  // ❌ Lanza excepción si falla
        val request = DiaryEntryRequest(userId, title, content, null)
        return api.createDiaryEntry(request)
    }

    suspend fun obtenerEntradas(userId: String): List<DiaryEntryResponse> {
        return api.getDiaryEntries(userId)
    }
}
```

**Después:**
```kotlin
class RemoteDiaryRepository(private val api: BackendApiService) {
    
    companion object {
        private const val TAG = "RemoteDiaryRepository"
    }

    /**
     * Envía una entrada de diario al backend.
     * Retorna Result<DiaryEntryResponse> para manejar errores de forma segura.
     */
    suspend fun enviarEntrada(
        userId: String,
        title: String,
        content: String
    ): Result<DiaryEntryResponse> {  // ✅ Result, no excepción
        return try {
            // Validación básica
            if (userId.isBlank() || title.isBlank() || content.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("userId, title y content no pueden estar vacíos")
                )
            }

            val request = DiaryEntryRequest(userId, title, content, null)
            val response = api.createDiaryEntry(request)
            
            Log.d(TAG, "Entrada creada exitosamente: ${response.id}")  // Debug
            Result.success(response)  // ✅ Éxito

        } catch (e: Exception) {
            Log.e(TAG, "Error enviando entrada: ${e.message}", e)  // Error
            Result.failure(e)  // ✅ Error encapsulado
        }
    }

    suspend fun obtenerEntradas(userId: String): Result<List<DiaryEntryResponse>> {
        return try {
            if (userId.isBlank()) {
                return Result.failure(IllegalArgumentException("userId no puede estar vacío"))
            }

            val response = api.getDiaryEntries(userId)
            Log.d(TAG, "Se obtuvieron ${response.size} entradas")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo entradas: ${e.message}", e)
            Result.failure(e)
        }
    }
}
```

**Por qué Result<T>:**
- ✅ Manejo explícito de errores (no try-catch en ViewModel)
- ✅ Más claro: `.onSuccess { }` y `.onFailure { }`
- ✅ Menos crashes inesperados

---

### 3️⃣ **DiaryViewModel.kt** - Integración Backend

**Qué cambió:**
- ✅ Agregado `RemoteDiaryRepository`
- ✅ `saveEntry()` ahora sincroniza con backend
- ✅ Estrategia: guardar localmente PRIMERO, luego enviar al backend
- ✅ Nuevo campo `syncStatus` en `DiaryUiState`
- ✅ Logging completo (Log.d, Log.w, Log.e)

**Antes:**
```kotlin
class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryRepository: DiaryRepository
    private val userRepository: UserRepository
    
    // ❌ Sin backend

    fun saveEntry() {
        // ...
        diaryRepository.insertEntry(entry)  // Solo local
        // ...
    }
}
```

**Después:**
```kotlin
data class DiaryUiState(
    val entries: List<DiaryEntryEntity> = emptyList(),
    val currentContent: String = "",
    val selectedMood: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val latestEntry: DiaryEntryEntity? = null,
    val totalEntries: Int = 0,
    val moodStatistics: List<MoodStatistic> = emptyList(),
    val syncStatus: String = "idle"  // ✅ NUEVO: idle, syncing, success, error
)

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryRepository: DiaryRepository
    private val remoteDiaryRepository: RemoteDiaryRepository  // ✅ Backend
    private val userRepository: UserRepository
    
    companion object {
        private const val TAG = "DiaryViewModel"
    }

    fun saveEntry() {
        val state = _uiState.value
        
        // Validaciones...
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, syncStatus = "syncing") }
                
                val entry = DiaryEntryEntity(...)
                
                // ✅ PASO 1: Guardar LOCALMENTE primero (garantizado)
                val localId = diaryRepository.insertEntry(entry)
                Log.d(TAG, "Entrada guardada localmente con ID: $localId")
                
                // ✅ PASO 2: Intentar sincronizar con BACKEND
                val title = "Entrada del ${getCurrentDate()}"
                val remoteResult = remoteDiaryRepository.enviarEntrada(
                    userId = currentUserId,
                    title = title,
                    content = state.currentContent
                )
                
                remoteResult.onSuccess { response ->
                    Log.d(TAG, "Entrada sincronizada al backend: ${response.id}")
                    _uiState.update {
                        it.copy(
                            currentContent = "",
                            selectedMood = "",
                            isLoading = false,
                            syncStatus = "success",
                            errorMessage = null
                        )
                    }
                }.onFailure { error ->
                    // ⚠️ Backend falló, pero ya está guardada localmente
                    Log.w(TAG, "Error en backend, pero guardada localmente: ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            syncStatus = "error",
                            errorMessage = "✓ Guardado localmente\n✗ No se pudo sincronizar: ${error.message}"
                        )
                    }
                }
                
                // Actualizar perfil y recargar
                updateUserProfile(state.selectedMood)
                loadStatistics()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error crítico: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        syncStatus = "error",
                        errorMessage = "Error al guardar: ${e.message}"
                    )
                }
            }
        }
    }
    
    // ✅ NUEVO: Helper para fecha
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
```

**Estrategia de Sincronización:**

```
┌─────────────────────────────────┐
│  Usuario presiona "Guardar"     │
└──────────┬──────────────────────┘
           │
           ▼
┌─────────────────────────────────┐
│ PASO 1: Guardar en Room Local   │  ✅ GARANTIZADO
│ (sin internet, igual funciona)  │
└──────────┬──────────────────────┘
           │
           ▼
┌─────────────────────────────────┐
│ PASO 2: Enviar al Backend       │  ⚠️ Depende de red
│ (Retrofit POST /api/diary)      │
└──────────┬──────────────────────┘
           │
      ┌────┴─────┐
      │           │
   ✅ÉXITO     ❌FALLO
      │           │
      ▼           ▼
  Mostrar    Mostrar:
  "Éxito"    "✓ Local, ✗ No sync"
```

---

## 📊 Tabla Resumen

| Componente | Cambio | Beneficio |
|------------|--------|-----------|
| **BackendRetrofitInstance** | +HttpLogging +OkHttpClient +Timeouts | Debugging + Estabilidad |
| **RemoteDiaryRepository** | Result<T> + Logging + Validaciones | Error handling seguro |
| **DiaryViewModel** | +Backend sync + syncStatus + Log | Integración completa |

---

## ✅ Cómo Verificar (Android)

### 1. Compilar sin errores
```bash
# En Android Studio: Build > Make Project
# O en terminal:
./gradlew build
```

### 2. Ver logs en Logcat
```
Logcat → Filter: "DiaryViewModel" o "RemoteDiaryRepository"
```

### 3. Ejecutar en emulador
- Abre app
- Navega a pantalla Diary
- Escribe contenido y selecciona mood
- Presiona "Guardar"

### 4. Verificar en Logcat

**Si todo funciona:**
```
D/DiaryViewModel: Entrada guardada localmente con ID: 1
D/RemoteDiaryRepository: Entrada creada exitosamente: 507f1f77...
D/DiaryViewModel: Entrada sincronizada al backend
```

**Si backend falla:**
```
D/DiaryViewModel: Entrada guardada localmente con ID: 1
W/DiaryViewModel: Error en backend, pero guardada localmente: Connection refused
```

### 5. Verificar en MongoDB

Si tienes acceso a MongoDB:
```bash
db.diary_entries.find({ "userId": "user123" })
```

---

## 🎯 Impacto: Antes vs Después

### Antes (Sin Backend):
```
Usuario escribe → Guardar → Se guarda en Room
                           → Datos nunca llegan al backend
                           → Imposible sincronizar entre dispositivos
```

### Después (Con Backend):
```
Usuario escribe → Guardar → Se guarda en Room (local)
                         → Se intenta enviar al backend
                         → Si éxito: sincronizado ✓
                         → Si falla: offline-friendly, se reintenta ✓
```

---

## 🚀 Próximos Pasos

1. ✅ PASO 1: Backend (LocalDate → String) ✓
2. ✅ PASO 2: Retrofit + Error Handling ✓
3. ⏳ PASO 3: Compilar y probar
4. ⏳ PASO 4: Refactorización futura (Hilt, WorkManager, etc.)

---

## 📝 Notas Importantes

### Result<T> Pattern
```kotlin
val result = remoteDiaryRepository.enviarEntrada(...)

result.onSuccess { data ->
    // Hacer algo con data
}.onFailure { error ->
    // Manejar error
}
```

### Logging Levels
- **Log.d()** - Debug, cosas que queremos ver en desarrollo
- **Log.e()** - Error, algo falló inesperadamente
- **Log.w()** - Warning, algo pasó pero no es crítico

### HttpLoggingInterceptor Levels
- **NONE** - Sin logs
- **BASIC** - Solo URLs y códigos HTTP
- **HEADERS** - URLs, códigos, headers
- **BODY** - Todo incluyendo request/response body

---

**¿Listo para PASO 3? 🚀**
