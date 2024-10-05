package icu.hku.vekumin.alarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmConfigDao {
    @Query("SELECT * FROM alarmConfigs")
    suspend fun getAllAlarms(): List<AlarmConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmConfig)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmConfig)

    @Update
    suspend fun updateAlarm(alarm: AlarmConfig)

    @Query("SELECT * FROM alarmConfigs ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAlarm(): AlarmConfig
}