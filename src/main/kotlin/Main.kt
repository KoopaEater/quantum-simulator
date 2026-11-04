package dk.maxkandersen

import dk.maxkandersen.parsing.QuantumParser
import dk.maxkandersen.parsing.paths.ResourcePathStrategy

fun main() {
    QuantumParser().read("C:\\Temp\\test.qcirc")
    QuantumParser(pathStrategy = ResourcePathStrategy()).read("test.qcirc")
}

