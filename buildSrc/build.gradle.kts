plugins {
    `kotlin-dsl`
}

dependencies {
    // for plugins
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.8.0")
    implementation("org.springframework.boot:org.springframework.boot.gradle.plugin:2.7.14")
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.0.15.RELEASE")
    implementation("com.bmuschko:gradle-docker-plugin:9.3.6")
}

repositories {
    mavenCentral()

    gradlePluginPortal()
}
