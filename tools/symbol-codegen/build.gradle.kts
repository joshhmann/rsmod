plugins {
    id("kotlin-conventions")
    application
}

dependencies {
    implementation(libs.clikt)
}

application {
    mainClass.set("org.rsmod.tools.symbol.codegen.SymbolCodegenKt")
}
