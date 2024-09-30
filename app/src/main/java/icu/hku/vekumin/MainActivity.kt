package icu.hku.vekumin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import icu.hku.vekumin.ui.theme.VekuminTheme
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VekuminTheme {
                val navController = rememberNavController()
                val uri = "vekumin://"
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("main") {
                            StartScreen("Vekumin")
                        }
                        composable(
                            "auth?callback={callback}",
                            deepLinks = listOf(navDeepLink { uriPattern = "$uri{callback}" })
                        ) {
                            val callback = it.arguments?.getString("callback")
                            StartScreen("Vekumin $callback")
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun StartScreen(name: String) {
    Column {
        Text(text = "Hello, $name!")
    }
}
