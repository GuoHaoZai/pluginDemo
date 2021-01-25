group = "guohao.simulator"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
}

intellij {
    pluginName = "Simulator"
    version = "2020.3.1"
    setPlugins("com.intellij.modules.platform")
    setPlugins("com.intellij.modules.lang")
    setPlugins("com.intellij.java")
}
