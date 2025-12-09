# Conexión a Microservicios y Flujo Backend (Momentum)

Este documento explica paso a paso cómo tu app Android (Compose + Kotlin) se conecta a los microservicios (Spring Boot) y cómo fluye la información desde la interfaz de usuario hasta el backend y de regreso.

---
## 1. Resumen Rápido del Flujo
```
Pantalla (Compose) → ViewModel → Repository → Retrofit (BackendApiService) → HTTP → Spring Boot Microservicio → JSON → Retrofit → Repository → ViewModel → UI State → Recompose
```

---
## 2. Punto de Entrada en la UI
En `NavGraph.kt` se crean las instancias de los repositorios que usan la capa remota:
- `val moodRepository = remember { MoodRepository(BackendRetrofitInstance.api) }`
- `val diaryRepository = remember { RemoteDiaryRepository(BackendRetrofitInstance.api) }`

Luego se pasan a los `ViewModel` mediante factories (ej. `MoodViewModelFactory`, `RemoteDiaryViewModelFactory`) o directamente.

La pantalla (por ejemplo `RemoteMoodsScreen`) dispara eventos de usuario (enviar mood, cargar historial) que llaman funciones del ViewModel.

---
## 3. ViewModel → Repository
El ViewModel expone métodos como `enviarMood()` o `obtenerHistorial()` (según implementación que añadas). Internamente llama al repositorio. Ejemplo real de repositorio:
`MoodRepository.kt`:
```kotlin
suspend fun enviarMood(userId: String, emotion: String, note: String?): MoodEntryResponse {
    val request = MoodEntryRequest(userId, emotion, note, date = null)
    return api.createMood(request)
}
```

`RemoteDiaryRepository.kt` funciona igual para entradas de diario.

---
## 4. Repository → Retrofit API Service
El repositorio no construye URLs manualmente; delega a la interfaz Retrofit:
`BackendApiService.kt`:
```kotlin
@POST("api/moods")
suspend fun createMood(@Body request: MoodEntryRequest): MoodEntryResponse

@GET("api/moods")
suspend fun getMoods(@Query("userId") userId: String): List<MoodEntryResponse>

@POST("api/diary")
suspend fun createDiaryEntry(@Body request: DiaryEntryRequest): DiaryEntryResponse

@GET("api/diary")
suspend fun getDiaryEntries(@Query("userId") userId: String): List<DiaryEntryResponse>
```
Las anotaciones (`@GET`, `@POST`, `@Body`, `@Query`) indican a Retrofit cómo formar la petición HTTP.

---
## 5. Instancia de Retrofit
Archivo: `BackendRetrofitInstance.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"

val api: BackendApiService by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BackendApiService::class.java)
}
```
- `10.0.2.2` es el alias especial que el emulador Android usa para referirse al `localhost` de tu PC.
- El convertidor actual es Gson (puede unificarse con Moshi si quieres consistencia).

---
## 6. Ejecución de la Petición
1. El método `createMood()` se invoca desde un `suspend` del repositorio.
2. Retrofit genera la implementación de `BackendApiService` dinámicamente.
3. Se construye la solicitud HTTP (POST a `/api/moods` con JSON en el cuerpo).
4. OkHttp (dentro de Retrofit) abre conexión, envía, espera respuesta.
5. El backend Spring Boot procesa la solicitud, crea/consulta datos y responde JSON.
6. Retrofit/Gson convierten el JSON recibido a `MoodEntryResponse` (DTO).
7. El repositorio devuelve ese DTO al ViewModel.
8. El ViewModel actualiza su `StateFlow` / `LiveData`.
9. La UI se recompone mostrando los cambios.

---
## 7. Modelos Usados
- Request: `MoodEntryRequest`, `DiaryEntryRequest` (en paquete `data.remote.diary` y `data.remote.moods` respectivamente).
- Response: `MoodEntryResponse`, `DiaryEntryResponse`.
Estos actualmente actúan tanto como DTO como modelos de presentación (puedes separar en domain si lo necesitas).

---
## 8. Ciclo Completo (Ejemplo Crear Mood)
```
[Usuario] Pulsa "Guardar Emoción"
   ↓
[Compose] Llama viewModel.enviarMood(...)
   ↓
[ViewModel] Ejecuta en coroutine (Dispatchers.IO ideal)
   ↓
[Repository] Construye MoodEntryRequest y llama api.createMood(request)
   ↓
[Retrofit] Serializa a JSON y hace POST /api/moods
   ↓
[Spring Boot] Guarda y responde JSON con campos (emotion, note, date, ...)
   ↓
[Retrofit] Deserializa a MoodEntryResponse
   ↓
[Repository] Devuelve al ViewModel
   ↓
[ViewModel] Actualiza estado (ej. lista + nuevo elemento)
   ↓
[Compose] Observa estado → Recompose → Muestra éxito
```

---
## 9. Ciclo Completo (Ejemplo Leer Diario)
```
[Pantalla Diario] Inicia efecto o usuario abre pestaña
   ↓
[ViewModel] solicita repository.obtenerEntradas(userId)
   ↓
[Repository] llama api.getDiaryEntries(userId)
   ↓
[Retrofit] GET /api/diary?userId=XYZ
   ↓
[Spring Boot] Recupera lista → JSON
   ↓
[Retrofit] Deserializa List<DiaryEntryResponse>
   ↓
[Repository] Devuelve lista
   ↓
[ViewModel] Emite nuevo estado con entries
   ↓
[UI] Renderiza lista en LazyColumn
```

---
## 10. Microservicios Spring Boot (Supuesto)
- Endpoints expuestos bajo prefijo `/api/`.
- Cada recurso (moods, diary) es un microservicio o controlador independiente.
- Recomendación futura: versionar (`/api/v1/moods`), incluir documentación OpenAPI/Swagger.

---
## 11. Errores y Manejo (Pendiente de Mejora)
Actualmente:
- Excepciones de red (`IOException`, `HttpException`) no se capturan en repositorios.
Mejoras sugeridas:
```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class ApiError(val code: Int, val message: String?) : NetworkResult<Nothing>()
    object NetworkError : NetworkResult<Nothing>()
}
```
En repositorio:
```kotlin
return try { NetworkResult.Success(api.getMoods(userId)) } catch (e: HttpException) { NetworkResult.ApiError(e.code(), e.message()) } catch (e: IOException) { NetworkResult.NetworkError }
```

---
## 12. Por Qué `suspend` Importa
- Permite llamadas no bloqueantes.
- ViewModel lanza coroutines; la UI sigue fluida.
- Retrofit integra coroutines: no necesitas `Call<T>` ni callbacks.

---
## 13. Emulador vs Dispositivo Físico
- Emulador: `10.0.2.2` → Host.
- Dispositivo físico: usar IP real de tu PC (ej. `http://192.168.1.10:8080/`). Cambia `BASE_URL` o introduce variable de entorno/build config.

---
## 14. Cómo Memorizar (Mapa Mental)
1. UI dispara evento.
2. ViewModel orquesta y no conoce HTTP.
3. Repository decide qué endpoint llamar.
4. Retrofit instancia concreta hace la petición.
5. Backend responde JSON.
6. Repository devuelve modelo.
7. ViewModel actualiza estado observable.
8. Compose recomposa.

---
## 15. Extensiones Futuras
- Autenticación: interceptor que añade Header `Authorization: Bearer <token>`.
- Logging: `HttpLoggingInterceptor` para depurar.
- Cache local y sync con Room (offline-first).
- Versionado y documentación (`/api/v1`, OpenAPI 3).
- Manejo estructurado de errores y reintentos exponenciales.

---
## 16. Checklist de Comprensión
- ¿Sabes dónde está la `BASE_URL`? → Sí, `BackendRetrofitInstance.kt`.
- ¿Identificas los métodos HTTP? → `@POST`, `@GET` en `BackendApiService.kt`.
- ¿Puedes seguir el flujo de crear un mood? → Ver sección 8.
- ¿Cómo cambiar a producción? → Ajustar `BASE_URL`.
- ¿Dónde añadir logs? → Construir `OkHttpClient` personalizado.

---
## 17. Frases Clave para Recordar
- "Repositorio = puente entre ViewModel y red/local".
- "RetrofitService = contrato HTTP tipado".
- "`10.0.2.2` = host PC desde emulador".
- "`suspend` = no bloqueo UI".

---
## 18. Mini Ejercicio (Repaso)
Escribe en voz alta:
"Cuando el usuario envía una emoción, la pantalla llama al ViewModel, este al repositorio, el repositorio usa Retrofit para hacer POST al microservicio Spring Boot, recibe JSON, lo transforma y actualiza el estado para que Compose muestre el resultado".

---
## 19. Próximo Paso Sugerido
Implementar manejo de errores + logging para reforzar el entendimiento.

---
Fin.
