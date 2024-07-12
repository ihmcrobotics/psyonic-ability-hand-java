package us.ihmc.abilityhand.ble;

import us.ihmc.tools.nativelibraries.NativeLibraryDescription;
import us.ihmc.tools.nativelibraries.NativeLibraryWithDependencies;

public class SimpleBLENativeLibrary implements NativeLibraryDescription
{
   @Override
   public String getPackage(OperatingSystem os, Architecture arch)
   {
      String archPackage = "";
      if (arch == Architecture.x64)
      {
         archPackage = switch (os)
         {
            case LINUX64 -> "linux-x86_64";
            case WIN64 -> "windows-x86_64";
            case MACOSX64 -> throw new RuntimeException("Unsupported platform");
         };
      }
      else if (arch == Architecture.arm64)
      {
         throw new RuntimeException("Unsupported platform");
      }

      return "SimpleBLE.native." + archPackage;
   }

   @Override
   public NativeLibraryWithDependencies getLibraryWithDependencies(OperatingSystem os, Architecture arch)
   {
      switch (os)
      {
         case LINUX64 ->
         {
            return NativeLibraryWithDependencies.fromFilename("libsimpleble-c.so", "libsimpleble.so", "libsimpleble.so.0", "libsimpleble.so.0.7.3");
         }
         case WIN64 ->
         {
            return NativeLibraryWithDependencies.fromFilename("simpleble-c.dll", "simpleble.dll");
         }
         case MACOSX64 -> throw new RuntimeException("Unsupported platform");
      }
      return null;
   }
}
