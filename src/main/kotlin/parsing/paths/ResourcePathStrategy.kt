package dk.maxkandersen.parsing.paths

import java.io.BufferedReader
import java.io.FileNotFoundException

class ResourcePathStrategy(val prefix: String = "") : PathStrategy {
    override fun open(filename: String): BufferedReader {
        val normalizedPrefix = if (prefix.isEmpty() || prefix.endsWith("/")) prefix else "$prefix/"
        val stream = javaClass.getResourceAsStream("/$normalizedPrefix$filename") ?: throw FileNotFoundException(filename)
        return stream.bufferedReader()
    }
}
