plugins {
	id("project-build.common")
}

dependencies {
	implementation(project(":lib-elasticsearch"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
//	implementation("org.springdoc:springdoc-openapi-kotlin:1.7.0")
}
