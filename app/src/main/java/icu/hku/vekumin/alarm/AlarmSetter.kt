package icu.hku.vekumin.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import java.util.Calendar


class AlarmSetter {
    fun setAlarm(context: Context, alarmConfig: AlarmConfig, aheadDays: Int = 0) {
        val targetTime = alarmConfig.toTimeString()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!canScheduleExactAlarms(alarmManager)) {
            Toast.makeText(context, "Cannot schedule exact alarms", Toast.LENGTH_SHORT).show()
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmConfig", alarmConfig.toConfigString()) // 将目标时间点传递给广播接收器
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, getHour(targetTime))
            set(Calendar.MINUTE, getMinute(targetTime))
            set(Calendar.SECOND, 0)
        }


        println("Alarm set at: ${calendar.time}")

        val triggerAtMillis = calendar.timeInMillis + aheadDays * 24 * 60 * 60 * 1000

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Cannot schedule exact alarms: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getHour(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt()
    }

    private fun getMinute(time: String): Int {
        val parts = time.split(":")
        return parts[1].toInt()
    }
}