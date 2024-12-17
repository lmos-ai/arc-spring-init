// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

function(
    name = "reflectionTool",
    isSensitive = true,
    description = "Dynamically extracts domain object from jar using reflection",
    params = types(
        string("className", "A path of class"),
        string("projectDirectory", "Project directory path"),
        string("jarDependency", "Jar dependency name"),
    )
) { (className, projectDirectory, jarDependency) ->
    println(
        "### Inside ReflectionTool And Variables are\n" +
                "className =  $className\n" +
                "projectDirectory = $projectDirectory\n" +
                "jarDependency = $jarDependency\n"
    )
    var reflectData = ""
    try {
        if (!listOf(className, projectDirectory, jarDependency).any { it.isNullOrEmpty() }) {
            // Step 1: Resolve JAR dependencies
            val jarPaths = resolveDependencyJar(jarDependency!!, projectDirectory!!)
            println("### Step-1 Jar-Path based on dependency is done with value = $jarPaths")
            if (jarPaths.isNotEmpty()) {
                // Step 2: Load the class from JAR
                val kClass = loadClassFromJar(jarPaths, className!!) ?: null
                println("### Step-2 Load the kClass from jar is done")
                if (kClass != null) {
                    // Step 3: Extract class structure
                    val classStructure = extractClassStructure(kClass)
                    println("### Step-3 Extract the class Structure is Done")
                    if (classStructure != null) {
                        // Step 4: Generate Kotlin code
                        reflectData = generateKotlinCode(classStructure)
                        println("### Step-4 Generate Kotlin code is Done")
                    }
                }
            }
        }
    } catch (ex: Exception) {
        println("### Exception occur while extracting classes")
    }
    """$reflectData""".trimIndent()
}

/**
 * A utility function for resolving JAR dependencies from a Gradle project.
 */
fun resolveDependencyJar(dependency: String, projectDir: String): List<String> {
    val project = File(projectDir)
    if (!project.exists()) throw IllegalArgumentException("Project directory does not exist: $projectDir")

    // Simulated resolution logic (adjust for actual tooling API integration)
    val jarDir = File(projectDir, "build/libs")
    if (!jarDir.exists()) throw IllegalArgumentException("No JARs found in $jarDir. Build the project first.")

    return jarDir.walk()
        .filter { it.isFile && it.extension == "jar" && it.name.contains(dependency.split(":")[1]) }
        .map { it.absolutePath }
        .toList()
}

/**
 * Loads a class from the resolved JAR file using reflection.
 */
fun loadClassFromJar(jarPaths: List<String>, className: String): KClass<*>? {
    val classLoader = URLClassLoader(jarPaths.map { File(it).toURI().toURL() }.toTypedArray())
    return try {
        val clazz = Class.forName(className, true, classLoader)
        clazz.kotlin
    } catch (e: ClassNotFoundException) {
        println("Class $className not found in JARs: $jarPaths")
        null
    } finally {
        classLoader.close()
    }
}

/**
 * Extracts the class structure using Kotlin reflection.
 */
fun extractClassStructure(kClass: KClass<*>): ClassStructure {
    val fields = kClass.memberProperties.map { property ->
        FieldInfo(
            name = property.name,
            type = property.returnType.toString(),
            isNullable = property.returnType.isMarkedNullable,
            annotations = property.annotations.map { it.annotationClass.simpleName.orEmpty() }
        )
    }

    return ClassStructure(
        packageName = kClass.java.packageName,
        className = kClass.simpleName.orEmpty(),
        fields = fields
    )
}

/**
 * Generates Kotlin code for the given class structure.
 */
fun generateKotlinCode(classStructure: ClassStructure): String {
    val fieldsCode = classStructure.fields.joinToString(",\n    ") { field ->
        "val ${field.name}: ${field.type}${if (field.isNullable) "?" else ""}"
    }

    return """
        package ${classStructure.packageName}
        
        data class ${classStructure.className}(
            $fieldsCode
        )
    """.trimIndent()
}

data class FieldInfo(
    val name: String,
    val type: String,
    val isNullable: Boolean,
    val annotations: List<String>
)

data class ClassStructure(
    val packageName: String,
    val className: String,
    val fields: List<FieldInfo>
)


