package dk.maxkandersen.parsing.paths

import java.io.BufferedReader
import java.io.FileNotFoundException

object ResourcePathStrategy : PathStrategy {
    override fun open(filename: String): BufferedReader {
        val stream = javaClass.getResourceAsStream("/$filename") ?: throw FileNotFoundException(filename)
        return stream.bufferedReader()
    }
}