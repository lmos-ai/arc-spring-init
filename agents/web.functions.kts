import io.github.lmos.arc.core.getOrThrow

/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

function(
    name = "get_web_data",
    description = "Returns the data on a webpage",
    params = types(
        string("url", "the url of the webpage"),
    )
) { (url) ->
    debug("$url")
    if (url != null) {
        html(url).getOrThrow()
    } else {
        "invalid url provided!"
    }
}
