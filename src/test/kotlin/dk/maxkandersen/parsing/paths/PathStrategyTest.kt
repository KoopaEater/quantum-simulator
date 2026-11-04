package dk.maxkandersen.parsing.paths

import java.io.File
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PathStrategyTest {

    @Test
    fun noPathJustTextReadsTheArgumentAsText() {
        assertEquals("1\nH 0", NoPathJustTextStrategy.open("1\nH 0").readText())
    }

    @Test
    fun relativePathReadsFromWorkingDirectory() {
        val f = File.createTempFile("pathstrategy", ".qcirc")
        f.writeText("hello")
        assertEquals("hello", RelativePathStrategy.open(f.absolutePath).readText())
        f.delete()
    }

    @Test
    fun resourcePathReadsFromClasspath() {
        val text = ResourcePathStrategy.open("teleportation.qcirc").readText()
        assertTrue(text.contains("CNOT 1 2"), text)
    }

    @Test
    fun resourcePathThrowsOnMissingResource() {
        assertFailsWith<FileNotFoundException> { ResourcePathStrategy.open("does-not-exist.qcirc") }
    }
}
