package dk.maxkandersen

import dk.maxkandersen.simulator.Simulator
import java.io.File

fun main(args: Array<String>) {
    val simulator = Simulator()
    val filename = args.getOrNull(0) ?: pickCircuitFile(simulator) ?: return
    val runs = args.getOrNull(1)?.toIntOrNull() ?: askRuns()
    val result = simulator.run(filename, runs)
    println("Results for $filename over ${result.runs} runs:")
    println(result.pretty)
}

private fun pickCircuitFile(simulator: Simulator): String? {
    while (true) {
        val files = simulator.listCircuitFiles()
        if (files.isEmpty()) {
            println("No .qcirc files found in ${File(simulator.prefix).absolutePath}")
            return null
        }
        println("Available circuits:")
        files.forEachIndexed { i, f -> println("  ${i + 1}: ${f.name}") }
        print("Choose a circuit [1-${files.size}, empty to quit]: ")
        val line = readLine()?.trim().orEmpty()
        if (line.isEmpty()) return null
        val idx = line.toIntOrNull()
        if (idx != null && idx in 1..files.size) return files[idx - 1].name
        println("Invalid choice: '$line'")
    }
}

private fun askRuns(): Int {
    while (true) {
        print("Number of runs [1]: ")
        val line = readLine()?.trim().orEmpty()
        if (line.isEmpty()) return 1
        val n = line.toIntOrNull()
        if (n != null && n >= 1) return n
        println("Invalid number: '$line'")
    }
}
