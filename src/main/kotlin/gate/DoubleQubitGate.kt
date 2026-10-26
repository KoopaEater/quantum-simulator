package dk.maxkandersen.gate

interface DoubleQubitGate : Gate {
    fun generateMultiGate(q1: Int, q2: Int, numQubits: Int): Gate {
        val gates = mutableListOf<Gate>()
        gates.add(this)
        for (i in 2 until numQubits) {
            gates.add(IDGate())
        }
        val multiGate = MultiGate(gates)
        val swapGate1 = SwapGate(q1, 0, numQubits)
        val swapGate2 = SwapGate(q2, 1, numQubits)
        return swapGate1 seq swapGate2 seq multiGate seq swapGate2 seq swapGate1
    }
}