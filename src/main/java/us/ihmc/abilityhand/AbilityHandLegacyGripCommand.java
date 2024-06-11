package us.ihmc.abilityhand;

public class AbilityHandLegacyGripCommand
{
   private static final byte LEGACY_GRIP_COMMAND_ASCII_PREFIX = 'G';

   private LegacyGripType legacyGripType;
   private LegacyGripSpeed legacyGripSpeed;

   public AbilityHandLegacyGripCommand(LegacyGripType legacyGripType, LegacyGripSpeed legacyGripSpeed)
   {
      this.legacyGripType = legacyGripType;
      this.legacyGripSpeed = legacyGripSpeed;
   }

   public LegacyGripType getGripType()
   {
      return legacyGripType;
   }

   public void setGripType(LegacyGripType legacyGripType)
   {
      this.legacyGripType = legacyGripType;
   }

   public LegacyGripSpeed getGripSpeed()
   {
      return legacyGripSpeed;
   }

   public void setGripSpeed(LegacyGripSpeed legacyGripSpeed)
   {
      this.legacyGripSpeed = legacyGripSpeed;
   }

   public byte[] getAsciiSequence()
   {
      byte asciiGripSpeed = 0;

      switch (legacyGripSpeed)
      {
         case REALLY_SLOW -> asciiGripSpeed = '2';
         case SLOW -> asciiGripSpeed = 'A';
         case MEDIUM -> asciiGripSpeed = 'W';
         case MEDIUM_FAST -> asciiGripSpeed = 'k';
         case FAST -> asciiGripSpeed = '}';
      }

      assert asciiGripSpeed != 0;

      byte asciiGripIndex = legacyGripType.getAsciiGripIndex();
      return new byte[] {LEGACY_GRIP_COMMAND_ASCII_PREFIX, asciiGripIndex, asciiGripSpeed};
   }

   /**
    * Not protocol, these are just some reasonable presets
    */
   public enum LegacyGripSpeed
   {
      REALLY_SLOW, SLOW, MEDIUM, MEDIUM_FAST, FAST
   }

   public enum LegacyGripType
   {
      OPEN_HAND('0'),
      POWER('1'),
      KEY('2'),
      PINCH('3'),
      CHUCK('4'),
      HORNS('5'), // \m/
      CYLINDER_GRIP('6'),
      MOUSE_GRIP('7'),
      POWER_KEY_MODE_SWITCH('Z'),
      POINT('9'),
      RUDE_POINT('A'),
      HOOK_GRASP('8'),
      RELAX('C'),
      SLEEVE_GRASP('D'),
      PEACE_GRASP('B'),
      CHUCK_GRASP('F'),
      HANG_LOOSE_GRASP('E'),
      HANDSHAKE_GRASP('G'),
      PINCH_FIXED_THUMB_FLEXOR_LOCATION('H'),
//      UNASSIGNED_1('I'),
//      UNASSIGNED_2('J'),
//      UNASSIGNED_3('K'),
//      UNASSIGNED_4('L'),
//      UNASSIGNED_5('M'),
      TRIGGER_GRIP('N'),
//      UNASSIGNED_6('O'),
//      UNASSIGNED_7('P'),
//      UNASSIGNED_8('Q'),
//      UNASSIGNED_9('R'),
//      UNASSIGNED_10('S'),
//      UNASSIGNED_11('T'),
//      UNASSIGNED_12('U'),
      OPTIONAL_WAGGLE_GRIP('V');

      private final byte asciiGripIndex;

      LegacyGripType(char asciiGripIndex)
      {
         this.asciiGripIndex = (byte) asciiGripIndex;
      }

      public byte getAsciiGripIndex()
      {
         return asciiGripIndex;
      }
   }

   @Override
   public String toString()
   {
      return "AbilityHandLegacyGripCommand{" + "legacyGripType=" + legacyGripType + ", legacyGripSpeed=" + legacyGripSpeed + '}';
   }
}
