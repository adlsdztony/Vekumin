package icu.hku.vekumin.alarm.data

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun fromDaysOfWeek(days: List<Int>): String {
        return days.joinToString(",")
    }

    @TypeConverter
    fun toDaysOfWeek(data: String): List<Int> {
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(",").map { it.toInt() }
        }
    }
}