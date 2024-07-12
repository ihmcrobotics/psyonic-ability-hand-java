plugins {
   id("java-library")
   id("maven-publish")
}

group = "us.ihmc"
version = "1.0.1"

repositories {
   mavenCentral()
   maven { url = uri("https://jitpack.io") }
}

publishing {
   publications {
      create<MavenPublication>("mavenJava") {
         from(components["java"])

         groupId = project.group.toString()
         artifactId = "psyonic-ability-hand-java"
         version = project.version.toString()
      }
   }

   repositories {
      maven {
         val releasesRepo = uri("https://s01.oss.sonatype.org/content/repositories/releases")
         val snapshotsRepo = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
         url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepo else releasesRepo

         credentials {
            username = project.findProperty("publishUsername").toString()
            password = project.findProperty("publishPassword").toString()
         }
      }
   }
}

dependencies {
   implementation("net.java.dev.jna:jna:5.14.0") {
      isTransitive = true
   }
   implementation("net.java.dev.jna:jna-platform:5.14.0") {
      isTransitive = true
   }
   api("us.ihmc:ihmc-native-library-loader:2.0.2") {
      isTransitive = true
   }
}