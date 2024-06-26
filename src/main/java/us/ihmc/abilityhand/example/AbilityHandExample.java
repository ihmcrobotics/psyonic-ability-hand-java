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
      String handAddress = "DE:76:4F:34:6F:E1";

      AbilityHandBLEManager bleManager = new AbilityHandBLEManager(new String[] {handAddress});

      bleManager.connect();

      Runtime.getRuntime().addShutdownHook(new Thread(() ->
      {
         running = false;
         bleManager.disconnect();
      }));

      while (running)
      {
          AbilityHandLegacyGripCommand gripCommand = getRandomGripCommand();

         System.out.println("Sending " + gripCommand);

         bleManager.sendLegacyGripCommand(handAddress, gripCommand);

         Thread.sleep(5000);
      }
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
