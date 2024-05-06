import io.github.lmos.arc.runner.countdown.timer.ObservationType
import io.github.lmos.arc.runner.countdown.timer.CountdownTimer
import io.github.lmos.arc.runner.countdown.timer.TimerCompletionObserver

/*
 * // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 * //
 * // SPDX-License-Identifier: Apache-2.0
 */

function(
    name = "set_countdown_timer",
    description = "Set the countdown timer for the given duration and unit. Duration unit is specified as sec, min, hr, d, wk, mo, yr",
    group = "countdown_timer",
    params = types(
        string("duration", "duration of countdown timer"),
        string("unit", "unit for duration"),
        )
) { (duration, unit) ->
    val timer = CountdownTimer()
    timer.addObserver(ObservationType.COMPLETED, TimerCompletionObserver())
    timer.start(duration?.toLong(), unit)
    """
        Countdown timer set for $duration $unit
    """
}





