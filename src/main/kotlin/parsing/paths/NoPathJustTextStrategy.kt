package dk.maxkandersen.parsing.paths

import java.io.BufferedReader

class NoPathJustTextStrategy(val text: String) : PathStrategy {
    override fun open(filename: String): BufferedReader {
        return text.byteInputStream().bufferedReader()
    }
}