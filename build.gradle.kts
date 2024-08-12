buildscript {
    dependencies {
        classpath(libs.google.services)

    }
    repositories {
        google()
        jcenter()

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false

}



