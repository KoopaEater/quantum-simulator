package dk.maxkandersen.circuit

import dk.maxkandersen.action.Action
import dk.maxkandersen.action.gate.Gate
import dk.maxkandersen.qubit.QState

class QCircuit(numQubits: Int) {
    init {
        require(numQubits >= 1) { "A circuit needs at least 1 qubit, got $numQubits" }
    }

    private val actions = mutableListOf<Action>()
    private var startState: QState = QState.baseState(numQubits)

    fun setStartState(state: QState) {
        require(state.nQubits == startState.nQubits) { "New start state has ${state.nQubits} qubits, but this circuit's state has ${startState.nQubits} qubits" }
        startState = state
    }

    fun addAction(action: Action) {
        if (action is Gate) {
            require(action.matrix.numRows == startState.tensor.numRows && action.matrix.numCols == startState.tensor.numRows) { "Gate matrix is ${action.matrix.numRows}x${action.matrix.numCols}, but the circuit state has dimension ${startState.tensor.numRows} (${startState.nQubits} qubits)" }
        }
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