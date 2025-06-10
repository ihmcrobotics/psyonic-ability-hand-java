package us.ihmc.abilityhand;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
      @Platform(
            include = {"wrapper.h", "hand.h"},
            includepath = "C:/Users/tbialek/Projects/psyonic-ability-hand-java/cppbuild/ability-hand-api/cpp/ah_wrapper/include"
      )
},
      target = "us.ihmc.abilityhand",
      global = "us.ihmc.abilityhand.global.abilityhand"
)

public class AbilityHandJavaAPIConfig implements InfoMapper
{

      @Override
      public void map(InfoMap infoMap)
      {

      }
}
