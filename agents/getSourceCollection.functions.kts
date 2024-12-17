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
    /**
     * Collect Source information for given below and make it configurable the functions, source_code, projectPath ..etc
     * Note: Read the environment variables
     */
    val functionScriptBasePath = System.getenv("FUNCTIONS_BASE_PATH") ?: "agents"
    val projectDirectoryPath = System.getenv("PROJECT_DIR") ?: ""
    val jarDependencyName = System.getenv("JAR_DEPENDENCY_NAME") ?: "build"
    val allowedApplicationLevelConfig = System.getenv("ALLOWED_READ_RESOURCES") ?: "false"
    val externalConfigFileName = System.getenv("EXTERNAL_CONFIG_FILE_NAME") ?: ""

    println(
        "ENV Variables\n" +
                "FUN_BASE_PATH =  $functionScriptBasePath\n" +
                "PROJECT_DIR_PATH =  $projectDirectoryPath\n" +
                "JAR_DEPENDENCY_NAME =  $jarDependencyName\n" +
                "ALLOWED_READ_RESOURCES = $allowedApplicationLevelConfig\n" +
                "EXTERNAL_CONFIG_FILE_NAME = $externalConfigFileName"
    )
    try {
        //Function DSL or Template
        val functionPath = Paths.get(functionScriptBasePath).toAbsolutePath().toString()
        //source code Base Path
        val sourceCodeBasePath = Paths.get("src/main/kotlin").toAbsolutePath().toString()
        //Build and Properties
        val buildPath = Paths.get("build.gradle.kts").toAbsolutePath().toString()
        val propertiesPath = Paths.get("settings.gradle.kts").toAbsolutePath().toString()
        // README-PATH
        val readMePath = Paths.get("README.md").toAbsolutePath().toString()
        // Application Level Configuration
        val applicationConfigPath = allowedApplicationLevelConfig.toBoolean()
            .let { if (it) Paths.get("src/main/resources").toAbsolutePath().toString() else "" }
        //Application Level External configuration
        val applicationExternalConfigPath = Paths.get(externalConfigFileName).toAbsolutePath().toString()

        // Get all extensions based file names for source code, config, functions and application level
        val sourceCodeFileNames = listKtOrKtsFiles(sourceCodeBasePath, extensions = listOf("kt"))
        println("###Source Code file Count = ${sourceCodeFileNames.count()}")
        val functionFileNames = listKtOrKtsFiles(functionPath, extensions = listOf("functions.kts"))
        val buildFileNames = listKtOrKtsFiles(buildPath, extensions = listOf("gradle.kts"))
        val propertiesFileNames = listKtOrKtsFiles(propertiesPath, extensions = listOf("gradle.kts"))
        val readmeFileNames = listKtOrKtsFiles(readMePath, extensions = listOf("md"))
        val applicationConfigFileNames = if (applicationConfigPath.isNotEmpty()) listKtOrKtsFiles(
            applicationConfigPath,
            extensions = listOf("yml")
        ) else emptyList<String>()

        val applicationExternalFileName = if (applicationExternalConfigPath.isNotEmpty()) listKtOrKtsFiles(
            applicationExternalConfigPath,
            extensions = listOf("yml")
        ) else emptyList<String>()
        """
        "Source Code Files":${sourceCodeFileNames}
        "Template Files ":${functionFileNames}
        "Service Build Info ":${buildFileNames}
        "Service Properties Info":${propertiesFileNames}
        "Service Readme Info":${readmeFileNames}
        "Application Config Files":${applicationConfigFileNames}
        "Project Directory:${projectDirectoryPath}
        "JAR Dependency":${jarDependencyName}
        "External Config Files":${applicationExternalFileName}
    """.trimIndent()
    } catch (ex: Exception) {
        """Issue during source collection with exception ${ex.message}""".trimIndent()
    }
}


// Function to get all extensions based files name from the given directory
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
        .filter { it.isFile && (matchesExtension(it.name, extensions)) }
        .map { "${it.name} -> ${it.absolutePath}" }
        .joinToString(", ")
}

// Helper to check if a file matches any of the extensions
fun matchesExtension(fileName: String, extensions: List<String>): Boolean {
    return extensions.any { fileName.endsWith(".$it", ignoreCase = true) }
}

