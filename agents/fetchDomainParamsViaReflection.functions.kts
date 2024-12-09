// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.isAccessible

function(
    name = "fetchDomainParamsViaReflection",
    isSensitive = true,
    description = "Dynamically extracts field values into json from a given POJO or domain object using reflection, mapping its properties into a key-value representation for further processing or transformation",
    params = types(string("packagePath", "package path"), string("className","name of class"))
) { (packagePath, className) ->
    // Class name including package name
    val className = "$packagePath.$className"
    var jsonString = ""
    println("### Inside reflection tool ###")
    try {
        // Use Class.forName to load the class dynamically
        val agentRequest = Class.forName(className).kotlin
        // create mock instance
        val agentRequestInstance = createMockInstance(agentRequest)
        // Convert the instance to JSON string
        jsonString = toJsonString(agentRequestInstance)
        println("Serialized JSON:\n$jsonString")
    } catch (e: Exception) {
        println("Class $className not found!")
        e.printStackTrace()
    }
    """  
      $jsonString   
    """.trimIndent()
}


// Recursively converts an object to a JSON string
fun toJsonString(obj: Any?): String {
    if (obj == null) return "{}"
    return Json.encodeToString(toJsonObject(obj).toString())
}

// Recursively builds a JsonObject from an object
fun toJsonObject(obj: Any): JsonObject {
    val klass = obj::class
    return buildJsonObject {
        klass.memberProperties.forEach { property ->
            property.isAccessible = true
            val value = property.getter.call(obj)
            val key = property.name

            when {
                value == null -> put(key, JsonPrimitive("null"))
                isPrimitive(value) -> put(key, JsonPrimitive(value.toString()))
                value is List<*> -> put(key, JsonArray(value.mapNotNull { it?.let { toJsonObject(it) } }))
                value is Set<*> -> put(key, JsonArray(value.mapNotNull { it?.let { toJsonObject(it) } }))
                value is Map<*, *> -> put(
                    key,
                    JsonObject(
                        value.entries.associate { (mapKey, mapValue) ->
                            mapKey.toString() to (mapValue?.let { toJsonObject(it) } ?: JsonPrimitive("null"))
                        }
                    )
                )
                else -> put(key, toJsonObject(value)) // Handle nested objects
            }
        }
    }
}

/**
 * Helper function to check if a type is a primitive or String.
 */
fun isPrimitive(value: Any): Boolean {
    return value::class.simpleName in setOf("String", "Int", "Double", "Float", "Long", "Boolean", "Char", "Short")
}

/**
 * Dynamically create an instance for a POJO-like class, even with nested generics.
 */
fun createMockInstance(klass: KClass<*>): Any {
    return try {
        klass.primaryConstructor?.let {
            val args = it.parameters.map { param ->
                createMockValue(param.type) // Dynamically resolve the value of each parameter
            }
            it.call(*args.toTypedArray())
        } ?: klass.createInstance() // No-arg constructor fallback
    } catch (e: Exception) {
        throw IllegalArgumentException("Cannot create instance of ${klass.simpleName}", e)
    }
}

/**
 * Dynamically mock values based on type, including handling generics like List<T>, Set<T>, Map<K,V>
 */
fun createMockValue(type: KType): Any {
    return when (type.classifier) {
        String::class -> "Mock String"
        Int::class -> 42
        Double::class -> 99.99
        Boolean::class -> true
        Char::class -> 'a'
        Long::class -> 42L
        Float::class -> 42.0f
        List::class -> listOf(createMockValue(type.arguments[0].type ?: Any::class.createType()))
        Set::class -> setOf(createMockValue(type.arguments[0].type ?: Any::class.createType()))
        Map::class -> mapOf("Key" to createMockValue(type.arguments[1].type ?: Any::class.createType()))
        else -> {
            val klass = type.classifier as? KClass<*>
            klass?.let { createMockInstance(it) } ?: "Unsupported Type"
        }
    }
}

