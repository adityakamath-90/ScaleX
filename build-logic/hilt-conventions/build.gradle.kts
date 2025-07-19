import jdk.tools.jlink.resources.plugins

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("hilt-conventions") {
            id = "hilt-conventions"
            implementationClass = "HiltConventionPlugin"
        }
    }
}