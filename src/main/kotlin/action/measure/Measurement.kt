package dk.maxkandersen.action.measure

import dk.maxkandersen.action.Action
import dk.maxkandersen.qubit.QState

interface Measurement : Action {
    fun measure(state: QState): QState
    override fun apply(state: QState): QState = measure(state)
}