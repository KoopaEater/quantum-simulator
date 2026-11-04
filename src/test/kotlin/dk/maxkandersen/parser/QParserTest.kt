package dk.maxkandersen.parser

import dk.maxkandersen.parsing.QParser
import dk.maxkandersen.parsing.exception.EmptyFileException
import dk.maxkandersen.parsing.exception.InvalidArgException
import dk.maxkandersen.parsing.exception.NumArgsException
import dk.maxkandersen.parsing.exception.NumQubitsException
import dk.maxkandersen.parsing.exception.UnknownCommandException
import dk.maxkandersen.parsing.paths.NoPathJustTextStrategy
import dk.maxkandersen.parsing.paths.ResourcePathStrategy
import dk.maxkandersen.qubit.QState
import dk.maxkandersen.testutil.Assertions.assertIdentical
import org.ejml.data.ZMatrixRMaj
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class QParserTest {

    val q0 = QState.zero()
    val q1 = QState.one()
    val qp = QState.plus()
    val qn1 = QState.negOne()

    val qi1 = QState(ZMatrixRMaj(2, 1, true,
        0.0, 0.0,
        0.0, 1.0)) // i|1>
    val qt1 = QState(ZMatrixRMaj(2, 1, true,
        0.0, 0.0,
        sqrt(0.5), sqrt(0.5))) // e^(i pi/4)|1>

    @Test
    fun testIDGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nI 0")
        val res = circuit.run()
        assertIdentical(QState.zero(), res)
    }

    @Test
    fun testXGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nX 0")
        val res = circuit.run()
        assertIdentical(q1, res)
    }

    @Test
    fun testZGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nX 0\nZ 0")
        val res = circuit.run()
        assertIdentical(qn1, res)
    }

    @Test
    fun testHGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nH 0")
        val res = circuit.run()
        assertIdentical(qp, res)
    }

    @Test
    fun testSGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nX 0\nS 0")
        val res = circuit.run()
        assertIdentical(qi1, res)
    }

    @Test
    fun testTGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nX 0\nT 0")
        val res = circuit.run()
        assertIdentical(qt1, res)
    }

    @Test
    fun testPGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nX 0\nP $PI 0")
        val res = circuit.run()
        assertIdentical(qn1, res)
    }

    @Test
    fun testCNOTGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nCNOT 0 1")
        val res = circuit.run()
        assertIdentical(q1 kron q1, res)
    }

    @Test
    fun testCXGateAlias() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 1\nCX 1 0")
        val res = circuit.run()
        assertIdentical(q1 kron q1, res)
    }

    @Test
    fun testCZGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nX 1\nCZ 0 1")
        val res = circuit.run()
        assertIdentical(qn1 kron q1, res)
    }

    @Test
    fun testCHGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nCH 0 1")
        val res = circuit.run()
        assertIdentical(q1 kron qp, res)
    }

    @Test
    fun testCSGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nX 1\nCS 0 1")
        val res = circuit.run()
        assertIdentical(q1 kron qi1, res)
    }

    @Test
    fun testCTGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nX 1\nCT 0 1")
        val res = circuit.run()
        assertIdentical(q1 kron qt1, res)
    }

    @Test
    fun testCPGate() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("2\nX 0\nX 1\nCP 0 0 1") // CP(0) = identity
        val res = circuit.run()
        assertIdentical(q1 kron q1, res)
    }

    @Test
    fun testMeasureSingleQubit() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nMEASURE 0") // |0> always measures as 0
        val res = circuit.run()
        assertIdentical(q0, res)
    }

    @Test
    fun testMeasureFullIsBasisState() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("1\nH 0\nMEASURE")
        val res = circuit.run()
        assertTrue(res isIdenticalTo q0 || res isIdenticalTo q1, res.pretty)
    }

    @Test
    fun testLeadingCommentsAndBlankLinesAllowed() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val circuit = parser.parse("# a comment\n\n1\nH 0")
        val res = circuit.run()
        assertIdentical(qp, res)
    }

    @Test
    fun testEmptyFileThrowsEmptyFileException() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        assertFailsWith<EmptyFileException> { parser.parse("") }
    }

    @Test
    fun testOnlyCommentsThrowsEmptyFileException() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        assertFailsWith<EmptyFileException> { parser.parse("# just\n# comments\n") }
    }

    @Test
    fun testNonIntegerNumQubitsThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<NumQubitsException> { parser.parse("abc\nH 0") }
        assertEquals(1, e.line)
    }

    @Test
    fun testMultipleTokensOnHeaderThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<NumQubitsException> { parser.parse("1 2\nH 0") }
        assertEquals(1, e.line)
    }

    @Test
    fun testZeroOrNegativeNumQubitsThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        assertFailsWith<NumQubitsException> { parser.parse("0\n") }
        assertFailsWith<NumQubitsException> { parser.parse("-2\n") }
    }

    @Test
    fun testNumQubitsErrorReportsLineAfterComments() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<NumQubitsException> { parser.parse("# comment\n\n2x\nH 0\n") }
        assertEquals(3, e.line)
    }

    @Test
    fun testUnknownCommandThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<UnknownCommandException> { parser.parse("1\nH 0\nFOO 0") }
        assertEquals(3, e.line)
        assertEquals("FOO", e.command)
    }

    @Test
    fun testWrongArgCountThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e1 = assertFailsWith<NumArgsException> { parser.parse("1\nH") }
        assertEquals(2, e1.line)
        assertEquals("H", e1.command)
        assertEquals("1", e1.expected)
        assertEquals(0, e1.actual)
        val e2 = assertFailsWith<NumArgsException> { parser.parse("2\nCNOT 0") }
        assertEquals("2", e2.expected)
        assertEquals(1, e2.actual)
        assertFailsWith<NumArgsException> { parser.parse("1\nMEASURE 0 1") }
    }

    @Test
    fun testNonNumericQubitThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<InvalidArgException> { parser.parse("1\nH x") }
        assertEquals(2, e.line)
        assertEquals("H", e.command)
        assertEquals(1, e.argNum)
        assertEquals("x", e.actual)
    }

    @Test
    fun testOutOfRangeQubitThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<InvalidArgException> { parser.parse("2\nH 0\nX 5") }
        assertEquals(3, e.line)
        assertEquals(1, e.argNum)
        assertEquals("5", e.actual)
    }

    @Test
    fun testSameQubitDoubleGateThrows() {
        val parser = QParser(pathStrategy = NoPathJustTextStrategy)
        val e = assertFailsWith<InvalidArgException> { parser.parse("2\nCNOT 0 0") }
        assertEquals(2, e.line)
        assertEquals("CNOT", e.command)
    }

    @Test
    fun testTeleportationFromResourceFile() {
        val parser = QParser(pathStrategy = ResourcePathStrategy)
        val circuit = parser.parse("teleportation.qcirc")
        val res = circuit.run()
        // qubit 0 teleports |0> to qubit 2; qubits 0,1 are the random measurement outcomes
        assertTrue(
            (res isIdenticalTo (q0 kron q0 kron q0)) ||
            (res isIdenticalTo (q0 kron q1 kron q0)) ||
            (res isIdenticalTo (q1 kron q0 kron q0)) ||
            (res isIdenticalTo (q1 kron q1 kron q0)), res.pretty)
    }
}
