plugins {
    id("project-build.spring-boot-common")
    id("project-build.docker")
}

// do not build plain jar, just bootJar
tasks.getByName<Jar>("jar") {
    enabled = false
}
