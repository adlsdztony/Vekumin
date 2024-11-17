package icu.hku.vekumin.quiz

import android.content.Context
import com.google.gson.Gson
import icu.hku.vekumin.quiz.data.QuizConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class QuizResponse(
    val results: List<QuizResult>
)

data class QuizResult(
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)

class QuizGetter {
    fun fetchQuizData(context: Context): QuizResponse? {
        val config = QuizConfig.load(context)
        val amount = config?.get("amount") ?: "5"
        var difficulty = config?.get("difficulty") ?: "1"
        difficulty = when (difficulty) {
            "1" -> "easy"
            "2" -> "medium"
            "3" -> "hard"
            else -> "easy"
        }
        val url = "https://opentdb.com/api.php?amount=$amount&difficulty=$difficulty&type=boolean"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val jsonResponse = response.body()?.string()
            return jsonResponse?.let {
                Gson().fromJson(it, QuizResponse::class.java)
            }
        }
    }
}