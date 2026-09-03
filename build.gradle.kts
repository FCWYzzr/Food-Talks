import org.slf4j.event.Level

val minecraft_version           = project.findProperty("minecraft_version          ".trim()) as String 
val minecraft_version_range     = project.findProperty("minecraft_version_range    ".trim()) as String 
val neo_version                 = project.findProperty("neo_version                ".trim()) as String 
val neo_version_range           = project.findProperty("neo_version_range          ".trim()) as String 
val loader_version_range        = project.findProperty("loader_version_range       ".trim()) as String 
val mod_id                      = project.findProperty("mod_id                     ".trim()) as String 
val mod_name                    = project.findProperty("mod_name                   ".trim()) as String 
val mod_license                 = project.findProperty("mod_license                ".trim()) as String 
val mod_version                 = project.findProperty("mod_version                ".trim()) as String 
val mod_authors                 = project.findProperty("mod_authors                ".trim()) as String 
val mod_description             = project.findProperty("mod_description            ".trim()) as String 
val mod_group_id                = project.findProperty("mod_group_id               ".trim()) as String 
val parchment_mappings_version  = project.findProperty("parchment_mappings_version ".trim()) as String 
val parchment_minecraft_version = project.findProperty("parchment_minecraft_version".trim()) as String 
val kff_version                 = project.findProperty("kff_version                ".trim()) as String
val jei_version                 = project.findProperty("jei_version                ".trim()) as String


plugins {
    id("java-library")
    id("eclipse")
    id("idea")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.146"
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.0"
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

version = mod_version
group = mod_group_id

base {
    archivesName = mod_id
}

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    // Specify the version of NeoForge to use.
    version = neo_version

    parchment {
        mappingsVersion = parchment_mappings_version
        minecraftVersion = parchment_minecraft_version
    }

    // This line is optional. Access Transformers are automatically detected
    // accessTransformers = project.files('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        create("client") {
            client()

            // Comma-separated list of namespaces to load game-tests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        // This run config launches GameTestServer and runs all registered game-tests, then exits.
        // By default, the server will crash when no game-tests are provided.
        // The game-test system is also enabled by default for other run configs under the /test command.
        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            data()

            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file('run-data')

            // Specify the mod id for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", mod_id, "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }

        // applies to all the run configs above
        configureEach {
            // Recommended logging data for a user-dev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            logLevel = Level.DEBUG
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // mostly optional in a single mod project
        // but multi mod projects should define one per mod
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Include resources generated by data generators.
sourceSets.main.get().resources.srcDir("src/generated/resources")

// Sets up a dependency configuration called 'localRuntime'.
// This configuration should be used instead of 'runtimeOnly' to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
configurations {
    register("localRuntime") {
        extendsFrom(configurations.runtimeClasspath.get())
    }
}

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

dependencies {
    // implementation "net.neoforged:neoforge:${neo_version}"
    // annotationProcessor 'org.spongepowered:mixin:0.8.5:processor'

    compileOnly("thedarkcolour:kotlinforforge-neoforge:${kff_version}")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
            "minecraft_version" to minecraft_version,
            "minecraft_version_range" to minecraft_version_range,
            "kff_version_range" to "[${kff_version},)",
            "jei_version_range" to "[${jei_version},)",
            "neo_version" to neo_version,
            "neo_version_range" to neo_version_range,
            "loader_version_range" to loader_version_range,
            "mod_id" to mod_id,
            "mod_name" to mod_name,
            "mod_license" to mod_license,
            "mod_version" to mod_version,
            "mod_authors" to mod_authors,
            "mod_description" to mod_description
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

// Example configuration to allow publishing using the maven-publish plugin
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}