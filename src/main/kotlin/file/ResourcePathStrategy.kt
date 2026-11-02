package dk.maxkandersen.file

import java.io.BufferedReader
import java.io.FileNotFoundException

class ResourcePathStrategy : PathStrategy {
    override fun open(filename: String): BufferedReader {
        val stream = javaClass.getResourceAsStream("/$filename") ?: throw FileNotFoundException(filename)
        return stream.bufferedReader()
    }
}