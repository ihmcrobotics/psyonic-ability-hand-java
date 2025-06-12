package us.ihmc.abilityhand.library;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription;
import us.ihmc.tools.nativelibraries.NativeLibraryLoader;
import us.ihmc.tools.nativelibraries.NativeLibraryWithDependencies;

public class AbilityHandAPINativeLibrary implements NativeLibraryDescription
{
   @Override
   public String getPackage(OperatingSystem os, Architecture arch)
   {
      String archPackage = switch (arch)
      {
         case x64 -> switch (os)
         {
            case WIN64 -> "windows-x86_64";
            case LINUX64 -> "linux-x86_64";
            case MACOSX64 -> throw new RuntimeException("Unsupported platform");
         };
         case arm64 -> throw new RuntimeException("Unsupported platform");
      };

      return "abilityhand.native." + archPackage;
   }

   @Override
   public NativeLibraryWithDependencies getLibraryWithDependencies(OperatingSystem os, Architecture arch)
   {
      return switch (os)
      {
         case WIN64 -> NativeLibraryWithDependencies.fromFilename("jniabilityhand.dll", "ability_hand_api.dll");
         case LINUX64 -> NativeLibraryWithDependencies.fromFilename("libjniabilityhand.so", "libability_hand_api.so");
         case MACOSX64 -> throw new RuntimeException("Unsupported platform");
      };
   }

   private static boolean loaded = false;

   public static boolean load()
   {
      if (!loaded)
      {
         AbilityHandAPINativeLibrary lib = new AbilityHandAPINativeLibrary();
         loaded = NativeLibraryLoader.loadLibrary(lib);
      }

      return loaded;
   }
}
