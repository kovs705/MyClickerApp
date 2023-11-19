package com.example.myclickerapp.Core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface StateMachine<State : Any, Event : Any, SideEffect> : Handle<Event>, ObserveState<State> {

    abstract class Abstract<State : Any, Event : Any, SideEffect>(initialState: State) :
        StateMachine<State, Event, SideEffect> {
        protected val state = MutableStateFlow(initialState)

        final override fun observeState(): Flow<State> {
            return state.asStateFlow()
        }
    }
}