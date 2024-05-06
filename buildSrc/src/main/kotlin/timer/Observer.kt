// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package arc.timer

import arc.alarm.staticAlarm


interface CountdownTimerObserver {
    fun update(type: ObservationType, message: String, duration: Long, unit: String)
}

class TimerCompletionObserver: CountdownTimerObserver {
    override fun update(type: ObservationType, message: String, duration: Long, unit: String) {
        staticAlarm()
    }
}