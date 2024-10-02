package icu.hku.vekumin.alarm

import android.content.Context

data class AlarmConfigs(var alarmConfigs: MutableMap<Int, AlarmConfig>) {
    fun getAlarmConfigs(): Map<Int, AlarmConfig> {
        return alarmConfigs
    }

    fun addAlarmConfig(alarmConfig: AlarmConfig) {
        alarmConfigs[alarmConfig.hashCode()] = alarmConfig
    }

    fun removeAlarmConfig(alarmConfig: AlarmConfig) {
        alarmConfigs.remove(alarmConfig.hashCode())
    }

    fun save(context: Context) {
        val sharedPref = context.getSharedPreferences("alarmConfigs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            alarmConfigs.forEach { (key, value) ->
                putString(key.toString(), value.toConfigString())
            }
            apply()
        }
    }

    companion object {
        fun load(context: Context): AlarmConfigs? {
            val sharedPref = context.getSharedPreferences("alarmConfigs", Context.MODE_PRIVATE)
            val alarmConfigs = mutableMapOf<Int, AlarmConfig>()
            sharedPref.all.forEach { (key, value) ->
                alarmConfigs[key.toInt()] =
                    AlarmConfig.fromConfigString(value as String) ?: return null
            }
            return AlarmConfigs(alarmConfigs)
        }
    }
}