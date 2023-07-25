plugins {
    `kotlin-dsl`
}

dependencies {
    // to use plugin: kotlin("jvm")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.8.0")
}

repositories {
    mavenCentral()
}
