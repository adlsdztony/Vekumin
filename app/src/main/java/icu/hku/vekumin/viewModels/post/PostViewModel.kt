package icu.hku.vekumin.viewModels.post

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class PostViewModel : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
}