package org.rsmod.content.interfaces.journal.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias journal_components = JournalComponents

object JournalComponents : ComponentReferences() {
    val tab_container = find("side_journal:tab_container")
    val summary_list = find("side_journal:summary_list")
    val quest_list = find("side_journal:quest_list")
    val task_list = find("side_journal:task_list")

    val summary_contents = find("account_summary_sidepanel:summary_contents")
    val summary_click_layer = find("account_summary_sidepanel:summary_click_layer")
}
