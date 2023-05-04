package com.wyy.jetpack_workmanager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class UploadLogWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val filePath = inputData.getString("filePath")
        val fileName = inputData.getString("fileName")
//        TimeUnit.SECONDS.sleep(5)
        Log.d("打印线程", "${Thread.currentThread().name} $fileName : $filePath")
        return Result.success()
    }

    /**
     * 在 Android 12 之前，工作器中的 getForegroundInfoAsync() 和 getForegroundInfo() 方法可让 WorkManager 在您调用 setExpedited() 时显示通知。如果您想要请求任务作为加急作业运行，则所有的 ListenableWorker 都必须实现 getForegroundInfo 方法
     */
    @SuppressLint("RestrictedApi")
    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        val future = SettableFuture.create<ForegroundInfo>()
        future.set(getForegroundInfo())
        return future
    }

    private fun getForegroundInfo(): ForegroundInfo {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("1", "hh", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "1")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText("我是一个上传日志的任务")
            .build()
        return ForegroundInfo(1337, notification)

    }
}