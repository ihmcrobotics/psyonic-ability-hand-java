package us.ihmc.abilityhand;

import com.sun.jna.Pointer;
import us.ihmc.abilityhand.ble.SimpleBLE;
import us.ihmc.abilityhand.ble.SimpleBLE.libsimpleble;
import us.ihmc.abilityhand.ble.SimpleBLE.libsimpleble.uuid_t.ByValue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbilityHandBLEManager extends Thread
{
   private final SimpleBLE simpleBLE = new SimpleBLE();
   private final String[] handAddresses;
   private final Pointer[] handPeripheralPointers;
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
      this.handPeripheralPointers = new Pointer[handAddresses.length];

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

      Pointer handPeripheralPointer = getHandPeripheralPointer(writeSequence.handAddress);
      byte[] data = writeSequence.sequence;
      libsimpleble.size_t dataLength = new libsimpleble.size_t(data.length);
      ByValue serviceID = new ByValue();
      writeUUID(serviceID, BLEUUID.ABILITY_HAND_SERVICE_ID);
      ByValue characteristicID = new ByValue();
      writeUUID(characteristicID, BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID);

      simpleBLE.writeCommand(handPeripheralPointer, data, dataLength, serviceID, characteristicID);

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

   public void connect() throws InterruptedException
   {
      // TODO: turn adapter on

      Pointer adapterPointer = simpleBLE.getAdapterHandle(0);

      simpleBLE.scanStart(adapterPointer);

      Thread.sleep(2000);

      simpleBLE.scanStop(adapterPointer);

      int peripheralCount = simpleBLE.getScanResultsCount(adapterPointer);

      for (int i = 0; i < peripheralCount; i++)
      {
         Pointer peripheralPointer = simpleBLE.getPeripheralHandle(adapterPointer, i);

         String address = simpleBLE.getPeripheralAddress(peripheralPointer);

         for (int j = 0; j < handAddresses.length; j++)
         {
            if (handAddresses[j].equals(address))
            {
               handPeripheralPointers[j] = peripheralPointer;

               simpleBLE.connectToPeripheral(peripheralPointer);

               Thread.sleep(100);
            }
         }
      }
   }

   public void disconnect() throws InterruptedException
   {
      for (int i = 0; i < handAddresses.length; i++)
      {
         Pointer handPeripheralPointer = handPeripheralPointers[i];

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

   public Pointer getHandPeripheralPointer(String handAddress)
   {
      int index = -1;

      for (int i = 0; i < handAddresses.length; i++)
      {
         if (handAddresses[i].equals(handAddress))
         {
            index = i;
            break;
         }
      }

      return handPeripheralPointers[index];
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
