plugins {
	id("project-build.common")
//	id("org.springframework.boot") version "2.7.13"
//	id("io.spring.dependency-management") version "1.0.15.RELEASE"
//	kotlin("plugin.spring") version "1.6.21"
}

dependencies {
	implementation(project(":lib-elasticsearch"))
	implementation(project(":lib-misc"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	implementation("rome:rome:1.0") // for RSS feed
	implementation("org.jsoup:jsoup:1.16.1") // for web scraping
}
