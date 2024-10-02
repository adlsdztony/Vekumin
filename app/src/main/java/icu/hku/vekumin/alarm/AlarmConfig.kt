package icu.hku.vekumin.alarm

data class AlarmConfig(
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = false,
    val repeat: Boolean = false
) {
    fun toTimeString(): String {
        return "$hour:$minute"
    }

    fun toConfigString(): String {
        return "AlarmConfig(hour=$hour, minute=$minute, enabled=$enabled, repeat=$repeat)"
    }

    // from config string
    companion object {
        fun fromConfigString(configString: String?): AlarmConfig? {
            if (configString == null) return null
            val config = configString
                .removePrefix("AlarmConfig(")
                .removeSuffix(")")
                .split(", ")
                .map { it.split("=") }
                .map { it[0] to it[1] }
                .toMap()
            return AlarmConfig(
                config["hour"]?.toInt() ?: return null,
                config["minute"]?.toInt() ?: return null,
                config["enabled"]?.toBoolean() ?: return null,
                config["repeat"]?.toBoolean() ?: return null
            )
        }
    }
}