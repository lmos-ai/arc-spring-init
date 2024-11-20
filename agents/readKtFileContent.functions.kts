// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import java.io.File
function(
    name = "readKtFileContent",
    isSensitive = true,
    description = "Read the file content",
    params = types(string("filepath","File location"))
){(filepath)->
    val file = File(filepath)
    println("filePath = ${file.absolutePath}")
    var content : String = ""
    // Check if the file exists
    if (file.exists()) {
        content =  file.readText() // Reads the entire content of the file
    } else {
        content =  "File not found"
    }
    """
       "This is file content ": ${content}
       
    """.trimIndent()
}