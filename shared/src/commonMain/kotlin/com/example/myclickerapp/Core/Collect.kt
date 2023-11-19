package com.example.myclickerapp.Core

interface Collect<T> {
    fun collect(onEach: (T) -> Unit, onCompletion: (cause: Throwable?) -> Unit) : Closeable
}
