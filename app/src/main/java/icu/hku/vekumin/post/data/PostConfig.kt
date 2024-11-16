package icu.hku.vekumin.post.data

import android.content.Context

data class PostConfig(var values: Map<String, String>) {
    companion object {
        const val PREFERENCE = "post_config"

        fun load(context: Context): PostConfig? {
            val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
            val values = sharedPref.all.mapValues { it.value as String }
            return PostConfig(values)
        }

        fun clear(context: Context) {
            val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }
        }
    }

    fun save(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            values.forEach { (key, value) ->
                putString(key, value)
            }
            apply()
        }
    }

    fun get(key: String): String? {
        return values[key]
    }

    fun set(key: String, value: String): PostConfig {
        values = values + (key to value)
        return this
    }

    fun remove(key: String): PostConfig {
        values = values - key
        return this
    }

    fun clear(): PostConfig {
        values = emptyMap()
        return this
    }
}