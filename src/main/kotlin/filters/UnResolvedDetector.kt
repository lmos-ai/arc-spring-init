// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0


package ai.ancf.lmos.arc.app.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.agents.dsl.DSLContext

context(DSLContext)
class UnResolvedDetector: AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        if (message.content.contains("NO_ANSWER")) {
            breakWith("Not enough information provided.")
        }
        return message
    }
}