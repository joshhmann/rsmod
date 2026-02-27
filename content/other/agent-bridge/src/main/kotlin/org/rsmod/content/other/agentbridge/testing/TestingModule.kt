package org.rsmod.content.other.agentbridge.testing

import org.rsmod.plugin.module.PluginModule

class TestingModule : PluginModule() {
    override fun bind() {
        bindInstance<SaveStateManager>()
        bindInstance<TestResultReporter>()
        bindInstance<ActionRetry>()
        LearningDocs.initializeDefaults()
    }
}
