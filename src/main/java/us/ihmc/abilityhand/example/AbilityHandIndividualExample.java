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
         try
         {
            bleManager.disconnect();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
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
            individualCommand.setIndexPosition((float) Math.abs(65 * Math.sin(Math.toRadians(i))));
            individualCommand.setMiddlePosition((float) Math.abs(65 * Math.cos(Math.toRadians(i))));
            individualCommand.setRingPosition((float) Math.abs(65 * Math.sin(Math.toRadians(i))));
            individualCommand.setPinkyPosition((float) Math.abs(65 * Math.cos(Math.toRadians(i))));
            bleManager.sendIndividualCommand(handAddress, individualCommand);
            Thread.sleep(10);
         }
         Thread.sleep(250);
      }
   }
}
