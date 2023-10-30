plugins {
	id("project-build.common")
	id("project-build.docker")
}

dependencies {
	implementation(project(":lib-elasticsearch"))
	implementation(project(":lib-misc"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
}
