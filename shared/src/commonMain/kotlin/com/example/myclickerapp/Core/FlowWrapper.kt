package com.example.myclickerapp.Core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.wrapFlow(): FlowWrapper<T> = FlowWrapper(this)

class FlowWrapper<T>(private val source: Flow<T>) : Flow<T> by source , Collect<T>{

    override fun collect(onEach: (T) -> Unit, onCompletion: (cause: Throwable?) -> Unit): Closeable {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope.launch {
            try {
                collect {
                    onEach(it)
                }
                onCompletion(null)
            } catch (e: Throwable) {
                onCompletion(e)
            }
        }

        return object : Closeable {
            override fun close() {
                print("close call")
                scope.cancel()
            }

        }
    }
}