// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package arc.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownTimer {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val observers = mutableMapOf<ObservationType, MutableList<CountdownTimerObserver>>()

    fun addObserver(type: ObservationType, observer: CountdownTimerObserver) {
        val typeObservers = observers.getOrPut(type) { mutableListOf() }
        typeObservers.add(observer)
    }

    fun start(duration: Long? = 0, unit: String? = DurationUnit.SECONDS.unit) {
        notifyObservers(ObservationType.STARTED, "CountdownTimer Started", duration!!, unit!!)
        coroutineScope.launch {
            delay(durationInMilliSeconds(duration, unit))
            notifyObservers(ObservationType.COMPLETED, "CountdownTimer Completed", duration, unit)
        }
    }

    private fun notifyObservers(type: ObservationType, message: String, duration: Long, unit: String) {
        observers[type]?.forEach { it.update(type, message, duration, unit) }
        observers[ObservationType.ALL]?.forEach { it.update(type, message, duration, unit)}
    }

    private fun durationInMilliSeconds(duration: Long, unit: String) =
        when (unit.lowercase()) {
            DurationUnit.MINUTES.unit -> duration * 60
            DurationUnit.HOURS.unit -> duration * 3600
            DurationUnit.DAYS.unit -> duration * 86400
            DurationUnit.SECONDS.unit -> duration
            DurationUnit.WEEKS.unit -> duration * 604800
            else -> throw IllegalArgumentException("Invalid duration unit")
        } * 1000
}