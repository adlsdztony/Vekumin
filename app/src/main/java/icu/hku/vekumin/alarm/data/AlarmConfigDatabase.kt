package icu.hku.vekumin.alarm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AlarmConfig::class], version = 1)
@TypeConverters(Converter::class)
abstract class AlarmConfigDatabase : RoomDatabase() {
    abstract fun alarmConfigDao(): AlarmConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmConfigDatabase? = null

        fun getDatabase(context: Context): AlarmConfigDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmConfigDatabase::class.java,
                    "alarm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}