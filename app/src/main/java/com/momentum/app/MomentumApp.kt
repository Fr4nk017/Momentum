package com.momentum.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.momentum.app.notifications.NotificationHelper
import com.momentum.app.workers.CheckPatientStateWorker
import java.util.concurrent.TimeUnit

class MomentumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)

        // Programa un chequeo cada 3 horas
        val request = PeriodicWorkRequestBuilder<CheckPatientStateWorker>(3, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "patient_state_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}