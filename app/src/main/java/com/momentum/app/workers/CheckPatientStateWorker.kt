package com.momentum.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.momentum.app.notifications.NotificationHelper
import com.momentum.app.data.store.EmotionStateStore
import kotlin.time.Duration.Companion.hours

class CheckPatientStateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        NotificationHelper.ensureChannel(applicationContext)
        val store = EmotionStateStore(applicationContext)
        val (state, lastTime) = store.getLastState()

        val now = System.currentTimeMillis()
        val sixHoursMillis = 6.hours.inWholeMilliseconds

        if (state != null) {
            when (state.lowercase()) {
                "ansioso", "triste" -> {
                    NotificationHelper.showFollowup(
                        applicationContext,
                        title = "¿Hacemos una pausa?",
                        text = "Te sugiero 3 ciclos de respiración 4-4-6."
                    )
                }
                "irritado" -> {
                    NotificationHelper.showFollowup(
                        applicationContext,
                        title = "Recupera tu calma",
                        text = "Prueba una caminata de 5 minutos + respiración."
                    )
                }
            }
        }

        if (lastTime == null || now - lastTime > sixHoursMillis) {
            NotificationHelper.showFollowup(
                applicationContext,
                title = "¿Cómo te sientes?",
                text = "Registra tu estado en Momentum y escribe en tu diario."
            )
        }

        return Result.success()
    }
}