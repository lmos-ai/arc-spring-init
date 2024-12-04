// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

function(
    name = "get_web_content",
    description = "Returns content from the web.",
    params = types(
        string("url", "The URL of the content to fetch.")
    )
) { (url) ->
   httpGet(url.toString())
}