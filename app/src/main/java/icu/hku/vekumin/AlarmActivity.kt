package icu.hku.vekumin

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import icu.hku.vekumin.quiz.QuizPoster
import icu.hku.vekumin.quiz.QuizResult
import icu.hku.vekumin.ui.theme.VekuminTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmActivity : ComponentActivity() {
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0
    private var wrongAnswersCount = 0
    private lateinit var questions: List<QuizResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            val poster = QuizPoster()
            val quizData = poster.fetchQuizData()
            quizData?.let {
                questions = it.results
                loadQuestion()
            }
        }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val answers = question.incorrect_answers.toMutableList().apply {
                add(question.correct_answer)
                shuffle()
            }

            runOnUiThread {
                setContent {
                    VekuminTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            AlarmScreen(modifier = Modifier.padding(innerPadding),
                                activity = this@AlarmActivity,
                                question = question.question,
                                correctAnswer = question.correct_answer,
                                answers = answers,
                                correctAnswersCount = correctAnswersCount,
                                totalQuestionsCount = questions.size,
                                onAnswerSelected = { isCorrect ->
                                    handleAnswer(isCorrect)
                                })
                        }
                    }
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "All Wrong Answer :(", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleAnswer(isCorrect: Boolean) {
        if (isCorrect) {
            correctAnswersCount++
            if (correctAnswersCount >= 5) {
                Toast.makeText(
                    this, "Correct! You answered 5 questions correctly.", Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
                currentQuestionIndex++
                loadQuestion()
            }
        } else {
            wrongAnswersCount++
            currentQuestionIndex++
            Toast.makeText(this, "Wrong Answer :(", Toast.LENGTH_SHORT).show()
            loadQuestion()
        }
    }
}

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    activity: Activity,
    question: String,
    correctAnswer: String,
    answers: List<String>,
    correctAnswersCount: Int,
    totalQuestionsCount: Int,
    onAnswerSelected: (Boolean) -> Unit
) {
    // get the current time
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    // countdown state
    var countdown by remember { mutableIntStateOf(60) }

    // selected answer state
    var selectedAnswer by remember { mutableStateOf("") }

    // LaunchedEffect to update countdown every second
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        // destroy current screen
        Toast.makeText(activity, "Check Your Social Media :)", Toast.LENGTH_SHORT).show()
        activity.finish()
    }

    Column(
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(0.dp, 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime,
                modifier = modifier,
                style = MaterialTheme.typography.displayLarge,
                fontSize = 128.sp
            )
            Text(
                text = "$countdown seconds left",
                modifier = modifier,
                color = MaterialTheme.colorScheme.error,
                fontSize = 24.sp
            )
        }
        Column(
            modifier = Modifier.padding(0.dp, 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Correct: $correctAnswersCount / $totalQuestionsCount",
                modifier = modifier,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.padding(0.dp, 4.dp))
            Text(
                text = question,
                modifier = modifier,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp
            )
            Column(modifier = Modifier.padding(0.dp, 16.dp)) {
                answers.forEach { answer ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedAnswer == answer,
                            onClick = { selectedAnswer = answer })
                        Text(text = answer)
                    }
                }
            }
            Button(onClick = {
                onAnswerSelected(selectedAnswer == correctAnswer)
            }) {
                Text("Submit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VekuminTheme {
        AlarmScreen(activity = ComponentActivity(),
            question = "What is 1 + 1?",
            correctAnswer = "2",
            answers = listOf("2", "3", "4", "5"),
            correctAnswersCount = 0,
            totalQuestionsCount = 10,
            onAnswerSelected = {})
    }
}