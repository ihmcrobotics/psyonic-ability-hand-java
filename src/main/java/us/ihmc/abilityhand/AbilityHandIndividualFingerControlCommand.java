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
   //Thumb Rotator goes inward the more negative the number
   private float indexPosition = 5;
   private float  indexPeriod = 0;
   private float middlePosition = 5;
   private float  middlePeriod = 0;
   private float ringPosition = 5;
   private float  ringPeriod = 0;
   private float pinkyPosition = 5;
   private float  pinkyPeriod = 0;
   private float thumbFlexorPosition = 5;
   private float  thumbFlexorPeriod = 0;
   private float thumbRotatorPosition = 50;
   private float  thumbRotatorPeriod = 0;


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
   public void setIndexPosition(float indexPosition)
   {
      this.indexPosition = indexPosition;
   }
   public void setIndexPeriod(float indexPeriod)
   {
      this.indexPeriod = indexPeriod;
   }
   public void setMiddlePosition(float middlePosition)
   {
      this.middlePosition = middlePosition;
   }
   public void setMiddlePeriod(float middlePeriod)
   {
      this.middlePeriod = middlePeriod;
   }
   public void setRingPosition(float ringPosition)
   {
      this.ringPosition = ringPosition;
   }
   public void setRingPeriod(float ringPeriod)
   {
      this.ringPeriod = ringPeriod;
   }
   public void setPinkyPosition(float pinkyPosition)
   {
      this.pinkyPosition = pinkyPosition;
   }
   public void setPinkyPeriod(float pinkyPeriod)
   {
      this.pinkyPeriod = pinkyPeriod;
   }
   public void setThumbFlexorPosition(float thumbFlexorPosition)
   {
      this.thumbFlexorPosition = thumbFlexorPosition;
   }
   public void setThumbFlexorPeriod(float thumbFlexorPeriod)
   {
      this.thumbFlexorPeriod = thumbFlexorPeriod;
   }
   public void setThumbRotatorPosition(float thumbRotatorPosition)
   {
      this.thumbRotatorPosition = thumbRotatorPosition;
   }
   public void setThumbRotatorPeriod(float thumbRotatorPeriod)
   {
      this.thumbRotatorPeriod = thumbRotatorPeriod;
   }
}
