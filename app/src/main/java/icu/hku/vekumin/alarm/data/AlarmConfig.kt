package icu.hku.vekumin.alarm.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarmConfigs")
data class AlarmConfig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int>,
    val enabled: Boolean = false,
    val repeat: Boolean = false,
) {
    fun toConfigString(): String {
        if (daysOfWeek.isEmpty()) {
            return "AlarmConfig(id=$id,hour=$hour,minute=$minute,daysOfWeek=empty,enabled=$enabled,repeat=$repeat)"
        }
        return "AlarmConfig(id=$id, hour=$hour, minute=$minute, daysOfWeek=${daysOfWeek.joinToString(" ")}, enabled=$enabled, repeat=$repeat)"
    }

    fun toTimeString(): String {
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    fun toRepeatString(): List<String> {
        return if (!repeat) {
            emptyList()
        } else {
            daysOfWeek.sorted().map {
                when (it) {
                    0 -> "Sun"
                    1 -> "Mon"
                    2 -> "Tue"
                    3 -> "Wed"
                    4 -> "Thu"
                    5 -> "Fri"
                    6 -> "Sat"
                    else -> { "" }
                }
            }
        }
    }

    companion object {
        fun fromConfigString(configString: String): AlarmConfig? {
            if (!configString.startsWith("AlarmConfig(")) {
                return null
            }
            // get config string inside the parentheses
            val configString = configString.substringAfter("(").substringBefore(")")
            val config = configString.split(",")
            if (config.size != 6) {
                return null
            }
            var daysOfWeek = config[3].split("=")[1].split(" ")
            if (daysOfWeek.size == 1 && daysOfWeek[0] == "empty") {
                daysOfWeek = emptyList()
            }

            return AlarmConfig(
                id = config[0].split("=")[1].toInt(),
                hour = config[1].split("=")[1].toInt(),
                minute = config[2].split("=")[1].toInt(),
                daysOfWeek = daysOfWeek.map { it.toInt() },
                enabled = config[4].split("=")[1].toBoolean(),
                repeat = config[5].split("=")[1].toBoolean()
            )
        }
    }
}


//// test
//fun main() {
//    val alarmConfig = AlarmConfig(1, 8, 0, listOf(1, 3, 5), true, true)
//    println("Alarm config: $alarmConfig")
//    val configString = alarmConfig.toConfigString()
//    println("Alarm config string: $configString")
//    val parsedAlarmConfig = AlarmConfig.fromConfigString(configString)
//    println("Parsed alarm config: $parsedAlarmConfig")
//    println("Parsed alarm config repeat string: ${parsedAlarmConfig?.toRepeatString()}")
//    println("Parsed alarm config time string: ${parsedAlarmConfig?.toTimeString()}")
//}