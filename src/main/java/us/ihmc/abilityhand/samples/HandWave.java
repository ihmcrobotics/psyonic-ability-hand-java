package us.ihmc.abilityhand.samples;

import us.ihmc.abilityhand.AHWrapper;
import us.ihmc.abilityhand.FloatArray6;
import us.ihmc.abilityhand.global.abilityhand;
import us.ihmc.abilityhand.library.AbilityHandAPINativeLibrary;

public class HandWave
{
   public static void main(String[] args)
   {
      // Library must be loaded before use
      boolean loaded = AbilityHandAPINativeLibrary.load();
      assert loaded;

      // Initialize wrapper and connect
      AHWrapper wrapper = new AHWrapper((byte) 0x50, 460800);
      wrapper.connect();

      // Initialize the command
      FloatArray6 command = new FloatArray6();
      for (int i = 0; i < 5; ++i)
         command.put(i, 30.0f);
      command.put(5, -30.0f);

      for (int i = 0; i < 100000; ++i)
      {
         // Calculate hand wave
         double timeSeconds = 1E-9 * System.nanoTime();
         for (int j = 0; j < command.size(); ++j)
         {
            double ft = timeSeconds * 3.0 + j * (2.0 * Math.PI / 12.0);
            command.put(j, (float) (0.5 * Math.sin(ft) + 0.5) * 45.0f + 15.0f);
         }
         command.put(5, -command.get(5));

         // Write to the hand (also reads)
         wrapper.read_write_once(command, abilityhand.POSITION, (byte) 0);
      }

      wrapper.close();
   }
}
