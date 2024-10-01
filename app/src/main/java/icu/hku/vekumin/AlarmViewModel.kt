package icu.hku.vekumin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {
    private val _alarms = mutableStateOf(listOf<Pair<Int, Int>>())
    val alarms: State<List<Pair<Int, Int>>> = _alarms

    private val _selectedAlarmIndex = mutableStateOf<Int?>(null)

    private val _showTimePicker = mutableStateOf(false)
    val showTimePicker: State<Boolean> = _showTimePicker

    fun addOrUpdateAlarm(hour: Int, minute: Int) {
        val newAlarms = if (_selectedAlarmIndex.value == null) {
            _alarms.value + Pair(hour, minute)
        } else {
            _alarms.value.toMutableList().apply {
                _selectedAlarmIndex.value?.let { set(it, Pair(hour, minute)) }
            }
        }
        _alarms.value = newAlarms
        resetPickerState()
    }

    fun showTimePicker(show: Boolean) {
        _showTimePicker.value = show
    }

    fun setSelectedAlarmIndex(index: Int?) {
        _selectedAlarmIndex.value = index
    }

    private fun resetPickerState() {
        _showTimePicker.value = false
        _selectedAlarmIndex.value = null
    }
}
