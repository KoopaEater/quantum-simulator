package dk.maxkandersen.gate

interface DoubleQubitGate : Gate {
    fun generateMultiGate(q1: Int, q2: Int, numQubits: Int): Gate {
        val gates = mutableListOf<Gate>()
        gates.add(this)
        for (i in 2 until numQubits) {
            gates.add(IDGate())
        }
        val multiGate = MultiGate(gates)
        val swapGate = SwapGate(q1, q2, numQubits)
        return swapGate seq multiGate seq swapGate
    }
}