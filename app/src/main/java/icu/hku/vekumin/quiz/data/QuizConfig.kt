package icu.hku.vekumin.quiz.data


import android.content.Context

data class QuizConfig(var values: Map<String, String>) {
    companion object {
        const val PREFERENCE = "quiz_config"

        fun load(context: Context): QuizConfig? {
            val sharedPref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
            val values = sharedPref.all.mapValues { it.value as String }
            return QuizConfig(values)
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

    fun set(key: String, value: String): QuizConfig {
        values = values + (key to value)
        return this
    }

    fun remove(key: String): QuizConfig {
        values = values - key
        return this
    }

    fun clear(): QuizConfig {
        values = emptyMap()
        return this
    }
}



//// test
//fun main() {
//    val quizConfig = QuizConfig(mapOf("key1" to "value1", "key2" to "value2"))
//    println(quizConfig.get("key1")) // expect: value1
//    println(quizConfig.get("key2")) // expect: value2
//    println(quizConfig.get("key3")) // expect: null
//
//    quizConfig.set("key3", "value3")
//    println(quizConfig.get("key3")) // expect: value3
//
//    quizConfig.set("key3", "value4")
//    println(quizConfig.get("key3")) // expect: value4
//
//    val quizConfig3 = quizConfig.remove("key2")
//    println(quizConfig3.get("key2")) // expect: null
//
//    val quizConfig4 = quizConfig3.clear()
//    println(quizConfig4.get("key1")) // expect: null
//}