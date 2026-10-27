package dk.maxkandersen.measure

import dk.maxkandersen.qubit.QState

interface Measurement {
    fun measure(state: QState): QState
}