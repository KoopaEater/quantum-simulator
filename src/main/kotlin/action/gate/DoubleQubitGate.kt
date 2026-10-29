package dk.maxkandersen.action.gate

interface DoubleQubitGate : Gate {
    fun generateMultiGate(q1: Int, q2: Int, numQubits: Int): Gate {
        val gates = mutableListOf<Gate>()
        gates.add(this)
        for (i in 2 until numQubits) {
            gates.add(IDGate())
        }
        val multiGate = MultiGate(gates)
        val swapGate1 = SwapGate(q1, 0, numQubits)
        val q2n = if (q2 == 0) q1 else q2
        val swapGate2 = SwapGate(q2n, 1, numQubits)
        return swapGate1 seq swapGate2 seq multiGate seq swapGate2 seq swapGate1
    }
}