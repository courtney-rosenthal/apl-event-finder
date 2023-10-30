import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("com.bmuschko.docker-remote-api")
}

tasks.register<Copy>("dockerSetupBuild") {
    dependsOn("bootJar")
    from(tasks.getByName("bootJar").outputs)
    from("src/main/docker")
    into(layout.buildDirectory.dir("docker"))
}

tasks.register<DockerBuildImage>("dockerBuildImage") {
    dependsOn("dockerSetupBuild")
    setProperty("inputDir", layout.buildDirectory.dir("docker"))
    setProperty("images", setOf("${project.name}:latest"))
}

tasks.getByName("build") {
    finalizedBy("dockerBuildImage")
}