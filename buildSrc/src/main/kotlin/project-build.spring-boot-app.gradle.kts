plugins {
    id("project-build.spring-boot-common")
    id("project-build.docker")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

// do not build plain jar, just bootJar
tasks.getByName<Jar>("jar") {
    enabled = false
}