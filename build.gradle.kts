plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
}

group = "guohao.generator"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.intellij")

    val developmentOnly by configurations.creating

    configurations {
        runtimeClasspath {
            extendsFrom(developmentOnly)
        }
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
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
        implementation("com.google.guava","guava", "30.0-jre")
        implementation("org.apache.commons","commons-collections4", "4.1")
        implementation(fileTree(mapOf("dir" to "libs", "include" to "tools.jar")))

        implementation("org.projectlombok","lombok","1.18.2")
        annotationProcessor( "org.projectlombok","lombok","1.18.2")
        testImplementation("org.projectlombok","lombok","1.18.2")
    }
}

tasks.forEach {
    it.enabled = false
}