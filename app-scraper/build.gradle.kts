plugins {
	id("project-build.spring-boot-app")
}

dependencies {
	implementation(project(":lib-elasticsearch"))
	implementation(project(":lib-misc"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	implementation("rome:rome:1.0") // for RSS feed
	implementation("org.jsoup:jsoup:1.16.1") // for web scraping
}
