plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.content.skills.agility)
    implementation(projects.api.scriptAdvanced)
    implementation(projects.api.config)
}
