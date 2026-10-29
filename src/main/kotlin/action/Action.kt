package dk.maxkandersen.action

import dk.maxkandersen.qubit.QState

interface Action {
    fun apply(state: QState): QState
}