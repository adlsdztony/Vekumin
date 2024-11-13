package icu.hku.vekumin.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.content.ContextCompat.startActivity
import icu.hku.vekumin.alarm.data.AlarmConfig
import java.util.Calendar
import java.util.Date

class AlarmSetter {
    fun setAlarm(context: Context, alarmConfig: AlarmConfig) {
        val targetTime = alarmConfig.toTimeString()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!canScheduleExactAlarms(alarmManager)) {
            val intent = Intent().apply {
                setClassName(
                    "com.android.settings",
                    "com.android.settings.Settings\$AlarmsAndRemindersActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(context, intent, null)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                // Handle the failure to open the settings page
                Toast.makeText(
                    context, "Unable to open alarm and reminder settings page", Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(
                "alarmConfig", alarmConfig.toConfigString()
            ) // Pass the target time to the broadcast receiver
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmConfig.id + 323,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, getHour(targetTime))
            set(Calendar.MINUTE, getMinute(targetTime))
            set(Calendar.SECOND, 0)
        }
        var aheadDays = 0
        if (alarmConfig.repeat) {
            val date: Date = Date()
            val dayOfWeek = date.toInstant().atZone(java.time.ZoneId.systemDefault()).dayOfWeek.value
            aheadDays = alarmConfig.daysOfWeek
                .map { if (it >= dayOfWeek) it - dayOfWeek else 7 - dayOfWeek + it }
                .filter { it > 0 }
                .minOrNull() ?: 0
            if (aheadDays == 0) {
                if (date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().isAfter(
                        java.time.LocalTime.of(getHour(targetTime), getMinute(targetTime))
                    )
                ) {
                    aheadDays = 7
                }
            }
        } else {
            if (calendar.before(Calendar.getInstance())) {
                aheadDays = 1
            }
        }
        println("Ahead days: $aheadDays")

        val triggerAtMillis = calendar.timeInMillis + aheadDays * 24 * 60 * 60 * 1000

        // back into calendar
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = triggerAtMillis
        println("Alarm ${alarmConfig.id} set at: ${calendar2.time}")

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
            )
        } catch (e: SecurityException) {
            Toast.makeText(
                context, "Cannot schedule exact alarms: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cancelAlarm(context: Context, alarmConfig: AlarmConfig) {
        println("Alarm ${alarmConfig.id} at ${alarmConfig.toTimeString()} canceled")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmConfig.id + 323,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
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