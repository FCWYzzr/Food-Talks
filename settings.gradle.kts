pluginManagement {
    repositories {
        mavenLocal()

        maven {
            name = "Kotlin for Forge"
            url = uri("https://thedarkcolour.github.io/KotlinForForge/")
            content {
                includeGroup("thedarkcolour")
            }
        }

        maven {
            url = uri("https://lss233.littleservice.cn/repositories/minecraft")
        }

        maven {
            // location of the maven that hosts JEI files
            name = "Progwml6 maven"
            url = uri("https://dvs1.progwml6.com/files/maven/")
        }

        maven {
            // location of a maven mirror for JEI files, as a fallback
            name = "ModMaven"
            url = uri("https://modmaven.dev")
        }

        maven {
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}