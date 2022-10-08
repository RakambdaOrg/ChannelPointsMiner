plugins {
    application
    jacoco
    alias(libs.plugins.shadow)
    alias(libs.plugins.lombok)
    alias(libs.plugins.jib)
    alias(libs.plugins.gitProperties)
    alias(libs.plugins.testLogger)
}

dependencies {
    implementation(libs.slf4j)
    implementation(libs.bundles.log4j2)

    implementation(libs.bundles.unirest)
    implementation(libs.picocli)
    implementation(libs.bundles.jackson)
    implementation(libs.httpclient)
    implementation(libs.lang3)
    implementation(libs.websocket)
    implementation(libs.kittehIrc)
    implementation(libs.selenide)

    implementation(libs.hikaricp)
    implementation(libs.mariadb)
    implementation(libs.sqlite)
    implementation(libs.bundles.flyway)

    compileOnly(libs.jetbrainsAnnotations)

    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.junitEngine)

    testImplementation(libs.bundles.assertj)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.awaitility)
    testImplementation(libs.unirestMocks)
    testImplementation(libs.bundles.jsonUnit)
    testImplementation(libs.rerunnerJupiter)
}

sourceSets {
    create("schema") {
        java {
            compileClasspath += sourceSets.main.get().compileClasspath
            compileClasspath += sourceSets.main.get().output

            runtimeClasspath += sourceSets.main.get().compileClasspath
            runtimeClasspath += sourceSets.main.get().runtimeClasspath
            runtimeClasspath += sourceSets.main.get().output

            dependencies {
                implementation(libs.bundles.jsonschemaGenerator)
            }
        }
    }
}

tasks {
    processResources {
        expand(project.properties)
    }

    compileJava {
        val moduleName: String by project
        inputs.property("moduleName", moduleName)

        options.encoding = "UTF-8"
        options.isDeprecation = true
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    jar {
        manifest {
            attributes["Multi-Release"] = "true"
        }
    }

    test {
        useJUnitPlatform()
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("shaded")
        archiveVersion.set("")
    }
}

application {
    val moduleName: String by project
    val className: String by project

//    mainModule.set(moduleName)
    mainClass.set(className)
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

gitProperties {
    failOnNoGitDirectory = false
}
