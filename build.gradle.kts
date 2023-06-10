plugins {
    idea
    alias(libs.plugins.names)
    alias(libs.plugins.gitVersion)
}

group = "fr.rakambda"
description = "Channel points miner"

allprojects {
    repositories {
        mavenCentral()
    }

    if (version.equals("unspecified")) {
        version = try {
            val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
            val details = versionDetails()

            if (details.commitDistance > 0) {
                "${details.lastTag}-${details.commitDistance}"
            } else {
                details.lastTag
            }
        } catch (e: Exception) {
            "0.0.1-dev"
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}
