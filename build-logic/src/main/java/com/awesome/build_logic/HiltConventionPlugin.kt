package com.awesome.build_logic

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("com.google.dagger.hilt.android")
            plugins.apply("kotlin-kapt")

            dependencies {
                add("implementation", "com.google.dagger:hilt-android:2.51")
                add("kapt", "com.google.dagger:hilt-compiler:2.51")

                // Hilt testing setup (optional)
                add("androidTestImplementation", "com.google.dagger:hilt-android-testing:2.51")
                add("kaptAndroidTest", "com.google.dagger:hilt-compiler:2.51")
            }
        }
    }
}