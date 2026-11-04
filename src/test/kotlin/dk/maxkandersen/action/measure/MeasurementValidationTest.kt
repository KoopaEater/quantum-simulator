package dk.maxkandersen.action.measure

import dk.maxkandersen.qubit.QState
import org.ejml.data.ZMatrixRMaj
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MeasurementValidationTest {

    @Test
    fun fullMeasurementChecksNumQubitsAndBasis() {
        assertFailsWith<IllegalArgumentException> { FullMeasurement(0) }
        assertFailsWith<IllegalArgumentException> { FullMeasurement(1, basis0 = ZMatrixRMaj(4, 1)) }
        assertFailsWith<IllegalArgumentException> { FullMeasurement(1, basis1 = ZMatrixRMaj(2, 2)) }
    }

    @Test
    fun fullMeasurementChecksStateSize() {
        assertFailsWith<IllegalArgumentException> { FullMeasurement(2).measure(QState.zero()) }
    }

    @Test
    fun singleQubitMeasurementChecksQubitAndNumQubitsAndBasis() {
        assertFailsWith<IllegalArgumentException> { SingleQubitMeasurement(0, 0) }
        assertFailsWith<IllegalArgumentException> { SingleQubitMeasurement(5, 2) }
        assertFailsWith<IllegalArgumentException> { SingleQubitMeasurement(-1, 2) }
        assertFailsWith<IllegalArgumentException> { SingleQubitMeasurement(0, 1, basis0 = ZMatrixRMaj(3, 1)) }
    }

    @Test
    fun singleQubitMeasurementChecksStateSize() {
        assertFailsWith<IllegalArgumentException> { SingleQubitMeasurement(0, 2).measure(QState.zero()) }
    }
}
