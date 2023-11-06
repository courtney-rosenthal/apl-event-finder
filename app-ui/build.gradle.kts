import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.0.1"
}

node {
    download.set(true)
    version.set("20.5.0")
}

tasks.register<NpmTask>("npmInstall") {
    setProperty("args", listOf("install"))
}

tasks.register<NpmTask>("build") {
    dependsOn("npmInstall")
    setProperty("args", listOf("run", "build"))
}

tasks.register<Exec>("clean") {
    commandLine = listOf("rm", "-rf", "build")
}