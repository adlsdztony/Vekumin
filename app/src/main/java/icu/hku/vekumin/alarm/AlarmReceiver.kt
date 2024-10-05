package icu.hku.vekumin.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import icu.hku.vekumin.alarm.data.AlarmConfig
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmConfigString = intent.getStringExtra("alarmConfig")?: return
        val alarmConfig = AlarmConfig.fromConfigString(alarmConfigString)?: return
        val targetTime = alarmConfig.toTimeString()
        println("Received target time: $targetTime")

        try {
            if (alarmConfig.repeat) {
                // set next alarm
                val alarmSetter = AlarmSetter()
                alarmSetter.setAlarm(context, alarmConfig, aheadDays = 1)
            }

            val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            if (currentTime == targetTime) {
                timeUp(context)
            }

            Toast.makeText(context, "Current time: $currentTime", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun timeUp(context: Context) {
        // TODO: Function when time is up
        Toast.makeText(context, "Reached target time, playing audio", Toast.LENGTH_SHORT).show()
    }
}