plugins {
	id("project-build.common")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

tasks.getByName("bootJar") {
	enabled = false
}
