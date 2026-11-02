package dk.maxkandersen.file

import java.io.BufferedReader
import java.io.File
import java.io.InputStream

class RelativePathStrategy : PathStrategy {
    override fun open(filename: String): BufferedReader {
        return File(filename).bufferedReader()
    }
}