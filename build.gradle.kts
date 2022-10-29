plugins {
    idea
    alias(libs.plugins.names)
}

group = "fr.rakambda"
description = "Channel points miner"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
}
