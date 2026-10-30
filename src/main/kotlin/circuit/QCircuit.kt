package dk.maxkandersen.circuit

import dk.maxkandersen.action.Action
import dk.maxkandersen.qubit.QState

class QCircuit(numQubits: Int) {
    private val actions = mutableListOf<Action>()
    private var startState: QState = QState.baseState(numQubits)

    fun setStartState(state: QState) {
        startState = state
    }

    fun addAction(action: Action) {
        actions.add(action)
    }

    fun run(): QState {
        var state = startState
        for (action in actions) {
            state = action.apply(state)
        }
        return state
    }
}