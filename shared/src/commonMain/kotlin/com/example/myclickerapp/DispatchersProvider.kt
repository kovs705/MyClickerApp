package com.example.myclickerapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface DispatchersProvider {

    fun async(scope: CoroutineScope, block: suspend () -> Unit) : Job

    fun main(scope: CoroutineScope, block: suspend () -> Unit) : Job

    suspend fun swithToMain(block: suspend () -> Unit)

    abstract class Abstract(private val ui : CoroutineDispatcher, private val background : CoroutineDispatcher) : DispatchersProvider {

        override fun async(scope: CoroutineScope, block: suspend () -> Unit) : Job {
            return scope.launch(background) {
                block()
            }
        }

        override fun main(scope: CoroutineScope, block: suspend () -> Unit): Job {
            return scope.launch(ui) {
                block()
            }
        }

        override suspend fun swithToMain(block: suspend () -> Unit) {
            withContext(ui) {
                block()
            }
        }
    }

    //object Base : Abstract(mainDispatcher, ioDispatcher)
    object Base : Abstract(Dispatchers.Main, Dispatchers.Default)

}