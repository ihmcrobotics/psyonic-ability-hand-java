#!/bin/bash
# This build script is designed to work on Linux and Windows. For Windows, run from a bash shell launched with launchBashWindows.bat

pushd .
mkdir cppbuild
cd cppbuild

if [ ! -d ability-hand-api ]; then
  git clone https://github.com/psyonicinc/ability-hand-api.git ability-hand-api
fi

cd ability-hand-api/cpp
mkdir build
cd build

if [ "$(uname)" == "Linux" ]; then
  # TODO
  echo "TODO"
else # Winows
  cmake .. -Wno-dev
fi

popd

### Java generation ####
cd cppbuild
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
# TODO
