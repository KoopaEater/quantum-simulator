package dk.maxkandersen.simulator

import dk.maxkandersen.random.FixedRandomStrategy
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SimulatorTest {

    private fun tempSimulator(rand: Double = 0.0, block: (dir: File) -> Unit): File {
        val dir = Files.createTempDirectory("simulator-test").toFile()
        block(dir)
        return dir
    }

    @Test
    fun bellCircuitWithoutMeasureGivesTheoretical50_50() {
        val dir = tempSimulator { it.resolve("bell.qcirc").writeText("2\nH 0\nCNOT 0 1\n") }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        val res = sim.run("bell.qcirc", runs = 100)
        assertEquals(100, res.runs)
        assertEquals(setOf("00", "01", "10", "11"), res.outcomes) // zero-probability outcomes kept
        assertEquals(0.5, res.probability("00"), 1e-12)
        assertEquals(0.5, res.probability("11"), 1e-12)
        assertEquals(0.0, res.probability("01"), 1e-12)
        assertEquals(0.0, res.probability("10"), 1e-12)
        dir.deleteRecursively()
    }

    @Test
    fun measuredCircuitWithFixedRandIsDeterministic() {
        val dir = tempSimulator { it.resolve("meas.qcirc").writeText("1\nH 0\nMEASURE\n") }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        val res = sim.run("meas.qcirc", runs = 50)
        assertEquals(setOf("0", "1"), res.outcomes)
        assertEquals(1.0, res.probability("0"), 1e-12)
        assertEquals(0.0, res.probability("1"), 1e-12)
        dir.deleteRecursively()
    }

    @Test
    fun listCircuitFilesFindsOnlyQcircSortedByName() {
        val dir = tempSimulator {
            it.resolve("b.qcirc").writeText("1\n")
            it.resolve("a.qcirc").writeText("1\n")
            it.resolve("notes.txt").writeText("not a circuit")
        }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        assertEquals(listOf("a.qcirc", "b.qcirc"), sim.listCircuitFiles().map { it.name })
        dir.deleteRecursively()
    }

    @Test
    fun missingFileThrows() {
        val dir = tempSimulator { }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        assertFailsWith<java.io.FileNotFoundException> { sim.run("nope.qcirc") }
        dir.deleteRecursively()
    }

    @Test
    fun runsMustBePositive() {
        val dir = tempSimulator { it.resolve("c.qcirc").writeText("1\n") }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        assertFailsWith<IllegalArgumentException> { sim.run("c.qcirc", runs = 0) }
        dir.deleteRecursively()
    }

    @Test
    fun prettyContainsAllOutcomesAndBars() {
        val dir = tempSimulator { it.resolve("bell.qcirc").writeText("2\nH 0\nCNOT 0 1\n") }
        val sim = Simulator(dir.absolutePath, FixedRandomStrategy(0.0))
        val pretty = sim.run("bell.qcirc", 10).pretty
        assertTrue("00" in pretty && "11" in pretty, pretty)
        assertTrue("01" in pretty && "0.000" in pretty, pretty) // zero-probability lines shown
        assertTrue("█" in pretty, pretty)
        dir.deleteRecursively()
    }
}
