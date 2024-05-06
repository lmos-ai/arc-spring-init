// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package arc.timer


enum class DurationUnit(val unit: String) {
    MINUTES("min"),
    HOURS("hr"),
    DAYS("d"),
    SECONDS("sec"),
    WEEKS("wk"),
    MONTHS("mo"),
    YEARS("yr")
}

enum class ObservationType(val type: String) {
    STARTED("started"),
    COMPLETED("completed"),
    ALL("all"),
}