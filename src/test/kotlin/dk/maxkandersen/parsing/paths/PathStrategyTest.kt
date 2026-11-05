package dk.maxkandersen.parsing.paths

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
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
        assertEquals("hello", RelativePathStrategy().open(f.absolutePath).readText())
        f.delete()
    }

    @Test
    fun resourcePathReadsFromClasspath() {
        val text = ResourcePathStrategy().open("teleportation.qcirc").readText()
        assertTrue(text.contains("CNOT 1 2"), text)
    }

    @Test
    fun resourcePathThrowsOnMissingResource() {
        assertFailsWith<FileNotFoundException> { ResourcePathStrategy().open("does-not-exist.qcirc") }
    }

    @Test
    fun relativePathPrefixIsPrepended() {
        val dir = Files.createTempDirectory("prefix-test").toFile()
        File(dir, "hej.txt").writeText("hello prefix")
        assertEquals("hello prefix", RelativePathStrategy(dir.absolutePath + File.separator).open("hej.txt").readText())
        assertEquals("hello prefix", RelativePathStrategy(dir.absolutePath).open("hej.txt").readText()) // separator auto-added
        dir.delete()
    }

    @Test
    fun resourcePathPrefixSelectsClasspathFolder() {
        // teleportation.qcirc sits at the classpath root; a bogus prefix must miss it
        assertFailsWith<FileNotFoundException> { ResourcePathStrategy("no-such-folder").open("teleportation.qcirc") }
        // prefix with and without trailing slash both work for the root
        assertTrue(ResourcePathStrategy("").open("teleportation.qcirc").readText().contains("CNOT 1 2"))
    }
}
