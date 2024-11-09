package icu.hku.vekumin.viewModels.alarm

//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import icu.hku.vekumin.alarm.data.AlarmConfig
//import kotlinx.coroutines.launch
//
//class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {
//    private val _alarms = mutableStateOf(listOf<AlarmConfig>())
//    val alarms: State<List<AlarmConfig>> = _alarms
//
//    private val _selectedAlarmIndex = mutableStateOf<Int?>(null)
//
//    private val _showTimePicker = mutableStateOf(false)
//    val showTimePicker: State<Boolean> = _showTimePicker
//
//    fun addOrUpdateAlarm(hour: Int, minute: Int) {
//        viewModelScope.launch {
//            val newAlarms = if (_selectedAlarmIndex.value == null) {
//                _alarms.value + AlarmConfig(hour, minute)
//            } else {
//                _alarms.value.toMutableList().apply {
//                    _selectedAlarmIndex.value?.let { set(it, Pair(hour, minute)) }
//                }
//            }
//            _alarms.value = newAlarms
//            resetPickerState()
//        }
//    }
//
//    fun showTimePicker(show: Boolean) {
//        _showTimePicker.value = show
//    }
//
//    fun setSelectedAlarmIndex(index: Int?) {
//        _selectedAlarmIndex.value = index
//    }
//
//    private fun resetPickerState() {
//        _showTimePicker.value = false
//        _selectedAlarmIndex.value = null
//    }
//}

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import icu.hku.vekumin.alarm.data.AlarmConfig
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmConfig>>(emptyList())
    val alarms: StateFlow<List<AlarmConfig>> = _alarms


    init {
        viewModelScope.launch {
            _alarms.value = repository.getAllAlarms()
        }
    }

    fun addAlarm(alarm: AlarmConfig) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            _alarms.value = repository.getAllAlarms()
        }
    }

    fun updateAlarm(alarm: AlarmConfig) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            _alarms.value = repository.getAllAlarms()
        }
    }

    fun cancelAlarm(alarm: AlarmConfig) {
        viewModelScope.launch {
            repository.cancelAlarm(alarm)
        }
    }

    fun enableAlarm(alarm: AlarmConfig) {
        viewModelScope.launch {
            repository.enableAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: AlarmConfig) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            _alarms.value = repository.getAllAlarms()
        }
    }
}
