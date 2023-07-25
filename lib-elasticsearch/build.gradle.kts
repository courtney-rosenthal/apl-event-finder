//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("project-build.common")
	id("org.springframework.boot") version "2.7.13"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("plugin.spring") version "1.6.21"
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("com.google.guava:guava:32.1.1-jre")
	implementation("org.fissore:slf4j-fluent:0.14.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.assertj:assertj-core:3.24.2")

	// for integration testing
	testImplementation("org.springframework.boot:spring-boot-starter-web")
}



tasks.getByName("bootJar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
}
