// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import java.io.File
import java.nio.file.Paths

function(
    name = "getSourceCollection",
    isSensitive = true,
    description = "A compilation of file paths containing essential details and resources related to the service or project",
) {
    //Collect Source information for given below

    //Function DSL or Template
    val functionPath = Paths.get("templates").toAbsolutePath().toString()
    //Domain Entities
    val domainPath = Paths.get("data").toAbsolutePath().toString()
    //Build and Properties
    val buildPath = Paths.get("build.gradle.kts").toAbsolutePath().toString()
    val propertiesPath = Paths.get("settings.gradle.kts").toAbsolutePath().toString()
    // README-PATH
    val readMePath = Paths.get("README.md").toAbsolutePath().toString()

    // Get all .kt and .kts file names for domains and functions
    val domainFileNames = listKtOrKtsFiles(domainPath)
    val functionFileNames = listKtOrKtsFiles(functionPath)
    val buildFileNames = listKtOrKtsFiles(buildPath, extensions = listOf("gradle.kts"))
    val propertiesFileNames = listKtOrKtsFiles(propertiesPath, extensions = listOf("gradle.kts"))
    val readmeFileNames = listKtOrKtsFiles(readMePath, extensions = listOf("md"))
    """
        "Domain Files":${domainFileNames}
        "Template Files ":${functionFileNames}
        "Service Build Info ":${buildFileNames}
        "Service Properties Info":${propertiesFileNames}
        "Service Readme Info":${readmeFileNames}
    """.trimIndent()
}


// Function to list all .kt or .kts file names from the given directory
fun listKtOrKtsFiles(directoryPath: String, extensions: List<String> = listOf("kt", "kts")): String {
    val file = File(directoryPath)
    // Check if the path is a file
    if (file.isFile) {
        if (matchesExtension(file.name, extensions)) {
            return "${file.name} -> ${file.absolutePath}"
        } else {
            throw IllegalArgumentException("The file does not have the required extension: $extensions")
        }
    }

    // Check if the path is a directory
    if (!file.exists() || !file.isDirectory) {
        throw IllegalArgumentException("Invalid directory path: $directoryPath")
    }

    // Process directory for matching files
    return file.walk()
        .filter { it.isFile && (matchesExtension(it.name,extensions)) }
        .map { "${it.name} -> ${it.absolutePath}" }
        .joinToString(", ")
}

// Helper to check if a file matches any of the extensions
fun matchesExtension(fileName: String, extensions: List<String>): Boolean {
    return extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

