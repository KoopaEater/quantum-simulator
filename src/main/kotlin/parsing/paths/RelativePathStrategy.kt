package dk.maxkandersen.parsing.paths

import java.io.BufferedReader
import java.io.File

class RelativePathStrategy : PathStrategy {
    override fun open(filename: String): BufferedReader {
        return File(filename).bufferedReader()
    }
}