plugins {
    id("base-conventions")
}

dependencies {
    implementation(libs.bundles.logging)
    implementation(projects.api.hunt)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.player)
    implementation(projects.api.registry)
    implementation(projects.api.route)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.module)
    implementation(projects.engine.routefinder)
    implementation(projects.content.other.agentBridge)
    implementation(libs.jackson.module.kotlin)
}
