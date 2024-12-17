// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import java.io.File

function(
    name = "extractFileContent",
    isSensitive = true,
    description = "Retrieve and return the content of a specified file.",
    params = types(string("filepath", "File Location")),
) { (filepath) ->
    //Extract the file content based on file location to path
    val file = File(filepath)
    var content: String = ""
    // Check if the file exists
    if (file.exists()) {
        content = file.readText() // Reads the entire content of the file
    } else {
        content = "No Information Available"
    }
    """
       "File Content ": $content
    """.trimIndent()
}