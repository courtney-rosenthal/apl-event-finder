plugins {
	id("project-build.spring-boot-lib")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}

tasks.getByName("bootJar") {
	enabled = false
}
