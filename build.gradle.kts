import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "5.0.0.4638"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", System.getenv("SONAR_PROJECT"))
        property("sonar.organization", System.getenv("SONAR_ORG"))
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("$rootDir/config/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}