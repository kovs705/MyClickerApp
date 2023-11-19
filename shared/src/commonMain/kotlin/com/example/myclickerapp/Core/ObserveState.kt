package com.example.myclickerapp.Core

import kotlinx.coroutines.flow.Flow

interface ObserveState<State> {

    fun observeState(): Flow<State>

}