plugins {
    alias(libs.plugins.springboot)
    alias(libs.plugins.springbootDependencies)
    alias(libs.plugins.jib)
}

configurations {
    compileOnly {
        extendsFrom(configurations["annotationProcessor"])
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.xerial:sqlite-jdbc")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

jib {
    from {
        image = "eclipse-temurin:17-jdk"
        platforms {
            platform {
                os = "linux"
                architecture = "arm64"
            }
            platform {
                os = "linux"
                architecture = "amd64"
            }
            platform {
                os = "linux"
                architecture = "arm"
            }
        }
    }
    to {
        auth {
            username = project.findProperty("dockerUsername").toString()
            password = project.findProperty("dockerPassword").toString()
        }
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}
