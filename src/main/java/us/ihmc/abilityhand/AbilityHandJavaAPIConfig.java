package us.ihmc.abilityhand;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.Info;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(value = {
      @Platform(
            include = {"wrapper.h", "hand.h"},
            includepath = "install/include",
            link = "ability_hand_api",
            linkpath = "install/lib",
            preload = "jniabilityhand"
      ),
      @Platform(
            value = "linux",
            define = "PLATFORM_LINUX"
      ),
      @Platform(
            value = "windows",
            define = "PLATFORM_WINDOWS"
      )},
      target = "us.ihmc.abilityhand",
      global = "us.ihmc.abilityhand.global.abilityhand"
)

public class AbilityHandJavaAPIConfig implements InfoMapper
{
   @Override
   public void map(InfoMap infoMap)
   {
      infoMap.put(new Info("std::array<float,6>").pointerTypes("FloatArray6").define())
             .put(new Info("std::array<uint16_t,30>").pointerTypes("UInt16Array30").define())
             // Hand field of AHWrapper is read-only. Code adapted from here:
             // https://github.com/bytedeco/javacpp/wiki/Mapping-Recipes#mapping-a-declaration-to-custom-code
             .put(new Info("AHWrapper::hand").javaText("public native @MemberGetter @Const @ByRef Hand hand();"))
             .put(new Info("AHSerial").skip());
   }
}
