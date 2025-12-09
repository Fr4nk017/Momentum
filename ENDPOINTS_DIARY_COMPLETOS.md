# 📍 ENDPOINTS COMPLETOS - DIARY (CRUD)

## ✅ Resumen de Operaciones

| Operación | HTTP | Endpoint | Android |
|-----------|------|----------|---------|
| **Crear** | POST | `/api/diary` | `enviarEntrada()` |
| **Leer** | GET | `/api/diary?userId=X` | `obtenerEntradas()` |
| **Actualizar** | PUT | `/api/diary/{id}` | `actualizarEntrada()` |
| **Eliminar** | DELETE | `/api/diary/{id}` | `eliminarEntrada()` |

---

## 🔧 Endpoints Detallados

### 1️⃣ **CREATE - POST /api/diary**

**Backend:** `DiaryEntryController.createDiaryEntry()`  
**Android:** `RemoteDiaryRepository.enviarEntrada()`

**Request (JSON):**
```json
{
  "userId": "user123",
  "title": "Mi primer día",
  "content": "Hoy fue un gran día",
  "date": "2025-12-09"  // opcional
}
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userId": "user123",
  "title": "Mi primer día",
  "content": "Hoy fue un gran día",
  "date": "2025-12-09"
}
```

**Error (400 Bad Request):**
```json
{
  "error": "title es requerido"
}
```

**Android Code:**
```kotlin
val result = remoteDiaryRepository.enviarEntrada(
    userId = "user123",
    title = "Mi entrada",
    content = "Contenido"
)

result.onSuccess { response ->
    Log.d("TAG", "Creado: ${response.id}")
}.onFailure { error ->
    Log.e("TAG", "Error: ${error.message}")
}
```

---

### 2️⃣ **READ - GET /api/diary**

**Backend:** `DiaryEntryController.getDiaryEntries()`  
**Android:** `RemoteDiaryRepository.obtenerEntradas()`

**Request (Query Parameters):**
```
GET /api/diary?userId=user123
```

**Response (200 OK):**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "userId": "user123",
    "title": "Mi primer día",
    "content": "Contenido",
    "date": "2025-12-09"
  },
  {
    "id": "507f1f77bcf86cd799439012",
    "userId": "user123",
    "title": "Mi segundo día",
    "content": "Otro día",
    "date": "2025-12-08"
  }
]
```

**Error (400 Bad Request):**
```json
{
  "error": "userId es requerido"
}
```

**Android Code:**
```kotlin
val result = remoteDiaryRepository.obtenerEntradas("user123")

result.onSuccess { entries ->
    Log.d("TAG", "Entradas: ${entries.size}")
}.onFailure { error ->
    Log.e("TAG", "Error: ${error.message}")
}
```

---

### 3️⃣ **UPDATE - PUT /api/diary/{id}**

**Backend:** `DiaryEntryController.updateDiaryEntry()`  
**Android:** `RemoteDiaryRepository.actualizarEntrada()`

**Request (JSON):**
```json
{
  "title": "Mi primer día - EDITADO",
  "content": "Hoy fue aún mejor",
  "userId": "",  // ignorado
  "date": null   // ignorado
}
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userId": "user123",
  "title": "Mi primer día - EDITADO",
  "content": "Hoy fue aún mejor",
  "date": "2025-12-09"
}
```

**Error (404 Not Found):**
```json
{
  "error": "Entrada no encontrada"
}
```

**Android Code:**
```kotlin
// En DiaryViewModel
viewModel.updateEntry(
    entryId = "localId",
    remoteId = "507f1f77bcf86cd799439011",
    newTitle = "Nuevo título",
    newContent = "Nuevo contenido"
)

// O directamente en Repository
val result = remoteDiaryRepository.actualizarEntrada(
    id = "507f1f77bcf86cd799439011",
    title = "Nuevo título",
    content = "Nuevo contenido"
)

result.onSuccess { response ->
    Log.d("TAG", "Actualizado: ${response.id}")
}.onFailure { error ->
    Log.e("TAG", "Error: ${error.message}")
}
```

---

### 4️⃣ **DELETE - DELETE /api/diary/{id}**

**Backend:** `DiaryEntryController.deleteDiaryEntry()`  
**Android:** `RemoteDiaryRepository.eliminarEntrada()`

**Request:**
```
DELETE /api/diary/507f1f77bcf86cd799439011
```

**Response (200 OK):**
```json
{
  "message": "Entrada eliminada exitosamente"
}
```

**Error (404 Not Found):**
```json
{
  "error": "Entrada no encontrada"
}
```

**Android Code:**
```kotlin
// En DiaryViewModel
viewModel.deleteEntry(entryEntity)  // Elimina local + backend

// O directamente en Repository
val result = remoteDiaryRepository.eliminarEntrada("507f1f77bcf86cd799439011")

result.onSuccess { response ->
    Log.d("TAG", "Eliminado: ${response["message"]}")
}.onFailure { error ->
    Log.e("TAG", "Error: ${error.message}")
}
```

---

## 📊 Validaciones Backend

### POST /api/diary
- ✅ `userId` no puede estar vacío
- ✅ `title` no puede estar vacío
- ✅ `content` no puede estar vacío
- ✅ `date` es opcional (usa LocalDate.now() si no se proporciona)

### GET /api/diary
- ✅ `userId` es requerido

### PUT /api/diary/{id}
- ✅ `id` es requerido (en URL)
- ✅ `title` no puede estar vacío
- ✅ `content` no puede estar vacío
- ✅ La entrada debe existir (404 si no)

### DELETE /api/diary/{id}
- ✅ `id` es requerido (en URL)
- ✅ La entrada debe existir (404 si no)

---

## 🔄 Status HTTP Codes

| Code | Significado | Cuándo |
|------|------------|--------|
| **200** | OK | Operación exitosa |
| **400** | Bad Request | Validación fallida (campos vacíos, etc.) |
| **404** | Not Found | Entrada no encontrada |
| **500** | Internal Server Error | Error del servidor |

---

## 📱 Android Integration - DiaryViewModel

### saveEntry() - Crear
```kotlin
viewModel.saveEntry()  // Guarda local + intenta backend
```

**Flujo:**
1. Valida contenido y mood
2. Guarda en Room (local)
3. Intenta enviar al backend
4. Si falla: muestra advertencia pero dato persiste

---

### updateEntry() - Actualizar
```kotlin
viewModel.updateEntry(
    entryId = "local_id",
    remoteId = "remote_id",
    newTitle = "Nuevo título",
    newContent = "Nuevo contenido"
)
```

**Flujo:**
1. Valida inputs
2. Intenta actualizar en backend
3. Si éxito: actualiza localmente
4. Si falla: muestra error

---

### deleteEntry() - Eliminar
```kotlin
viewModel.deleteEntry(entry)
```

**Flujo:**
1. Elimina de Room (local)
2. Intenta eliminar del backend
3. Si falla: muestra advertencia pero se eliminó localmente

---

## 🧪 Testing con cURL

### POST (Crear)
```bash
curl -X POST http://localhost:8080/api/diary \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "title": "Test",
    "content": "Contenido de test"
  }'
```

### GET (Leer)
```bash
curl "http://localhost:8080/api/diary?userId=user123"
```

### PUT (Actualizar)
```bash
curl -X PUT http://localhost:8080/api/diary/507f1f77bcf86cd799439011 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Actualizado",
    "content": "Contenido actualizado"
  }'
```

### DELETE (Eliminar)
```bash
curl -X DELETE http://localhost:8080/api/diary/507f1f77bcf86cd799439011
```

---

## 🎯 Resumen de Métodos en Android

### RemoteDiaryRepository
```kotlin
// Crear
suspend fun enviarEntrada(...): Result<DiaryEntryResponse>

// Leer
suspend fun obtenerEntradas(userId): Result<List<DiaryEntryResponse>>

// Actualizar
suspend fun actualizarEntrada(id, title, content): Result<DiaryEntryResponse>

// Eliminar
suspend fun eliminarEntrada(id): Result<Map<String, String>>
```

### DiaryViewModel
```kotlin
// Crear (local + backend)
fun saveEntry()

// Actualizar (backend)
fun updateEntry(entryId, remoteId, newTitle, newContent)

// Eliminar (local + backend)
fun deleteEntry(entry)

// Limpiar estados
fun clearError()
fun clearSyncStatus()
```

---

**✅ Todos los endpoints CRUD implementados y listos para usar** 🚀
