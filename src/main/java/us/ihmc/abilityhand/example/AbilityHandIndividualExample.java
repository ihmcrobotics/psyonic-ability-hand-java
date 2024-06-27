package us.ihmc.abilityhand.example;

import us.ihmc.abilityhand.AbilityHandBLEManager;
import us.ihmc.abilityhand.AbilityHandIndividualFingerControlCommand;

public class AbilityHandIndividualExample {
    public static void main(String[] args) throws InterruptedException {
        String handAddress = "DE:76:4F:34:6F:E1";

        AbilityHandBLEManager bleManager = new AbilityHandBLEManager(new String[] {handAddress});

        bleManager.connect();

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            bleManager.disconnect();
        }));
        AbilityHandIndividualFingerControlCommand individualCommand = new AbilityHandIndividualFingerControlCommand();
        bleManager.sendIndividualCommand(handAddress, individualCommand);
        Thread.sleep(50);
    }
}
