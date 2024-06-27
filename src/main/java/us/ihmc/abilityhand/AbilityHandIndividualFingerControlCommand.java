package us.ihmc.abilityhand;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// 4.10 BLE Individual Finger Control ('M')
/*
   Finger order is the same as in all other instances of ordered per-finger data
      (index,middle,ring,pinky,thumb flexor, thumb rotator).
 */
public class AbilityHandIndividualFingerControlCommand {
   private static final byte INDIVIDUAL_FINGER_CONTROL_COMMAND_ASCII_PREFIX = 'M';

   //Position is set as an angle of the finger with respect to the palm
   //Period is how fast the hand gets to that angle, the lower the period the faster
   private static int indexPosition = 5;
   private int indexPeriod = 0;
   private int middlePosition = 5;
   private int middlePeriod = 0;
   private static int ringPosition = 5;
   private int ringPeriod = 0;
   private static int pinkyPosition = 5;
   private int pinkyPeriod = 0;
   private int thumbFlexorPosition = 5;
   private int thumbFlexorPeriod = 0;
   private int thumbRotatorPosition = 5;
   private int thumbRotatorPeriod = 0;


   // TODO:

   public byte[] getAsciiSequence() {
      ByteBuffer buffer = ByteBuffer.allocate(25);
      buffer.order(ByteOrder.LITTLE_ENDIAN);

      buffer.put(INDIVIDUAL_FINGER_CONTROL_COMMAND_ASCII_PREFIX);

      buffer.putShort((short) ((indexPosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((indexPeriod * 0xFFFF) / 300.0));

      buffer.putShort((short) ((middlePosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((middlePeriod * 0xFFFF) / 300.0));

      buffer.putShort((short) ((ringPosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((ringPeriod * 0xFFFF) / 300.0));

      buffer.putShort((short) ((pinkyPosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((pinkyPeriod * 0xFFFF) / 300.0));

      buffer.putShort((short) ((thumbFlexorPosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((thumbFlexorPeriod * 0xFFFF) / 300.0));

      buffer.putShort((short) ((thumbRotatorPosition * 0x7FFF) / 150.0));
      buffer.putShort((short) ((thumbRotatorPeriod * 0xFFFF) / 300.0));

      return buffer.array();
   }
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
