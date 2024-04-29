/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

/**
 * My weather function.
 */
function(
    name = "get_weather",
    description = "Returns real-time weather information for any location",
    params = types(string("location", "a city to obtain the weather for."))
) { (location) ->
    val locationSpecified = location != "unknown" && location?.isNotEmpty() == true
    val locationToUse = if (locationSpecified) location else memory("weather_location")
    if (locationToUse == null) {
        "Please provide a location."
    } else {
        if (locationSpecified) memory("weather_location", location, MemoryScope.LONG_TERM)
        if (!locationSpecified) +"Using your location preference of $locationToUse."

        """
         The weather is good in $locationToUse. It is 20 degrees celsius.
        """
    }
}



