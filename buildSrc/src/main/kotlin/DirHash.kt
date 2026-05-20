import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.math.BigInteger
import java.security.MessageDigest

@CacheableTask
abstract class DirHash : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val contents: DirectoryProperty

    @get:Input
    abstract val hashMethod: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun perform() {
        val digestAlgorithm = hashMethod.get()
        val md = MessageDigest.getInstance(digestAlgorithm)

        val directory = contents.asFile.get()

        // Get all regular files sorted for consistent ordering
        val files = directory.walkTopDown()
            .filter { it.isFile }
            .sortedBy { it.relativeTo(directory).path }
            .toList()

        // Update digest with each file's content
        files.forEach { file ->
            md.update(file.readBytes())
            // Optionally add a separator to avoid ambiguity
            md.update(byteArrayOf(0))
        }

        // Generate final hash
        val finalHash = BigInteger(1, md.digest()).toString(16)

        // Write output
        outputDir.asFile.get().mkdirs()
        val hashFile = outputDir.asFile.get().resolve("hash.txt")
        hashFile.writeText(finalHash)

        logger.lifecycle("Computed $digestAlgorithm hash for ${files.size} files: $finalHash")
    }
}