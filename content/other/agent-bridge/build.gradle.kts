plugins {
    id("base-conventions")
}

dependencies {
    implementation(projects.api.hunt)
    implementation(projects.api.pluginCommons)
    implementation(projects.api.player)
    implementation(projects.api.registry)
    implementation(projects.api.route)
    implementation(projects.engine.events)
    implementation(projects.engine.game)
    implementation(projects.engine.map)
    implementation(projects.engine.routefinder)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.java.websocket)
}
