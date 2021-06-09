group = "guohao.simulator"

dependencies {
    implementation(project(":common"))
    implementation(files(System.getenv("JAVA_HOME") + "/lib/tools.jar"))
}

intellij {
    pluginName = "Simulator"
}
