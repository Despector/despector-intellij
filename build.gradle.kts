import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            name = "intellij-plugin-service"
            setUrl("https://dl.bintray.com/jetbrains/intellij-plugin-service")
        }
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.1"
    idea
    id("org.jetbrains.intellij") version "0.2.5"
}

val CI = System.getenv("CI") != null

val ideaVersion: String by extra
val javaVersion: String by extra
val downloadIdeaSources: String by extra

val runIde: JavaExec by tasks
val compileKotlin by tasks

repositories {
    mavenCentral()
    maven {
        name = "despector"
        setUrl("http://repo.voxelgenesis.com/artifactory/decompiler")
    }
}

dependencies {
    compile(kotlinModule("stdlib-jre8")) {
        // JetBrains annotations are already bundled with IntelliJ IDEA
        exclude(group = "org.jetbrains", module = "annotations")
    }

    // Add an additional dependency on kotlin-runtime. It is essentially useless
    // (since kotlin-runtime is a transitive dependency of kotlin-stdlib-jre8)
    // but without kotlin-stdlib or kotlin-runtime on the classpath,
    // gradle-intellij-plugin will add IntelliJ IDEA's Kotlin version to the
    // dependencies which conflicts with our newer version.
    compile(kotlinModule("runtime")) {
        isTransitive = false
    }

    compile("org.spongepowered:despector:0.1.0-SNAPSHOT")
}

intellij {
    // IntelliJ IDEA dependency
    version = ideaVersion

    pluginName = "Despector Integration"
    updateSinceUntilBuild = false

    setPlugins("java-decompiler", "Kotlin")

    downloadSources = !CI && downloadIdeaSources.toBoolean()

    sandboxDirectory = project.rootDir.canonicalPath + "/.sandbox"
}

java {
    setSourceCompatibility(javaVersion)
    setTargetCompatibility(javaVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

idea {
    module {
        excludeDirs.add(file(intellij().sandboxDirectory))
    }
}

runIde {
    findProperty("intellijJre")?.let(this::setExecutable)

    System.getProperty("debug")?.let {
        systemProperty("idea.ProcessCanceledException", "disabled")
        systemProperty("idea.debug.mode", "true")
    }
}

inline operator fun <T : Task> T.invoke(a: T.() -> Unit): T = apply(a)
fun KotlinDependencyHandler.kotlinModule(module: String) = kotlinModule(module, "1.1.1") as String
