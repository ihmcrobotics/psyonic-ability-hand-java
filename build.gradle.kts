plugins {
   id("java-library")
   id("maven-publish")
}

group = "us.ihmc"
version = "1.0.0-SNAPSHOT"

repositories {
   mavenCentral()
   maven { url = uri("https://jitpack.io") }
}

dependencies {
   implementation("com.github.weliem.blessed-bluez:blessed:0.61") {
      isTransitive = true
   } // Linux Bluetooth library
   implementation("org.jetbrains:annotations:24.0.0") {
      isTransitive = false
   }
}