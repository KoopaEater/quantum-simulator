package dk.maxkandersen.parsing.paths

import java.io.BufferedReader
import java.io.File

class RelativePathStrategy(val prefix: String = "") : PathStrategy {
    private val normalizedPrefix = if (prefix.isEmpty() || prefix.endsWith("/") || prefix.endsWith("\\")) prefix else prefix + File.separator
    override fun open(filename: String): BufferedReader {
        return File(normalizedPrefix + filename).bufferedReader()
    }
}
