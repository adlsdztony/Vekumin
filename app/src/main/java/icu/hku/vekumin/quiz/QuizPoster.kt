package icu.hku.vekumin.quiz

import com.google.gson.Gson
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

class QuizPoster {
    fun fetchQuizData(): QuizResponse? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://opentdb.com/api.php?amount=10&category=18&difficulty=easy")
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