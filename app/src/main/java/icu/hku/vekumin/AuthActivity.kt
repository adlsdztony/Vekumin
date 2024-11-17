package icu.hku.vekumin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.hku.vekumin.post.Platform
import icu.hku.vekumin.post.data.Secret
import icu.hku.vekumin.quiz.data.QuizConfig
import icu.hku.vekumin.ui.theme.VekuminTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val data: Uri? = intent?.data

        setContent {
            VekuminTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AuthBar() }) { innerPadding ->
                    Auth(
                        data = data ?: Uri.EMPTY, modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class SettingType {
    AMOUNT, DIFFICULTY, HEALTH
}

data class Setting(
    val type: SettingType, var value: Int, val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBar() {
    TopAppBar(title = { Text("Settings") })
}

@Composable
fun PlatformItem(platform: Platform, currentPlatform: Platform?, onDisable: () -> Unit) {
    val context = LocalContext.current
    val backgroundColor =
        if (currentPlatform == platform) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    Card(
        onClick = {
            if (currentPlatform == platform) {
                onDisable()
            } else {
                // go to auth website
                val url = platform.authUrl
                val intent = Intent(Intent.ACTION_VIEW, url)
                context.startActivity(intent)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)  // 根据条件设置背景色
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = platform.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (currentPlatform == platform) "Enabled, click to delete" else "Click to switch to ${platform.name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PlatformSelection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var platform by remember { mutableStateOf<Platform?>(Secret.load(context)?.platform) }
    LazyColumn(modifier = modifier) {
        items(Platform.entries.size) { index ->
            val platformEnum = Platform.entries[index]
            PlatformItem(platformEnum, platform, onDisable = {
                Secret.clear(context)
                Toast.makeText(context, "Successfully delete secret", Toast.LENGTH_SHORT).show()
                platform = null
            })
        }
    }
}

@Composable
fun SettingItem(title: String, content: String?, onEdit: () -> Unit) {
    Card(
        onClick = {
            onEdit()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (content != null) {
                Text(text = content, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuizDialog(
    title: String,
    openDialog: Boolean,
    sliderValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSliderValue by remember { mutableFloatStateOf(sliderValue) }

    if (openDialog) {
        AlertDialog(onDismissRequest = { onDismiss() }, title = { Text(title) }, text = {
            Column {
                Text("Slide to select a value: ${currentSliderValue.toInt()}")
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = currentSliderValue, onValueChange = { newValue ->
                        currentSliderValue = newValue
                    }, valueRange = valueRange, steps = steps
                )
            }
        }, confirmButton = {
            TextButton(onClick = {
                onConfirm(currentSliderValue)
                onDismiss()
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        })
    }
}

@Composable
fun QuizSettings(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val quizConfig = QuizConfig.load(context)

    // Create a list of settings with appropriate default values
    val settings = remember {
        listOf(
            Setting(SettingType.AMOUNT, quizConfig?.get("amount")?.toInt() ?: 5, "Amount"), Setting(
                SettingType.DIFFICULTY, quizConfig?.get("difficulty")?.toInt() ?: 1, "Difficulty"
            ), Setting(SettingType.HEALTH, quizConfig?.get("health")?.toInt() ?: 5, "Health")
        )
    }

    // State to track which dialog is open, if any
    var openDialog by remember { mutableStateOf<Setting?>(null) }

    Column(modifier = modifier) {
        // Iterate through the settings list and create SettingItems
        settings.forEach { setting ->
            val displayValue = when (setting.type) {
                SettingType.DIFFICULTY -> when (setting.value) {
                    1 -> "Easy"
                    2 -> "Medium"
                    3 -> "Hard"
                    else -> "Unknown"
                }

                else -> setting.value.toString()
            }

            SettingItem(
                title = setting.title,
                content = displayValue,
                onEdit = {
                    openDialog = setting
                },
            )
        }

        // Shared SliderDialog for all settings
        openDialog?.let { setting ->
            QuizDialog(title = setting.title,
                openDialog = true,
                sliderValue = setting.value.toFloat(),
                valueRange = when (setting.type) {
                    SettingType.AMOUNT -> 1f..10f
                    SettingType.DIFFICULTY -> 1f..3f
                    SettingType.HEALTH -> 1f..5f
                },
                steps = when (setting.type) {
                    SettingType.AMOUNT -> 8  // Steps: 1 to 10
                    SettingType.DIFFICULTY -> 1 // Steps: 1 to 3
                    SettingType.HEALTH -> 3  // Steps: 1 to 5
                },
                onConfirm = { newValue ->
                    setting.value = newValue.toInt()
                    quizConfig?.set(setting.type.name.lowercase(), setting.value.toString())?.save(context)
                },
                onDismiss = {
                    openDialog = null // Close dialog
                })
        }
    }
}


@Composable
fun PostSetting(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        SettingItem("Edit Your Post", "Click to edit", onEdit = {
            val intent = Intent(context, PostActivity::class.java)
            context.startActivity(intent)
        })
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp, 0.dp)
        )
        content()
    }
}

@Composable
fun SettingPage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Section(title = "Platform") {
            PlatformSelection()
        }
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        Section(title = "Post") {
            PostSetting()
        }
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        Section(title = "Quiz") {
            QuizSettings()
        }
    }
}

@Composable
fun Auth(data: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // if data is empty, show platform selection
    if (data == Uri.EMPTY) {
        SettingPage(modifier = modifier)
        return
    }

    fun getPlatformFromUri(uri: Uri): Platform? {
        val platform = uri.getQueryParameter("platform") ?: return null
        return try {
            Platform.valueOf(platform)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    val platformEnum = getPlatformFromUri(data)
    platformEnum?.let {
        val keys =
            data.queryParameterNames.associateWith { key -> data.getQueryParameter(key).orEmpty() }
        Secret.fromMap(it, keys).save(context)
        Toast.makeText(context, "Successfully saved secret", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Failed to save secret", Toast.LENGTH_SHORT).show()
    }
    SettingPage(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    VekuminTheme {
        Auth(Uri.EMPTY)
    }
}
