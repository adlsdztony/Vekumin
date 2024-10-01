package icu.hku.vekumin

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import icu.hku.vekumin.ui.theme.VekuminTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val alarmViewModel = viewModel<AlarmViewModel>()
            VekuminTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { alarmViewModel.showTimePicker(true) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
                        }
                    }) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                    ) {
                        composable("main") {
                            StartScreen(
                                alarmViewModel = alarmViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(title = { Text("Vekumin") }, actions = {
        IconButton(onClick = { /* TODO: show settings */ }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    alarmViewModel: AlarmViewModel, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp)
    ) {
        val alarms by alarmViewModel.alarms
        val showTimePicker by alarmViewModel.showTimePicker

        if (showTimePicker) {
            val currentTime = Calendar.getInstance()
            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(Calendar.MINUTE),
                is24Hour = true
            )

            TimePickerDialog(state = timePickerState,
                onDismissRequest = { alarmViewModel.showTimePicker(false) },
                onConfirm = {
                    alarmViewModel.addOrUpdateAlarm(timePickerState.hour, timePickerState.minute)
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(alarms) { index, (hour, minute) ->
                AlarmItem(hour, minute, onClick = {
                    alarmViewModel.setSelectedAlarmIndex(index)
                    alarmViewModel.showTimePicker(true)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState, onDismissRequest: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = onConfirm) { Text("Confirm") }
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) { Text("Cancel") }
    }, text = {
        TimePicker(state = state, modifier = Modifier.fillMaxWidth())
    })
}

@Composable
fun AlarmItem(hour: Int, minute: Int, onClick: () -> Unit) {
    val locale = Locale.getDefault()
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(locale, "%02d:%02d", hour, minute),
                style = MaterialTheme.typography.displayLarge
            )
            Switch(
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    VekuminTheme {
        StartScreen(AlarmViewModel())
    }
}