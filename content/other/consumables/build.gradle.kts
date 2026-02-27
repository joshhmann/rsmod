plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.config)
    implementation(projects.content.mechanics.poison)
}
