plugins {
    alias(libs.plugins.manes.versions)
    alias(libs.plugins.gradle.download)
    id("kotlin-conventions")
}

allprojects {
    group = "org.rsmod"
    version = "0.0.1"
}

project(":api") { subprojects { group = "org.rsmod.api" } }
project(":content") { subprojects { group = "org.rsmod.content" } }
project(":engine") { subprojects { group = "org.rsmod.engine" } }
project(":server") { subprojects { group = "org.rsmod.server" } }

dependencies {
    implementation(projects.server.install)
}

tasks.register("run") {
    group = "application"
    description = "Runs the RS Mod game server"

    dependsOn(":server:app:run")
}

tasks.register<Exec>("validateSymbols") {
    group = "verification"
    description = "Validate Kotlin symbol references against rev-233 symbol tables."
    workingDir = rootProject.projectDir
    val isWindows = System.getProperty("os.name").contains("Windows", ignoreCase = true)
    val pythonCmd = if (isWindows) "python" else "python3"
    val rootToolsScript = rootProject.projectDir.resolve("../tools/validate_symbols.py")
    val localToolsScript = rootProject.projectDir.resolve("tools/validate_symbols.py")
    val validatorScript = when {
        rootToolsScript.exists() -> rootToolsScript
        localToolsScript.exists() -> localToolsScript
        else -> rootToolsScript
    }
    commandLine(pythonCmd, validatorScript.absolutePath, "--content-only")
}

if (providers.gradleProperty("validateSymbolsGate").orNull == "true") {
    tasks.named("build") {
        dependsOn("validateSymbols")
    }
}

tasks.register<JavaExec>("install") {
    group = "installation"
    description = "Runs the complete RS Mod server installation task."

    mainClass.set("org.rsmod.server.install.GameServerInstallKt")
    classpath = sourceSets["main"].runtimeClasspath

    doLast { logger.lifecycle("Installation process completed.") }
}

tasks.register<JavaExec>("cleanInstall") {
    group = "installation"
    description = "Cleans up any partial or corrupted artifacts from previous RS Mod installations."

    args = getArgsFromProperty("rsmodInstallClean")
    mainClass.set("org.rsmod.server.install.GameServerCleanInstallKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting clean up of any previous installation attempts...") }
    doLast { logger.lifecycle("Clean-up process completed. You can now run the `install` task.") }
}

tasks.register<JavaExec>("downloadCache") {
    group = "cache"
    description = "Runs the cache download & extract task."

    args = getArgsFromProperty("cacheDownload")
    mainClass.set("org.rsmod.server.install.GameServerCacheDownloaderKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the cache download process...") }
    doLast { logger.lifecycle("Cache download completed.") }
}

tasks.register<JavaExec>("packCache") {
    group = "cache"
    description = "Runs the cache packer task."

    args = getArgsFromProperty("cachePack")
    mainClass.set("org.rsmod.server.install.GameServerCachePackerKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the cache-packing process...") }
    doLast { logger.lifecycle("Cache-packing process completed.") }
}

tasks.register<JavaExec>("generateRsa") {
    group = "security"
    description = "Runs the rsa-key generation task."

    args = getArgsFromProperty("rsa")
    mainClass.set("org.rsmod.server.install.GameNetworkRsaGeneratorKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting the rsa-key generation process...") }
    doLast { logger.lifecycle("RSA generation process completed.") }
}

tasks.register<JavaExec>("setupLogbackNovice") {
    description = "Copies the novice logback configuration."

    mainClass.set("org.rsmod.server.install.GameServerLogbackCopyKt")
    classpath = sourceSets["main"].runtimeClasspath

    doFirst { logger.lifecycle("Starting logback copy for novice configuration...") }
    doLast { logger.lifecycle("Logback novice configuration copied successfully.") }
}

tasks.register<JavaExec>("setupLogbackAdvanced") {
    description = "Copies the novice logback configuration."

    mainClass.set("org.rsmod.server.install.GameServerLogbackCopyKt")
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("--advanced-logback")

    doFirst { logger.lifecycle("Starting logback copy for advanced configuration...") }
    doLast { logger.lifecycle("Logback advanced configuration copied successfully.") }
}

fun getArgsFromProperty(propertyName: String): List<String> {
    val argsProp = project.findProperty(propertyName)
    return argsProp?.toString()?.split(" ") ?: emptyList()
}
