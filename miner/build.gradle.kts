import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    application
    jacoco
    alias(libs.plugins.shadow)
    alias(libs.plugins.jib)
    alias(libs.plugins.gitProperties)
    alias(libs.plugins.testLogger)
}

dependencies {
    implementation(platform(libs.jacksonBom))
    implementation(platform(libs.unirestBom))
    implementation(platform(libs.log4j2Bom))

    implementation(libs.slf4j)
    implementation(libs.bundles.log4j2)
    implementation(libs.log4jEcs)

    implementation(libs.bundles.unirest)
    implementation(libs.picocli)
    implementation(libs.bundles.jackson)
    implementation(libs.httpclient)
    implementation(libs.lang3)
    implementation(libs.commonsText)
    implementation(libs.websocket)
    implementation(libs.kittehIrc)
    implementation(libs.bundles.selenide)

    implementation(libs.hikaricp)
    implementation(libs.mariadb)
    implementation(libs.sqlite)
    implementation(libs.mysql)
    implementation(libs.postgresql)
    implementation(libs.bundles.flyway)
    implementation(libs.jSpecify)

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)

    implementation(platform(libs.junitBom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.junitPlatformLauncher)

    testImplementation(libs.bundles.assertj)
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.awaitility)
    testImplementation(libs.unirestMocks)
    testImplementation(libs.bundles.jsonUnit)
    testImplementation(libs.rerunnerJupiter)

    testCompileOnly(libs.lombok)

    testAnnotationProcessor(libs.lombok)
}

sourceSets {
    create("schema") {
        java {
            compileClasspath += sourceSets.main.get().compileClasspath
            compileClasspath += sourceSets.main.get().output

            annotationProcessorPath += sourceSets.main.get().annotationProcessorPath

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

        exclude("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")
        mergeServiceFiles()

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        transform<Log4j2PluginsCacheFileTransformer>()
    }
}

application {
    val moduleName: String by project
    val className: String by project

//    mainModule.set(moduleName)
    mainClass.set(className)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jacoco {
    toolVersion = libs.versions.jacocoVersion.get()
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD_PARALLEL
    showPassed = false
    showPassedStandardStreams = false
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
    }
}

gitProperties {
    failOnNoGitDirectory = false
}
