package com.wyy.jetpack_workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 设置工作约束
        val constraints = Constraints.Builder()
            .setRequiresCharging(true) // 只能在设备充电时运行
            .build()

        // 一次性任务
        val uploadLogWorkerRequest : WorkRequest = OneTimeWorkRequestBuilder<UploadLogWorker>()
            .setInputData(workDataOf("filePath" to "file://***", "fileName" to "log.txt")) // 传递参数
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST) // 设置加急任务
            .build()

        // 定时任务
        val periodicWorkRequest = PeriodicWorkRequestBuilder<UploadLogWorker>(15, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        // 延迟执行
        val uploadLogWorkerRequest2 = OneTimeWorkRequestBuilder<UploadLogWorker>()
//            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag("testTag1")
            .build()

        // 重试策略
        val uploadLogWorkerRequest3 : WorkRequest = OneTimeWorkRequestBuilder<UploadLogWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(uploadLogWorkerRequest2)
//        WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadLogWorkerRequest2.id).observe(this) {
//            if (it.state == WorkInfo.State.SUCCEEDED) {
//                Toast.makeText(this, "任务执行成功， 更新UI", Toast.LENGTH_SHORT).show()
//            } else {
//
//            }
//        }
        WorkManager.getInstance(this).getWorkInfosByTagLiveData("testTag1").observe(this) { it ->
            it.forEach {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(this, "任务执行成功， 更新UI", Toast.LENGTH_SHORT).show()
                } else {

                }
            }
        }
    }
}