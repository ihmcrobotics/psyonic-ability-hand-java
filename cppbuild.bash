#!/bin/bash
# This build script is designed to work on Linux and Windows. For Windows, run from a bash shell launched with launchBashWindows.bat

BASE_DIR=$(pwd)

mkdir cppbuild
cd cppbuild

# Clone the ability-hand-api repo, if it's not already here
if [ ! -d ability-hand-api ]; then
  git clone -b multi-hand-support https://github.com/ihmcrobotics/ability-hand-api.git ability-hand-api
fi

cd ability-hand-api/cpp

# Replace CMakeLists.txt with one that installs the API as a library
cp -f $BASE_DIR/patches/CMakeLists.txt .

# Build the API and install the library
mkdir build
cd build
cmake -Dinstall_dir=$BASE_DIR/cppbuild/install/ ..
cmake --build . --config Release --target install

### Java generation ####
cd $BASE_DIR/cppbuild
cp -r ../src/main/java/* .

JAVACPP_VERSION=1.5.11
if [ ! -f javacpp.jar ]; then
  curl -L https://github.com/bytedeco/javacpp/releases/download/$JAVACPP_VERSION/javacpp-platform-$JAVACPP_VERSION-bin.zip -o javacpp-platform-$JAVACPP_VERSION-bin.zip
  unzip -j javacpp-platform-$JAVACPP_VERSION-bin.zip
fi

java -cp "javacpp.jar" org.bytedeco.javacpp.tools.Builder us/ihmc/abilityhand/AbilityHandJavaAPIConfig.java
cp us/ihmc/abilityhand/*.java ../src/main/java/us/ihmc/abilityhand/
cp us/ihmc/abilityhand/global/*.java ../src/main/java/us/ihmc/abilityhand/global/

#### JNI compilation ####
java -cp "javacpp.jar" org.bytedeco.javacpp.tools.Builder us/ihmc/abilityhand/*.java us/ihmc/abilityhand/*.java -d javainstall

##### Copy shared libs to resources ####
# Linux
mkdir -p ../src/main/resources/abilityhand/native/linux-x86_64/
if [ -f "javainstall/libjniabilityhand.so" ]; then
  cp javainstall/libjniabilityhand.so ../src/main/resources/abilityhand/native/linux-x86_64/
fi
if [ -f "install/lib/libability_hand_api.so" ]; then
  cp install/lib/libability_hand_api.so ../src/main/resources/abilityhand/native/linux-x86_64/
fi
# Windows
mkdir -p ../src/main/resources/abilityhand/native/windows-x86_64/
if [ -f "javainstall/jniabilityhand.dll" ]; then
  cp javainstall/jniabilityhand.dll ../src/main/resources/abilityhand/native/windows-x86_64/
fi
if [ -f "install/lib/ability_hand_api.dll" ]; then
  cp install/lib/ability_hand_api.dll ../src/main/resources/abilityhand/native/windows-x86_64/
fi
