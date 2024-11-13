package icu.hku.vekumin.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import icu.hku.vekumin.AlarmActivity
import icu.hku.vekumin.R
import icu.hku.vekumin.alarm.data.AlarmConfig
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmConfigString = intent.getStringExtra("alarmConfig") ?: return
        val alarmConfig = AlarmConfig.fromConfigString(alarmConfigString) ?: return
        val targetTime = alarmConfig.toTimeString()
        println("Received target time: $targetTime")

        try {
            if (alarmConfig.repeat) {
                // get next alarm week day
                val date: Date = Date()
                val dayOfWeek = date.toInstant().atZone(java.time.ZoneId.systemDefault()).dayOfWeek.value
                val aheadDays = alarmConfig.daysOfWeek
                    .map { if (it >= dayOfWeek) it - dayOfWeek else 7 - dayOfWeek + it }
                    .filter { it > 0 }
                    .minOrNull() ?: 0
                println("Ahead days: $aheadDays")
                // set next alarm
                val alarmSetter = AlarmSetter()
                alarmSetter.setAlarm(context, alarmConfig, aheadDays = aheadDays)
            }

            val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            if (currentTime == targetTime) {
                timeUp(context)
            }

            println("Current time: $currentTime")
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun timeUp(context: Context) {
        // 使用全屏意图
        showFullScreenIntentNotification(context)
    }

    private fun showFullScreenIntentNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建启动 AuthActivity 的 PendingIntent
        val fullScreenIntent = Intent(context, AlarmActivity::class.java)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channel = NotificationChannel(
            "alarm_channel", "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Alarm Notification Channel"
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
        notificationManager.createNotificationChannel(channel)

        // 构建通知
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 设置通知图标
            .setContentTitle("Alarm Time Reached").setContentText("Tap to Cancel")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 高优先级
            .setCategory(NotificationCompat.CATEGORY_ALARM).setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true) // 设置全屏意图

        // 显示通知
        val notification = notificationBuilder.build()
        notificationManager.notify(1, notification)
    }
}
