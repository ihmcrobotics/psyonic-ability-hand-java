package us.ihmc.abilityhand;

import com.sun.jna.Pointer;
import us.ihmc.abilityhand.ble.SimpleBLE;
import us.ihmc.abilityhand.ble.SimpleBLE.libsimpleble.size_t;
import us.ihmc.abilityhand.ble.SimpleBLE.libsimpleble.uuid_t.ByValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbilityHandBLEManager extends Thread
{
   private final SimpleBLE simpleBLE = new SimpleBLE();
   private final String[] handAddresses;
   private final Map<String, Pointer> handAddressToPeripheralPointerMap = new HashMap<>();
   private final Queue<QueuedWriteSequence> writeSequenceQueue = new LinkedList<>();
   private final Lock lock = new ReentrantLock();
   private final Condition notEmpty = lock.newCondition();

   private volatile boolean running;

   private static class QueuedWriteSequence
   {
      private String handAddress;
      private byte[] sequence;
   }

   public AbilityHandBLEManager(String[] handAddresses)
   {
      this.handAddresses = handAddresses;

      running = true;

      start();
   }

   private void consume() throws InterruptedException
   {
      Thread.sleep(20);

      lock.lock();

      while (writeSequenceQueue.isEmpty())
      {
         notEmpty.await();
      }

      QueuedWriteSequence writeSequence = writeSequenceQueue.poll();

      Pointer handPeripheralPointer = handAddressToPeripheralPointerMap.get(writeSequence.handAddress);

      if (handPeripheralPointer != null)
      {
         byte[] data = writeSequence.sequence;
         size_t dataLength = new size_t(data.length);

         ByValue serviceID = new ByValue();
         writeUUID(serviceID, BLEUUID.ABILITY_HAND_SERVICE_ID);

         ByValue characteristicID = new ByValue();
         writeUUID(characteristicID, BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID);

         simpleBLE.writeRequest(handPeripheralPointer, serviceID, characteristicID, data, dataLength);
      }

      lock.unlock();
   }

   @Override
   public void run()
   {
      while (running)
      {
         try
         {
            consume();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
   }

   public int connect() throws InterruptedException
   {
      // TODO: turn adapter on

      int numberOfHandsConnected = 0;

      Pointer adapterPointer = simpleBLE.getAdapterHandle(0);

      simpleBLE.scanStart(adapterPointer);

      Thread.sleep(2000);

      simpleBLE.scanStop(adapterPointer);

      int peripheralCount = simpleBLE.getScanResultsCount(adapterPointer);

      for (int i = 0; i < peripheralCount; i++)
      {
         Pointer peripheralPointer = simpleBLE.getPeripheralHandle(adapterPointer, i);

         String address = simpleBLE.getPeripheralAddress(peripheralPointer);

         for (String handAddress : handAddresses)
         {
            if (handAddress.equalsIgnoreCase(address))
            {
               if (!simpleBLE.connectToPeripheral(peripheralPointer))
               {
                  handAddressToPeripheralPointerMap.put(handAddress, peripheralPointer);

                  numberOfHandsConnected++;
               }

               Thread.sleep(100);
            }
         }
      }

      return numberOfHandsConnected;
   }

   public void disconnect() throws InterruptedException
   {
      for (String handAddress : handAddresses)
      {
         Pointer handPeripheralPointer = handAddressToPeripheralPointerMap.get(handAddress);

         if (handPeripheralPointer != null)
         {
            simpleBLE.peripheralDisconnect(handPeripheralPointer);

            Thread.sleep(100);
         }
      }

      running = false;
   }

   public void sendGripCommand(String handAddress, AbilityHandGripCommand gripCommand)
   {
      QueuedWriteSequence queuedWriteSequence = new QueuedWriteSequence();
      queuedWriteSequence.handAddress = handAddress;
      queuedWriteSequence.sequence = gripCommand.getAsciiSequence();
      lock.lock();
      writeSequenceQueue.offer(queuedWriteSequence);
      notEmpty.signal();
      lock.unlock();
   }

   public void sendIndividualCommand(String handAddress, AbilityHandIndividualFingerControlCommand individualCommand)
   {
      QueuedWriteSequence queuedWriteSequence = new QueuedWriteSequence();
      queuedWriteSequence.handAddress = handAddress;
      queuedWriteSequence.sequence = individualCommand.getAsciiSequence();
      lock.lock();
      writeSequenceQueue.offer(queuedWriteSequence);
      notEmpty.signal();
      lock.unlock();
   }

   public void sendLegacyGripCommand(String handAddress, AbilityHandLegacyGripCommand legacyGripCommand)
   {
      QueuedWriteSequence queuedWriteSequence = new QueuedWriteSequence();
      queuedWriteSequence.handAddress = handAddress;
      queuedWriteSequence.sequence = legacyGripCommand.getAsciiSequence();
      lock.lock();
      writeSequenceQueue.offer(queuedWriteSequence);
      notEmpty.signal();
      lock.unlock();
   }

   private static void writeUUID(ByValue uuidByValue, UUID uuid)
   {
      String uuidString = uuid.toString();

      for (int i = 0; i < uuidString.toCharArray().length; i++)
      {
         uuidByValue.value[i] = (byte) uuidString.toCharArray()[i];
      }

      uuidByValue.value[36] = '\0';
   }
}
