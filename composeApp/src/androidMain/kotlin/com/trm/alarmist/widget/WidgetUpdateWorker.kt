package com.trm.alarmist.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.trm.alarmist.core.system.WidgetManager
import java.time.Duration
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams), KoinComponent {
  private val widgetManager: WidgetManager by inject()

  override suspend fun doWork(): Result {
    widgetManager.updateAllWidgets()
    return Result.success()
  }

  internal companion object {
    fun enqueue(context: Context) {
      WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest())
    }

    private fun workRequest(): PeriodicWorkRequest =
      PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
          Duration.ofMillis(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS)
        )
        .build()

    private const val WORK_NAME = "WidgetUpdateWork"
  }
}
