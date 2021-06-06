plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    kotlin("jvm") version "1.4.20"
    java
}

repositories {
    mavenLocal()
    maven("/Users/guohao/Downloads/ideaIC-2020.1.1.zip")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
}

subprojects {
    group = "guohao.pluginDemo"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.intellij")

    java.sourceCompatibility = JavaVersion.VERSION_11

    val developmentOnly by configurations.creating

    configurations {
        runtimeClasspath {
            extendsFrom(developmentOnly)
        }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    intellij {
        version = "2020.3.2"
        setPlugins("com.intellij.modules.platform")
        setPlugins("com.intellij.modules.lang")
        setPlugins("com.intellij.java")
    }

    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/jcenter")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        mavenCentral()
    }

    dependencies {
        implementation("com.google.guava", "guava", "30.0-jre")
        implementation("org.apache.commons", "commons-collections4", "4.1")
        implementation(fileTree(mapOf("dir" to "libs", "include" to "tools.jar")))

        implementation("org.projectlombok", "lombok", "1.18.2")
        annotationProcessor("org.projectlombok", "lombok", "1.18.2")
        testImplementation("org.projectlombok", "lombok", "1.18.2")
    }
}

tasks.forEach {
    it.enabled = false
}