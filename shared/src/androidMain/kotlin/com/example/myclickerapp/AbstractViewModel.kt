package com.example.myclickerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class AbstractViewModel : ViewModel() {
    actual val scope: CoroutineScope
        get() = viewModelScope

}