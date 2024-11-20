// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import java.io.File
import java.nio.file.Paths
function(
    name = "listKtFiles",
    isSensitive = true,
    description = "Read all kotlin files. with respect to the directory",
) {
        // Define separate paths for models and functions

        val domainModelsPath = Paths.get( "data").toAbsolutePath().toString()
        val functionDslPath = Paths.get( "templates").toAbsolutePath().toString()


        // Get all .kt and .kts file names for domains and functions
        val domainModelFileNames = listKtOrKtsFiles(domainModelsPath)
        val functionDslFileNames = listKtOrKtsFiles(functionDslPath)
    """
        "Domain Files with filePath ":${domainModelFileNames}
        "Template Files with filePath ":${functionDslFileNames}
    """.trimIndent()
    }


// Function to list all .kt or .kts file names from the given directory
fun listKtOrKtsFiles(directoryPath: String, extension: String = "kt"): String {
    val directory = File(directoryPath)
    if (!directory.exists() || !directory.isDirectory) {
        throw IllegalArgumentException("Invalid directory path: $directoryPath")
    }
    return directory.walk()
        .filter { it.isFile && (it.extension == extension || it.extension == "${extension}s") }
        .map { "${it.name} -> ${it.absolutePath}" } // Extract only the file name
        .joinToString(", ")
}