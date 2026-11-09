plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "cl.pizzeria"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Necesaria para usar Reflection (kotlin.reflect.full.*)
    implementation(kotlin("reflect"))
}

application {
    mainClass.set("MainKt")
}
