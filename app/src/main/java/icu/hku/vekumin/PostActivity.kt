package icu.hku.vekumin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.hku.vekumin.post.data.PostConfig
import icu.hku.vekumin.ui.theme.VekuminTheme
import icu.hku.vekumin.viewModels.post.PostViewModel

class PostActivity : ComponentActivity() {
    private val postViewModel = PostViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VekuminTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = { PostBar(postViewModel) }) { innerPadding ->
                    PostScreen(
                        modifier = Modifier.padding(innerPadding), postViewModel = postViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBar(postViewModel: PostViewModel) {
    val context = LocalContext.current
    TopAppBar(title = { Text("Edit Your Post") }, actions = {
        IconButton(onClick = {
            val title = postViewModel.title
            val content = postViewModel.content
            PostConfig.load(context)?.set("title", title)?.set("content", content)?.save(context)
            (context as? ComponentActivity)?.finish()
        }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
        }
    })
}

@Composable
fun PostScreen(modifier: Modifier = Modifier, postViewModel: PostViewModel) {
    val postConfig = PostConfig.load(LocalContext.current)
    var isTitleFocused by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        postViewModel.title = postConfig?.get("title") ?: ""
        postViewModel.content = postConfig?.get("content") ?: ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp)
            .imePadding()
    ) {
        OutlinedTextField(
            value = postViewModel.title,
            onValueChange = { newValue -> postViewModel.title = newValue },
            label = { Text("Title") },
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth()
                .onFocusChanged { focusState -> isTitleFocused = focusState.isFocused }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedTextField(
            value = postViewModel.content,
            onValueChange = { newValue -> postViewModel.content = newValue },
            label = { Text("Content") },
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth()
                .onFocusChanged { focusState -> isContentFocused = focusState.isFocused }
        )
        LazyRow {
            val chipItems = listOf("Day of Weeks", "Alarm Time", "Question Amount", "Question Difficulty")

            items(chipItems.size) { index ->
                SuggestionChip(
                    onClick = {
                        when {
                            isTitleFocused -> {
                                postViewModel.title += "\${${chipItems[index]}} "
                            }
                            isContentFocused -> {
                                postViewModel.content += "\${${chipItems[index]}} "
                            }
                        }
                    },
                    label = { Text(chipItems[index]) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    VekuminTheme {
        PostScreen(postViewModel = PostViewModel())
    }
}