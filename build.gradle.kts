plugins {
    idea
    alias(libs.plugins.names)
}

group = "fr.raksrinana"
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
