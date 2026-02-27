plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.config)
    implementation(projects.api.cache)
    implementation(projects.api.player)
    implementation(projects.api.playerOutput)
    implementation(projects.api.combat.combatCommons)
    implementation(projects.api.combat.combatManager)
    implementation(projects.api.repo)
    implementation(projects.api.random)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.script)
    implementation(projects.api.type.typeReferences)
    implementation(projects.engine.map)
    implementation(projects.engine.objtx)
    implementation(libs.guice)
}
