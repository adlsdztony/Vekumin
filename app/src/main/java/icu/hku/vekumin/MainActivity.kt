package icu.hku.vekumin

import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import icu.hku.vekumin.alarm.data.AlarmConfig
import icu.hku.vekumin.alarm.data.AlarmConfigDatabase
import icu.hku.vekumin.ui.theme.VekuminTheme
import icu.hku.vekumin.viewModels.alarm.AlarmViewModel
import java.util.Locale
import icu.hku.vekumin.viewModels.alarm.AlarmRepository
import icu.hku.vekumin.viewModels.alarm.AlarmViewModelFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Build
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.content.ContextCompat.startActivity
import icu.hku.vekumin.alarm.AlarmSetter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val alarmSetter: AlarmSetter = AlarmSetter()

        val alarmDao = AlarmConfigDatabase.getDatabase(applicationContext).alarmConfigDao()
        val repository = AlarmRepository(alarmDao,
            onSetAlarm = { alarmSetter.setAlarm(applicationContext, it) },
            onCancelAlarm = { alarmSetter.cancelAlarm(applicationContext, it) })

        val factory = AlarmViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        var isTimePickerDialogVisible by mutableStateOf(false)

        setContent {
            VekuminTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { isTimePickerDialogVisible = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alarm")
                        }
                    }) { innerPadding ->
                    AlarmApp(modifier = Modifier.padding(innerPadding),
                        viewModel,
                        isTimePickerDialogVisible = isTimePickerDialogVisible,
                        onDismissTimePickerDialog = { isTimePickerDialogVisible = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    val context = LocalContext.current
    TopAppBar(title = { Text("Vekumin") }, actions = {
        IconButton(onClick = {
            val intent = Intent(context, AuthActivity::class.java)
            context.startActivity(intent)
        }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
        }
        IconButton(
            onClick = {
                val intent = Intent(context, AlarmActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Icon(imageVector = Icons.Default.Build, contentDescription = "Alarm")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmApp(
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel,
    isTimePickerDialogVisible: Boolean,
    onDismissTimePickerDialog: () -> Unit
) {
    var alarmTime by remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<Int>() }
    val alarms by viewModel.alarms.collectAsState(initial = emptyList())

    val context = LocalContext.current

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    LaunchedEffect(Unit) {
        if (!canScheduleExactAlarms(alarmManager)) {
            Toast.makeText(context, "Please allow the permission", Toast.LENGTH_SHORT).show()
            val intent = Intent().apply {
                setClassName(
                    "com.android.settings",
                    "com.android.settings.Settings\$AlarmsAndRemindersActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(context, intent, null)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                // Handle the failure to open the settings page
                Toast.makeText(
                    context, "Unable to open alarm and reminder settings page", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp, 0.dp)
    ) {
        if (isTimePickerDialogVisible) {
            val currentTime = java.util.Calendar.getInstance()
            val timePickerState = rememberTimePickerState(
                initialHour = currentTime.get(java.util.Calendar.HOUR_OF_DAY),
                initialMinute = currentTime.get(java.util.Calendar.MINUTE),
                is24Hour = true
            )

            TimePickerDialog(state = timePickerState,
                selectedDays = selectedDays,
                onDismissRequest = { onDismissTimePickerDialog() },
                onConfirm = {
                    val locale = Locale.getDefault()
                    alarmTime = String.format(
                        locale, "%02d:%02d", timePickerState.hour, timePickerState.minute
                    )
                    val newAlarm = AlarmConfig(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        daysOfWeek = selectedDays.toList(),
                        enabled = true,
                        repeat = !selectedDays.isEmpty()
                    )
                    viewModel.addAlarm(newAlarm)
                    selectedDays.clear()
                    alarmTime = ""
                    onDismissTimePickerDialog()
                })
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(alarms) { alarm ->
                AlarmItem(alarm = alarm, onToggle = {
                    if (alarm.enabled) {
                        viewModel.cancelAlarm(alarm)
                    } else {
                        viewModel.enableAlarm(alarm)
                    }
                    viewModel.updateAlarm(alarm.copy(enabled = !alarm.enabled))
                }, onDelete = {
                    viewModel.deleteAlarm(alarm)
                }, onUpdate = { hour, minute, daysOfWeek ->
                    viewModel.updateAlarm(
                        alarm.copy(
                            hour = hour, minute = minute, daysOfWeek = daysOfWeek
                        )
                    )
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    state: TimePickerState,
    selectedDays: MutableList<Int>,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {

    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = onConfirm) {
            Text("Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text("Cancel")
        }
    }, text = {
        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            TimePicker(state = state)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

                items(daysOfWeek.size) { index ->
                    FilterChip(selected = selectedDays.contains(index), onClick = {
                        if (selectedDays.contains(index)) {
                            selectedDays.remove(index)
                        } else {
                            selectedDays.add(index)
                        }
                    }, label = { Text(daysOfWeek[index]) }, modifier = Modifier.padding(4.dp)
                    )
                }
            }

        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmItem(
    alarm: AlarmConfig,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Int, Int, List<Int>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedDays = remember { mutableStateListOf<Int>().apply { addAll(alarm.daysOfWeek) } }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { Text("Delete Alarm") },
            text = { Text("Are you sure you want to delete this alarm?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            })
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = alarm.hour, initialMinute = alarm.minute, is24Hour = true
        )

        TimePickerDialog(state = timePickerState,
            selectedDays = selectedDays,
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                alarm.repeat = selectedDays.isNotEmpty()
                onUpdate(timePickerState.hour, timePickerState.minute, selectedDays)
                showTimePicker = false
            })
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                showDialog = true
            }, onTap = {
                showTimePicker = true
            })
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = alarm.toTimeString(), style = MaterialTheme.typography.displayLarge)
                Text(
                    text = if (!alarm.repeat) "Every Day" else "Repeats on ${
                        alarm.toRepeatString().joinToString(" ")
                    }"
                )
            }
            Switch(
                checked = alarm.enabled,
                onCheckedChange = { onToggle() },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
