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
      float old = 0.0f;

      // Initialize wrapper and connect
      AHWrapper wrapper = new AHWrapper((byte) 0x50, 1000000);
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
         wrapper.write_once(command, abilityhand.POSITION, (byte) 0);
         int j = 0;
         boolean read = false;
         try {
            Thread.sleep(1);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
         while(j < 30 && !read)
         {
            read = wrapper.read_once((byte) 0);
            ++j;
         }
         if(old != wrapper.hand().pos().get(0))
         {
            System.out.println(
                  "Hand position: " + wrapper.hand().pos().get(0) + " " + wrapper.hand().pos().get(1) + " " + wrapper.hand().pos().get(2) + " " +
                  wrapper.hand().pos().get(3) + " " + wrapper.hand().pos().get(4) + " " + wrapper.hand().pos().get(5));
            old = wrapper.hand().pos().get(0);
         }
      }

   }
}
