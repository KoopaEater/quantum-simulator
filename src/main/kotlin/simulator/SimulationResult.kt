package dk.maxkandersen.simulator

/**
 * Result of running a circuit [runs] times: the accumulated probability mass
 * per outcome bitstring. [counts] holds summed probabilities, so the
 * probability of an outcome is counts[outcome] / runs.
 */
class SimulationResult(val runs: Int, val counts: Map<String, Double>) {

    init {
        require(runs >= 1) { "runs must be at least 1, got $runs" }
    }

    val outcomes: Set<String> get() = counts.keys

    fun probability(outcome: String): Double = (counts[outcome] ?: 0.0) / runs

    val pretty: String
        get() {
            val barWidth = 20 // probability 1.0 renders as 20 blocks
            val sb = StringBuilder()
            for (outcome in counts.keys.sorted()) {
                val p = probability(outcome)
                val cells = (p * barWidth).toInt().let { if (p > 0) it.coerceAtLeast(1) else 0 }
                sb.append(outcome)
                    .append(" | ").append("█".repeat(cells).padEnd(barWidth))
                    .append(String.format(java.util.Locale.ROOT, " %.3f", p))
                    .append(" (").append(String.format(java.util.Locale.ROOT, "%.0f", p * runs)).append("/").append(runs).append(")")
                    .append("\n")
            }
            return sb.toString().trimEnd()
        }
}
