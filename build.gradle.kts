plugins {
    java
    kotlin("jvm") version "2.+"
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
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
    maven("https://maven.mcbrawls.net/releases/")
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