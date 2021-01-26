description = "generator"
group = "guohao"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
}

intellij {
    pluginName = "Generator"
    version = "2020.3.1"
    setPlugins("com.intellij.modules.platform")
    setPlugins("com.intellij.modules.lang")
    setPlugins("com.intellij.java")
}