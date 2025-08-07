plugins {
   id("us.ihmc.ihmc-build")
}

ihmc {
   group = "us.ihmc"
   version = "1.1.2"
   vcsUrl = "https://github.com/ihmcrobotics/psyonic-ability-hand-java"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("us.ihmc:javacpp:1.5.11-ihmc-2")
   api("us.ihmc:ihmc-native-library-loader:2.0.4")
}
