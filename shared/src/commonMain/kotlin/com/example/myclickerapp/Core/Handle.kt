package com.example.myclickerapp.Core

interface Handle<T> {
    fun handle(data: T)
}