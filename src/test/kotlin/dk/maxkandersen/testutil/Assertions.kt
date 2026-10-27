package dk.maxkandersen.testutil

import dk.maxkandersen.qubit.QState

object Assertions {

    fun assertIdentical(expected: QState, actual: QState) {
        assert(expected isIdenticalTo actual) { generateMessage(expected.pretty, actual.pretty) }
    }

    fun <T> assertEqual(expected: T, actual: T) {
        assert(expected == actual) { generateMessage(expected.toString(), actual.toString())}
    }

    fun generateMessage(expected: String, actual: String): String = "Expected: $expected\nBut got:  $actual"
}