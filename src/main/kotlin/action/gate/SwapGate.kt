package dk.maxkandersen.action.gate

import org.ejml.data.ZMatrixRMaj

/**
 * SWAP gate: exchanges the states of two qubits [q1] and [q2] in an
 * [numQubits]-qubit register.
 *
 * HOW THE MATRIX IS BUILT
 *
 * A gate on n qubits is a 2^n x 2^n complex matrix (a "ZMatrixRMaj" here).
 * Each row/column index 0 .. 2^n-1 corresponds to one computational basis
 * state, written in binary. With 3 qubits, index 5 = binary 101 = |101>.
 *
 * Bit-order convention: qubit 0 is the LEFTMOST tensor
 * factor, so in the binary index it is the MOST SIGNIFICANT bit. For
 * n = 3:   index = q0 * 4 + q1 * 2 + q2.
 *
 * The SWAP gate is a *permutation matrix*: every column contains exactly one
 * "1" (as a complex 1.0 + 0.0i), everything else is 0. The 1 in column x sits
 * at row y, where y is the basis state you get by exchanging the bits of x
 * that belong to q1 and q2.
 *
 * Example (n = 3, swap q0 and q2): input |100> (q0=1, q2=0) becomes |001>,
 * so column 4 (binary 100) has its 1 in row 1 (binary 001).
 *
 * Because exchanging two EQUAL bits changes nothing, basis states where the
 * two swapped bits agree keep their 1 on the diagonal. The remaining states
 * pair up (2^(n-2) pairs) and exchange rows. For n = 2 this reduces to the
 * familiar 4x4 SWAP: the identity with rows 1 and 2 swapped.
 *
 * Cost: a single pass over all 2^n basis indices - no Kronecker products
 * and no complex arithmetic beyond storing 1.0 + 0.0i.
 */
class SwapGate(q1: Int, q2: Int, numQubits: Int) : Gate {
    init {
        // Qubit indices must be valid positions in the register.
        require(q1 in 0 until numQubits && q2 in 0 until numQubits) { "Invalid qubit index" }
    }

    override val matrix: ZMatrixRMaj = run {
        // Dimension of the matrix: 2^n for n qubits (1 shl n == 2^n).
        val dim = 1 shl numQubits

        // Position of each qubit's bit inside a basis index.
        // Qubit 0 is the most significant bit, so qubit k lives at bit
        // position (numQubits - 1 - k), counting from the right (LSB = 0).
        val p1 = numQubits - 1 - q1
        val p2 = numQubits - 1 - q2

        // A bitmask with 1s at exactly the two bit positions we want to
        // exchange, e.g. n=3, q0 and q2 -> positions 2 and 0 -> mask = 101b.
        // XOR-ing an index with this mask flips both of those bits at once.
        val mask = (1 shl p1) or (1 shl p2)

        // Start from the zero matrix (ZMatrixRMaj is zero-filled).
        val m = ZMatrixRMaj(dim, dim)

        // Fill in one "1" per column. Column x = the input basis state,
        // row y = the state that column is mapped to by the swap.
        for (x in 0 until dim) {
            // Extract the two bits of x that belong to the swapped qubits:
            // "x shr p" shifts bit p down to position 0, "and 1" keeps it.
            val b1 = (x shr p1) and 1
            val b2 = (x shr p2) and 1

            // If the two bits are EQUAL, swapping them does nothing, so the
            // 1 stays on the diagonal (y == x). If they DIFFER, XOR with the
            // mask flips both bits at once, producing the swapped index y.
            // (Doing "x xor mask" unconditionally would be wrong: it would
            // also flip the case where the bits are equal.)
            val y = if (b1 == b2) x else x xor mask

            // Set entry m[y, x] = 1.0 + 0.0i (complex number one).
            m.set(y, x, 1.0, 0.0)
        }
        m
    }
}
