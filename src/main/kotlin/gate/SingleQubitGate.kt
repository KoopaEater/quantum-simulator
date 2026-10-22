package dk.maxkandersen.gate

interface SingleQubitGate : Gate {
    fun generateMultiGate(qubit: Int, numQubits: Int): Gate {
        val gates = mutableListOf<Gate>()
        for (i in 0 until numQubits) {
            if (i == qubit) gates.add(this)
            else gates.add(IDGate())
        }
        return MultiGate(gates)
    }
}