plugins {
	id("project-build.spring-boot-lib")
}

dependencies {
	implementation("org.yaml:snakeyaml:2.2")
	implementation("org.springframework.boot:spring-boot-starter")
		{
			exclude(group = "org.yaml", module = "snakeyaml")
		}
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.+")
		{
			exclude(group = "org.yaml", module = "snakeyaml")
		}
}

tasks.getByName("bootJar") {
	enabled = false
}
