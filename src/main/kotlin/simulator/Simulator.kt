package dk.maxkandersen.simulator

import dk.maxkandersen.circuit.QCircuit
import dk.maxkandersen.parsing.QParser
import dk.maxkandersen.parsing.paths.RelativePathStrategy
import dk.maxkandersen.random.RandomStrategy
import dk.maxkandersen.random.StandardRandomStrategy
import java.io.File

/**
 * Backend for the simulator UI. Reads circuit files exclusively through
 * [RelativePathStrategy] under [prefix], runs them, and aggregates the
 * outcome probabilities into a [SimulationResult] histogram.
 */
class Simulator(val prefix: String = "", rand: RandomStrategy = StandardRandomStrategy()) {

    private val parser = QParser(RelativePathStrategy(prefix), rand)

    fun load(filename: String): QCircuit = parser.parse(filename)

    fun listCircuitFiles(): List<File> =
        File(prefix.ifEmpty { "." }).listFiles { f -> f.isFile && f.extension == "qcirc" }?.sortedBy { it.name }.orEmpty()

    fun run(filename: String, runs: Int = 1): SimulationResult {
        require(runs >= 1) { "runs must be at least 1, got $runs" }
        val circuit = load(filename)
        val totals = mutableMapOf<String, Double>()
        repeat(runs) {
            for ((outcome, p) in circuit.run().probabilities()) {
                totals[outcome] = (totals[outcome] ?: 0.0) + p
            }
        }
        return SimulationResult(runs, totals)
    }
}
