package icu.hku.vekumin.post.data

import android.content.Context
import icu.hku.vekumin.post.Platform
import icu.hku.vekumin.post.Postable

data class Secret(val platform: Platform, val keys: Map<String, String>) {

    companion object {
        fun fromMap(platform: Platform, keys: Map<String, String>): Secret {
            return Secret(platform, keys)
        }

        fun load(context: Context): Secret? {
            val sharedPref = context.getSharedPreferences("secret", Context.MODE_PRIVATE)
            val platform = sharedPref.getString("platform", null) ?: return null
            val keys = sharedPref.all.filterKeys { it != "platform" } .mapValues { it.value as String }
            return Secret(Platform.valueOf(platform), keys)
        }
    }

    fun save(context: Context) {
        val sharedPref = context.getSharedPreferences("secret", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("platform", platform.name)
            keys.forEach { (key, value) ->
                putString(key, value)
            }
            apply()
        }
    }

    fun createPoster(): Postable {
        return platform.createPoster(this)
    }
}

