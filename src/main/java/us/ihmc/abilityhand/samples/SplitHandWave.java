package us.ihmc.abilityhand.samples;

import us.ihmc.abilityhand.AHWrapper;
import us.ihmc.abilityhand.FloatArray6;
import us.ihmc.abilityhand.global.abilityhand;
import us.ihmc.abilityhand.library.AbilityHandAPINativeLibrary;

public class SplitHandWave
{
   public static void main(String[] args)
   {
      // Library must be loaded before use
      boolean loaded = AbilityHandAPINativeLibrary.load();
      assert loaded;

      // Initialize wrapper and connect
      AHWrapper wrapper = new AHWrapper((byte) 0x50, 460800);
      wrapper.connect("");

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

         // Write to the hand
         wrapper.write(command, abilityhand.POSITION, (byte) 0);

         // Read the hand's reply
         int result = -1;
         for (int j = 0; j < 10000 && result < 0; ++j)
         {
            result = wrapper.read((byte) 0);
         }

         if (result > 0)
         {
            System.out.println("Hand position: " + wrapper.hand().pos().get(0) + " " + wrapper.hand().pos().get(1) + " " + wrapper.hand().pos().get(2) + " "
                               + wrapper.hand().pos().get(3) + " " + wrapper.hand().pos().get(4) + " " + wrapper.hand().pos().get(5));
         }
         else if (result == 0)
         {
            System.out.println("Checksum failed");
         }
      }

      wrapper.close();
   }
}
