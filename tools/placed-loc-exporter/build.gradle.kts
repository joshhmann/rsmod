plugins {
    id("kotlin-conventions")
    application
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.openrs2.cache)
    implementation(libs.openrs2.buffer)
    implementation(libs.netty.buffer)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
}

application {
    mainClass.set("org.rsmod.tools.placedloc.PlacedLocExporterKt")
    // Run from root project directory so .data/ paths resolve
    applicationDefaultJvmArgs = listOf("-Duser.dir=${rootProject.projectDir.absolutePath}")
}

tasks.withType<JavaExec> {
    workingDir = rootProject.projectDir
}
