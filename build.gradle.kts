plugins {
    java
    kotlin("jvm") version "2.+"
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.vanilla.gradle)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        name = "paulemReleases"
        url = uri("https://maven.paulem.net/releases")
    }
    maven {
        name = "radRepoPublic"
        url = uri("https://maven.rad.vg/public")
    }
    maven("https://maven.paulem.net/releases/")
    maven("https://repo.viaversion.com")
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    implementation(libs.glowingentities)
    implementation(libs.arcana)
    implementation(libs.packed)

    implementation(libs.mcbrawls.spigot)
    implementation(libs.mcbrawls.api)
    implementation(libs.mcbrawls.http)
    implementation(libs.mcbrawls.jetty)
    implementation(libs.mcbrawls.javalin) {
        isTransitive = false
    }
    implementation(libs.javalin)

    compileOnly(libs.paper.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

artifacts.archives(tasks.shadowJar)

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveClassifier.set("")
        exclude("META-INF/**")

        relocate("fr.skytasul.glowingentities", "net.paulem.vanillahammers.glowingentities")
        relocate("ovh.paulem.arcana", "net.paulem.vanillahammers.arcana")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms1G", "-Xmx1G", "-Dcom.mojang.eula.agree=true", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

fun getProp(key: String, default: String): String {
    val prop = providers.gradleProperty(key);
    return prop.getOrElse(default)
}

minecraft {
    injectRepositories(false)
    version("26.1.2")
    runs {
        client {
            workingDirectory(file("run/client"))
            args("--quickPlayMultiplayer", "127.0.0.1:25565")
            parameterTokens {
                put("auth_player_name", getProp("mc.username", "Paulem79"))
                put("auth_uuid",        getProp("mc.uuid", "00000000-0000-0000-0000-000000000000"))
                put("auth_access_token", getProp("mc.access.token", "0"))
                put("user_type", "msa")
            }
        }
    }
}