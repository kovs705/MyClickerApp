package com.example.myclickerapp.Model

expect class ClickerGameModel() {
    fun getScore(): Int
    fun click()
}

fun createClickerGameModel(): ClickerGameModel {
    return ClickerGameModel()
}