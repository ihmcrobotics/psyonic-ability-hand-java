package us.ihmc.abilityhand;

// 4.10 BLE Individual Finger Control ('M')
/*
   Finger order is the same as in all other instances of ordered per-finger data
      (index,middle,ring,pinky,thumb flexor, thumb rotator).
 */
public class AbilityHandIndividualFingerControlCommand
{
   private static final byte INDIVIDUAL_FINGER_CONTROL_COMMAND_ASCII_PREFIX = 'M';

   private int indexPosition;
   private int indexPeriod;

   // TODO:

   public byte[] getAsciiSequence()
   {
      return null;
   }
}
