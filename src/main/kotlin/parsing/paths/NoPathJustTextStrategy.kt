package dk.maxkandersen.parsing.paths

import java.io.BufferedReader

object NoPathJustTextStrategy : PathStrategy {
    override fun open(filename: String): BufferedReader {
        return filename.byteInputStream().bufferedReader()
    }
}