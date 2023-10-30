import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("com.google.guava:guava:32.1.1-jre")
	implementation("org.fissore:slf4j-fluent:0.14.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.5")
	testImplementation("org.assertj:assertj-core:3.24.2")

	// for integration testing
	testImplementation("org.springframework.boot:spring-boot-starter-web")
}

group = "crosenthal.com"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
