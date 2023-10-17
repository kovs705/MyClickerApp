package com.example.myclickerapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform