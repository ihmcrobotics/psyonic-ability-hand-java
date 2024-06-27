package us.ihmc.abilityhand.example;

import us.ihmc.abilityhand.AbilityHandBLEManager;
import us.ihmc.abilityhand.AbilityHandIndividualFingerControlCommand;

public class AbilityHandIndividualExample
{
   private static volatile boolean running = true;

   public static void main(String[] args) throws InterruptedException
   {
      String handAddress = "DE:76:4F:34:6F:E1";

      AbilityHandBLEManager bleManager = new AbilityHandBLEManager(new String[] {handAddress});

      bleManager.connect();

      Runtime.getRuntime().addShutdownHook(new Thread(() ->
      {
         running = false;
         bleManager.disconnect();
      }));

      AbilityHandIndividualFingerControlCommand individualCommand = new AbilityHandIndividualFingerControlCommand();
      individualCommand.setIndexPosition(0);
      individualCommand.setMiddlePosition(0);
      individualCommand.setRingPosition(0);
      individualCommand.setPinkyPosition(0);
      bleManager.sendIndividualCommand(handAddress, individualCommand);
      individualCommand.setIndexPeriod(0.0F);
      individualCommand.setMiddlePeriod(0.0F);
      individualCommand.setRingPeriod(0.0F);
      individualCommand.setPinkyPeriod(0.0F);

      while (running)
      {
         for (int i = 0; i < 90; i++)
         {
            individualCommand.setIndexPosition((float) Math.abs(60 * Math.sin(i)));
            individualCommand.setMiddlePosition((float) Math.abs(60 * Math.cos(i)));
            individualCommand.setRingPosition((float) Math.abs(60 * Math.sin(i)));
            individualCommand.setPinkyPosition((float) Math.abs(60 * Math.cos(i)));
            bleManager.sendIndividualCommand(handAddress, individualCommand);
            Thread.sleep(100);
         }
         Thread.sleep(250);
      }
   }
}
