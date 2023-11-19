package com.example.myclickerapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual open class AbstractViewModel {
    actual val scope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun onCleared(){
        println("Clear")
        scope.cancel()
    }
}