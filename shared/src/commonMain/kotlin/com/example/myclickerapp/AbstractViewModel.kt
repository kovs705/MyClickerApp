package com.example.myclickerapp

import com.example.myclickerapp.Core.Handle
import com.example.myclickerapp.Core.Mapper
import com.example.myclickerapp.Core.ObserveState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.plus
import kotlin.coroutines.EmptyCoroutineContext

expect open class AbstractViewModel() {
    val scope: CoroutineScope
}

data class StateHolder<State : Any, SideEffect : Any, Event : Any>(
    val state: State,
    val effects: Set<SideEffect> = emptySet(),
    val events: Set<Event> = emptySet()
)

interface StateUpdater<S : Any, in Action : Any, SideEffect : Any, Event : Any> {
    fun update(action: Action, currentState: S): StateHolder<S, SideEffect, Event>

    abstract class Abstract<S : Any, Action : Actions, SideEffect : Any, Event : Any> :
        StateUpdater<S,Action,SideEffect,Event>
    {

        override fun update(action: Action, currentState: S): StateHolder<S, SideEffect, Event> {
            if (action is Actions.Nothing) {
                return StateHolder(currentState)
            } else {
                throw IllegalStateException("Unsupported action")
            }
        }
    }
}

interface Actions {
    object Nothing : Actions
}

interface Processor<out Action : Any, SideEffect : Any> {
    suspend fun process(effect: SideEffect): Action
}

interface ObserveEvents<Event> {
    fun observeEvent(): SharedFlow<Event>
}

interface ObserveUiState<UiState> {
    fun observeUiState(): StateFlow<UiState>
}

interface StateMachine<State : Any, Action : Any, SideEffect : Any, Event : Any, UIState> :
    Handle<Action>,
    ObserveState<State>, ObserveEvents<Event>, ObserveUiState<UIState>

abstract class BaseViewModel<State : Any, Action : Any, SideEffect : Any, Event : Any, UIState>(
    initialState: State,
    private val dispatchers: DispatchersProvider,
    private val initialEffects: Set<SideEffect> = emptySet(),
    private val updater: StateUpdater<State, Action, SideEffect, Event>,
    private val processor: Processor<Action, SideEffect>,
    private val stateUiMapper: Mapper<State, UIState>? = null,
    private val coroutineExceptionHandler: CoroutineExceptionHandler? = null
) :
    AbstractViewModel(), StateMachine<State, Action, SideEffect, Event, UIState> {
    private val events = MutableSharedFlow<Event>()
    protected val state = MutableStateFlow(initialState)
    private val uiState by lazy { MutableStateFlow(stateUiMapper?.map(state.value)) }

    init {
        processEffects(initialEffects)
    }

    final override fun handle(data: Action) {
        val newState = updater.update(data, state.value)

        if (newState.effects.isNotEmpty()) {
            processEffects(newState.effects)
        }

        if (newState.events.isNotEmpty()) {
            main {
                newState.events.forEach {
                    events.emit(it)
                }
            }
        }

        state.value = newState.state
        uiState.value = stateUiMapper?.map(newState.state)
    }

    final override fun observeEvent(): SharedFlow<Event> {
        return events.asSharedFlow()
    }

    @Suppress("UNCHECKED_CAST")
    final override fun observeUiState(): StateFlow<UIState> {
        check(stateUiMapper != null) { "Define stateUiMapper to use UIState" }
        return uiState.asStateFlow() as StateFlow<UIState>
    }

    final override fun observeState(): StateFlow<State> {
        return state.asStateFlow()
    }

    protected fun async(block: suspend () -> Unit) {
        dispatchers.async(scope) {
            block()
        }
    }

    protected fun main(block: suspend () -> Unit) {
        dispatchers.main(scope) {
            block()
        }
    }

    private fun processEffects(effects: Set<SideEffect>) {
        effects.forEach {
            val coroutineContext = coroutineExceptionHandler ?: EmptyCoroutineContext
            dispatchers.async(scope + coroutineContext) {
                val newAction = processor.process(it)

                dispatchers.swithToMain {
                    handle(newAction)
                }
            }
        }
    }
}