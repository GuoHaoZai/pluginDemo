plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
}

group = "guohao.code.generator"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("org.projectlombok","lombok","1.18.2")
    implementation("com.google.guava","guava", "30.0-jre")
    implementation("org.apache.commons","commons-collections4", "4.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to "tools.jar")))

    annotationProcessor( "org.projectlombok","lombok","1.18.2")

    testImplementation("org.projectlombok","lombok","1.18.2")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    pluginName = "myGenerator"
    version = "2020.3.1"
    setPlugins("com.intellij.modules.platform")
    setPlugins("com.intellij.modules.lang")
    setPlugins("com.intellij.java")
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
    )
}

tasks {
    runIde {
        jbrVersion("jbr-11_0_7b765.65")
    }
}