package us.ihmc.abilityhand;

import java.nio.charset.StandardCharsets;

public class AbilityHandGripCommand
{
   // gxx:xx.xx
   // g<grip index>:<floating point in seconds of the duration>
   // Examples
   // g03:0.20 - execute a power grasp with a 0.2 second motion period
   // g-1:5.1234 - open the hand with a 5.1234 seconds motion period
   private static final byte GRIP_COMMAND_ASCII_PREFIX = 'g';

   private GripConfiguration gripConfiguration;
   private double durationSeconds;

   public AbilityHandGripCommand(GripConfiguration gripConfiguration, double durationSeconds)
   {
      this.gripConfiguration = gripConfiguration;
      this.durationSeconds = durationSeconds;
   }

   public GripConfiguration getGripConfiguration()
   {
      return gripConfiguration;
   }

   public void setGripConfiguration(GripConfiguration gripConfiguration)
   {
      this.gripConfiguration = gripConfiguration;
   }

   public double getDurationSeconds()
   {
      return durationSeconds;
   }

   public void setDurationSeconds(double durationSeconds)
   {
      this.durationSeconds = durationSeconds;
   }

   public byte[] getAsciiSequence()
   {
      String gripIndexString = String.format("%02d", gripConfiguration.getGripIndex());
      String durationString = String.format("%.5f", durationSeconds);
      String gripCommandString = GRIP_COMMAND_ASCII_PREFIX + gripIndexString + ":" + durationString;
      return gripCommandString.getBytes(StandardCharsets.US_ASCII);
   }

   public enum GripConfiguration
   {
      CHUCK_GRASP(0),
      CHUCK_OK_GRASP(1),
      PINCH_GRASP(2),
      POWER_GRASP(3),
      KEY_GRASP(4),
      HANDSHAKE_GRASP(5),
      THREE_FINGER_TRIGGER_GRASP(6),
      POINT_GRASP(7),
      RELAX_GRASP(8),
      SIGN_OF_THE_HORNS_GRASP(9),
      RUDE_POINT_GRASP(10),
      MODE_SWITCH_CLOSER_GRASP(11), // special grip which gets written to by application
      UTILITY_CYLINDER(12),
      MOUSE(13),
      HOOK(14),
      SLEEVE(15),
      HANGLOOSE(16),
      PINCH_THUMB_DOES_NOT_MOVE(17),
      WAGGLE_GRIP(31); // IF waggle is not disabled (We28 disables it)

      private final int gripIndex;

      GripConfiguration(int gripIndex)
      {
         this.gripIndex = gripIndex;
      }

      public int getGripIndex()
      {
         return gripIndex;
      }
   }
}
