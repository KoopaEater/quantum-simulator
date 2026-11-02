package dk.maxkandersen

import dk.maxkandersen.file.QuantumFileReader
import dk.maxkandersen.file.ResourcePathStrategy

fun main() {
    QuantumFileReader().read("C:\\Temp\\test.qcirc")
    QuantumFileReader(pathStrategy = ResourcePathStrategy()).read("test.qcirc")
}

