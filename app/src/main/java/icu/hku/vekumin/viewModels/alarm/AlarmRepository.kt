package icu.hku.vekumin.viewModels.alarm

import icu.hku.vekumin.alarm.data.AlarmConfig
import icu.hku.vekumin.alarm.data.AlarmConfigDao


class AlarmRepository(
    private val alarmDao: AlarmConfigDao,
    private val onSetAlarm: (AlarmConfig) -> Unit,
    private val onCancelAlarm: (AlarmConfig) -> Unit
) {


    suspend fun getAllAlarms() = alarmDao.getAllAlarms()

    suspend fun insertAlarm(alarm: AlarmConfig) {
        alarmDao.insertAlarm(alarm)
        val latestAlarm = alarmDao.getLatestAlarm()
        onSetAlarm(latestAlarm)
    }

    suspend fun updateAlarm(alarm: AlarmConfig) {
        alarmDao.updateAlarm(alarm)
        onSetAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: AlarmConfig) {
        alarmDao.deleteAlarm(alarm)
        onCancelAlarm(alarm)
    }
}
