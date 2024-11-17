package icu.hku.vekumin

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import icu.hku.vekumin.post.data.PostConfig
import icu.hku.vekumin.post.data.Secret
import icu.hku.vekumin.quiz.QuizGetter
import icu.hku.vekumin.quiz.QuizResult
import icu.hku.vekumin.quiz.data.QuizConfig
import icu.hku.vekumin.ui.theme.VekuminTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmActivity : ComponentActivity() {

    fun decodeHtmlEntities(text: String): String {
        return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0
    private var wrongAnswersCount = 0
    private lateinit var questions: List<QuizResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        CoroutineScope(Dispatchers.IO).launch {
            val poster = QuizGetter()
            val quizData = poster.fetchQuizData(this@AlarmActivity)
            quizData?.let {
                questions = it.results.map { result ->
                    QuizResult(question = decodeHtmlEntities(result.question),
                        correct_answer = decodeHtmlEntities(result.correct_answer),
                        incorrect_answers = result.incorrect_answers.map { decodeHtmlEntities(it) })
                }
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
                                currentQuestionIndex = currentQuestionIndex,
                                onAnswerSelected = { isCorrect ->
                                    handleAnswer(
                                        isCorrect,
                                        QuizConfig.load(applicationContext)?.get("health")?.toInt()
                                            ?: 5
                                    )
                                })
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun handleAnswer(isCorrect: Boolean, maxHealth: Int) {
        if (isCorrect) {
            correctAnswersCount++
//            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            currentQuestionIndex++
            loadQuestion()
        } else {
            wrongAnswersCount++
            currentQuestionIndex++
            if ((maxHealth - (currentQuestionIndex - correctAnswersCount)) == 0) {
                val context = applicationContext
                Toast.makeText(this, "Check Your Social Media :)", Toast.LENGTH_SHORT).show()
                val postConfig = PostConfig.load(context)
                val title = postConfig?.get("title") ?: "Err Code: 404"
                val content = postConfig?.get("content") ?: "Err Code: 404"
                CoroutineScope(Dispatchers.IO).launch {
                    Secret.load(context)?.createPoster()?.post(title, content)
                }
                finish()
            }
//            Toast.makeText(this, "Wrong Answer :(", Toast.LENGTH_SHORT).show()
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
    currentQuestionIndex: Int,
    onAnswerSelected: (Boolean) -> Unit
) {
    // get the current time
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    val context = activity.applicationContext
    val maxHealth = QuizConfig.load(context)?.get("health")?.toInt() ?: 5

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
        val postConfig = PostConfig.load(context)
        val title = postConfig?.get("title") ?: "Err Code: 404"
        val content = postConfig?.get("content") ?: "Err Code: 404"
        CoroutineScope(Dispatchers.IO).launch {
            Secret.load(context)?.createPoster()?.post(title, content)
        }
        activity.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "bg",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Spacer(modifier = Modifier.height(128.dp))
            Text(
                text = currentTime,
                modifier = modifier,
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 128.sp,
            )
            Text(
                text = "$countdown seconds left",
                modifier = modifier,
                color = Color.Red.copy(alpha = 0.8f),
                fontSize = 24.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Answered: $currentQuestionIndex / $totalQuestionsCount",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 24.sp
            )
            Text(
                text = "${maxHealth - (currentQuestionIndex - correctAnswersCount)} ❤",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 24.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = Color.White
            )
            Column(modifier = Modifier.padding(0.dp, 32.dp)) {
                SingleChoiceSegmentedButtonRow {
                    answers.forEachIndexed { index, answer ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = answers.size
                            ), selected = selectedAnswer == answer, onClick = {
                                selectedAnswer = answer
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(500)
                                    onAnswerSelected(selectedAnswer == correctAnswer)
                                    selectedAnswer = ""
                                }
                            }, colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color.White,
                                activeContentColor = Color.Black,
                                inactiveContainerColor = Color.Transparent,
                                inactiveContentColor = Color.White
                            )
                        ) {
                            Text(answer)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
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
            currentQuestionIndex = 0,
            onAnswerSelected = {})
    }
}