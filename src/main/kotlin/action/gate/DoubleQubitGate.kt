package dk.maxkandersen.action.gate

interface DoubleQubitGate : Gate {
    fun generateMultiGate(q1: Int, q2: Int, numQubits: Int): Gate {
        require(numQubits >= 2) { "A two-qubit gate needs a register of at least 2 qubits, got $numQubits" }
        require(q1 in 0 until numQubits) { "Qubit index $q1 is out of range for $numQubits qubits (valid: 0..${numQubits - 1})" }
        require(q2 in 0 until numQubits) { "Qubit index $q2 is out of range for $numQubits qubits (valid: 0..${numQubits - 1})" }
        require(q1 != q2) { "The two qubits of a two-qubit gate must be different, both were $q1" }
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