package dk.maxkandersen.parsing.paths

import java.io.BufferedReader

interface PathStrategy {
    fun open(filename: String): BufferedReader
}