package org.rsmod.content.interfaces.skill.guides.configs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias guide_interfaces = SkillGuideInterfaces

typealias guide_components = SkillGuideComponents

object SkillGuideInterfaces : InterfaceReferences() {
    val skill_guide = find("skill_guide")
}

object SkillGuideComponents : ComponentReferences() {
    val attack = find("stats:attack")
    val strength = find("stats:strength")
    val defence = find("stats:defence")
    val ranged = find("stats:ranged")
    val prayer = find("stats:prayer")
    val magic = find("stats:magic")
    val runecraft = find("stats:runecraft")
    val construction = find("stats:construction")
    val hitpoints = find("stats:hitpoints")
    val agility = find("stats:agility")
    val herblore = find("stats:herblore")
    val thieving = find("stats:thieving")
    val crafting = find("stats:crafting")
    val fletching = find("stats:fletching")
    val slayer = find("stats:slayer")
    val hunter = find("stats:hunter")
    val mining = find("stats:mining")
    val smithing = find("stats:smithing")
    val fishing = find("stats:fishing")
    val cooking = find("stats:cooking")
    val firemaking = find("stats:firemaking")
    val woodcutting = find("stats:woodcutting")
    val farming = find("stats:farming")

    val subsection_1 = find("skill_guide:00")
    val subsection_2 = find("skill_guide:01")
    val subsection_3 = find("skill_guide:02")
    val subsection_4 = find("skill_guide:03")
    val subsection_5 = find("skill_guide:04")
    val subsection_6 = find("skill_guide:05")
    val subsection_7 = find("skill_guide:06")
    val subsection_8 = find("skill_guide:07")
    val subsection_9 = find("skill_guide:08")
    val subsection_10 = find("skill_guide:09")
    val subsection_11 = find("skill_guide:10")
    val subsection_12 = find("skill_guide:11")
    val subsection_13 = find("skill_guide:12")
    val subsection_14 = find("skill_guide:13")
    val subsection_entry_list = find("skill_guide:icons")
    val close_button = find("skill_guide:close")
}
