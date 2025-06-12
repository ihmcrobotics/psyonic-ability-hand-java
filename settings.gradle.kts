pluginManagement {
   plugins {
      id("us.ihmc.ihmc-build") version "1.1.1"
   }
}

buildscript {
   repositories {
      maven { url = uri("https://plugins.gradle.org/m2/") }
      mavenLocal()
   }
   dependencies {
      classpath("us.ihmc:ihmc-build:1.1.1")
   }
}
