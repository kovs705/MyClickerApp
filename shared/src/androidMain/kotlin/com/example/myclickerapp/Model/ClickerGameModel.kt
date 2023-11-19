package com.example.myclickerapp.Model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class ClickerGameModel actual constructor() {
    actual fun getScore(): Int {
        return clickCount.value
    }

    actual fun click() {
        _clickCount.value += 1
    }

    actual val _clickCount = MutableStateFlow(0)
    actual val clickCount: StateFlow<Int> = _clickCount
}