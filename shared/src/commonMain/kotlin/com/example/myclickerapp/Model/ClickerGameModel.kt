package com.example.myclickerapp.Model

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

expect class ClickerGameModel() {

    // MutableStateFlow is a state-holder observable that emits updates to the value to its collectors.
    val _clickCount: MutableStateFlow<Int>
    // StateFlow is a read-only version of MutableStateFlow. It is used to prevent unwanted modifications.
    val clickCount: StateFlow<Int>

    fun getScore(): Int
    fun click()
}

fun createClickerGameModel(): ClickerGameModel {
    return ClickerGameModel()
}