plugins {
    jacoco
    alias(libs.plugins.springboot)
    alias(libs.plugins.springbootDependencies)
    alias(libs.plugins.jib)
    alias(libs.plugins.testLogger)
}

configurations {
    compileOnly {
        extendsFrom(configurations["annotationProcessor"])
    }
}

ext["junit-jupiter.version"] = libs.versions.junitVersion.get()

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.apache.logging.log4j:log4j-layout-template-json")
    implementation("org.jspecify:jspecify:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation(libs.bundles.assertj)
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.awaitility)
    testImplementation(libs.bundles.jsonUnit)

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("org.xerial:sqlite-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD_PARALLEL
    showPassed = false
    showPassedStandardStreams = false
}

tasks {
    test {
        useJUnitPlatform()
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    jar {
        archiveClassifier.set("")
        enabled = false
    }
}

jacoco {
    toolVersion = libs.versions.jacocoVersion.get()
}

jib {
    from {
        image = "eclipse-temurin:22-jdk"
        platforms {
            platform {
                os = "linux"
                architecture = "arm64"
            }
            platform {
                os = "linux"
                architecture = "amd64"
            }
        }
    }
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")
        ports = listOf("8080")
    }
}
