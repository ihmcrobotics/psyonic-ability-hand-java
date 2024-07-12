package us.ihmc.abilityhand.example;

import us.ihmc.abilityhand.AbilityHandBLEManager;
import us.ihmc.abilityhand.AbilityHandLegacyGripCommand;
import us.ihmc.abilityhand.AbilityHandLegacyGripCommand.LegacyGripSpeed;
import us.ihmc.abilityhand.AbilityHandLegacyGripCommand.LegacyGripType;

import java.util.Random;

public class AbilityHandExample
{
   private static final Random RANDOM = new Random();
   private static volatile boolean running = true;

   public static void main(String[] args) throws InterruptedException
   {
      String[] handAddresses = new String[] {"DE:76:4F:34:6F:E1", "F9:C8:0F:A7:A4:D5"};

      AbilityHandBLEManager bleManager = new AbilityHandBLEManager(handAddresses);

      int numberOfHandsConnected = bleManager.connect();

      if (numberOfHandsConnected == 0)
      {
         System.out.println("No hands found");
      }

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

      while (running && numberOfHandsConnected > 0)
      {
         AbilityHandLegacyGripCommand gripCommand = getRandomGripCommand();

         System.out.println("Sending " + gripCommand);

         for (String handAddress : handAddresses)
         {
            bleManager.sendLegacyGripCommand(handAddress, gripCommand);
            Thread.sleep(100);
         }

         Thread.sleep(5000);
      }

      System.out.println("Exiting");
   }

   public static AbilityHandLegacyGripCommand getRandomGripCommand()
   {
      LegacyGripType[] gripTypes = LegacyGripType.values();
      LegacyGripType gripType = gripTypes[RANDOM.nextInt(gripTypes.length)];

      LegacyGripSpeed[] gripSpeeds = LegacyGripSpeed.values();
      LegacyGripSpeed gripSpeed = gripSpeeds[RANDOM.nextInt(gripSpeeds.length)];

      return new AbilityHandLegacyGripCommand(gripType, gripSpeed);
   }
}
