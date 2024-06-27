package us.ihmc.abilityhand.example;

import us.ihmc.abilityhand.AbilityHandBLEManager;
import us.ihmc.abilityhand.AbilityHandIndividualFingerControlCommand;

import java.util.Random;

public class AbilityHandIndividualExample {
    private static final Random RANDOM = new Random();
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        String handAddress = "DE:76:4F:34:6F:E1";

        AbilityHandBLEManager bleManager = new AbilityHandBLEManager(new String[] {handAddress});

        bleManager.connect();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            running = false;
            bleManager.disconnect();
        }));
        while(running)
        {
            AbilityHandIndividualFingerControlCommand individualCommand = new AbilityHandIndividualFingerControlCommand();
            individualCommand.setIndexPeriod(0);
            individualCommand.setIndexPosition(RANDOM.nextInt(60));
            individualCommand.setMiddlePosition(RANDOM.nextInt(60));
            individualCommand.setRingPosition(RANDOM.nextInt(60));
            individualCommand.setPinkyPosition(RANDOM.nextInt(60));
            individualCommand.setThumbFlexorPosition(RANDOM.nextInt(60));
            individualCommand.setThumbRotatorPosition(-RANDOM.nextInt(60));
            bleManager.sendIndividualCommand(handAddress, individualCommand);
            Thread.sleep(50);
        }
    }
}
