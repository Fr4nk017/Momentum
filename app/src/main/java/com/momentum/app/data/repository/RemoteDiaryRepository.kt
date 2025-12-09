package com.momentum.app.data.repository

import android.util.Log
import com.momentum.app.data.remote.moods.BackendApiService
import com.momentum.app.data.remote.diary.DiaryEntryRequest
import com.momentum.app.data.remote.diary.DiaryEntryResponse

class RemoteDiaryRepository(
    private val api: BackendApiService
) {
    
    companion object {
        private const val TAG = "RemoteDiaryRepository"
    }

    /**
     * Envía una entrada de diario al backend.
     * Retorna un Result<DiaryEntryResponse> para manejar errores de forma segura.
     */
    suspend fun enviarEntrada(
        userId: String,
        title: String,
        content: String
    ): Result<DiaryEntryResponse> {
        return try {
            // Validación básica antes de enviar
            if (userId.isBlank() || title.isBlank() || content.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("userId, title y content no pueden estar vacíos")
                )
            }

            val request = DiaryEntryRequest(
                userId = userId,
                title = title,
                content = content,
                date = null  // Backend usará LocalDate.now()
            )
            
            val response = api.createDiaryEntry(request)
            Log.d(TAG, "Entrada creada exitosamente: ${response.id}")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error enviando entrada: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las entradas de un usuario del backend.
     * Retorna un Result<List<DiaryEntryResponse>> para manejar errores.
     */
    suspend fun obtenerEntradas(userId: String): Result<List<DiaryEntryResponse>> {
        return try {
            if (userId.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("userId no puede estar vacío")
                )
            }

            val response = api.getDiaryEntries(userId)
            Log.d(TAG, "Se obtuvieron ${response.size} entradas")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo entradas: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza una entrada de diario en el backend.
     * Retorna un Result<DiaryEntryResponse> para manejar errores.
     */
    suspend fun actualizarEntrada(
        id: String,
        title: String,
        content: String
    ): Result<DiaryEntryResponse> {
        return try {
            if (id.isBlank() || title.isBlank() || content.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("id, title y content no pueden estar vacíos")
                )
            }

            val request = DiaryEntryRequest(
                userId = "",  // No se actualiza el userId
                title = title,
                content = content,
                date = null
            )

            val response = api.updateDiaryEntry(id, request)
            Log.d(TAG, "Entrada actualizada exitosamente: ${response.id}")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando entrada: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina una entrada de diario del backend.
     * Retorna un Result<Map<String, String>> con mensaje de confirmación.
     */
    suspend fun eliminarEntrada(id: String): Result<Map<String, String>> {
        return try {
            if (id.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("id no puede estar vacío")
                )
            }

            val response = api.deleteDiaryEntry(id)
            Log.d(TAG, "Entrada eliminada exitosamente: $id")
            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando entrada: ${e.message}", e)
            Result.failure(e)
        }
    }
}
