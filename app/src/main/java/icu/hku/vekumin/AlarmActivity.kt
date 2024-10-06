package icu.hku.vekumin

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.hku.vekumin.ui.theme.VekuminTheme
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VekuminTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlarmScreen(
                        modifier = Modifier.padding(innerPadding), activity = this@AlarmActivity
                    )
                }
            }
        }
    }
}

@Composable
fun AlarmScreen(modifier: Modifier = Modifier, activity: Activity) {
    // get the current time
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    // countdown state
    var countdown by remember { mutableStateOf(60) }

    // LaunchedEffect to update countdown every second
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        // destroy current screen
        activity.finish()
    }

    Column(
        modifier = Modifier
            .padding(8.dp, 0.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = currentTime, modifier = modifier, style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "$countdown seconds left",
            modifier = modifier,
            color = MaterialTheme.colorScheme.error
        )
        Button(modifier = Modifier.padding(64.dp), onClick = {}) {
            Text(
                "Do Math", style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VekuminTheme {
        AlarmScreen(activity = ComponentActivity())
    }
}