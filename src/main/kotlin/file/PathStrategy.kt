package dk.maxkandersen.file

import java.io.BufferedReader

interface PathStrategy {
    fun open(filename: String): BufferedReader
}