package us.ihmc.abilityhand;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothGattCharacteristic.WriteType;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.welie.blessed.BluetoothCentralManager.SCANOPTION_NO_NULL_NAMES;

public class AbilityHandBLEManager
{
   private final String[] handAddresses;
   private final BluetoothCentralManager bluetoothCentralManager;
   private final Queue<QueuedWriteSequence> writeSequenceQueue = new LinkedList<>();
   private final Lock lock = new ReentrantLock();
   private final Condition notEmpty = lock.newCondition();

   private volatile boolean writeThreadRunning;

   private static class QueuedWriteSequence
   {
      private String handAddress;
      private byte[] sequence;
   }

   public AbilityHandBLEManager(String[] handAddresses)
   {
      this.handAddresses = handAddresses;

      bluetoothCentralManager = new BluetoothCentralManager(new BluetoothCentralManagerCallback()
      {
      }, new HashSet<>(Collections.singleton(SCANOPTION_NO_NULL_NAMES)));

      Thread writeThread = new Thread(() ->
      {
         while (writeThreadRunning)
         {
            try
            {
               Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }

            lock.lock();

            while (writeSequenceQueue.isEmpty())
            {
               try
               {
                  notEmpty.await();
               }
               catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
            }

            QueuedWriteSequence writeSequence = writeSequenceQueue.poll();

            BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(writeSequence.handAddress);
            peripheral.writeCharacteristic(BLEUUID.ABILITY_HAND_SERVICE_ID,
                                           BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID,
                                           writeSequence.sequence,
                                           WriteType.WITHOUT_RESPONSE);

            lock.unlock();
         }
      }, getClass().getSimpleName() + "WriteThread");

      writeThreadRunning = true;

      writeThread.start();
   }

   public void connect() throws InterruptedException
   {
      bluetoothCentralManager.adapterOn();

      bluetoothCtlScan(true, 3);

      bluetoothCentralManager.stopScan();

      for (String handAddress : handAddresses)
      {
         BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

         while (!bluetoothCentralManager.getConnectedPeripherals().contains(peripheral))
         {
            if (peripheral.getDevice() != null && peripheral.getDevice().isConnected())
               break;

            bluetoothCentralManager.connectPeripheral(peripheral, new BluetoothPeripheralCallback()
            {
            });

            Thread.sleep(1000);
         }
      }
   }

   public void disconnect()
   {
      writeThreadRunning = false;

      bluetoothCentralManager.stopScan();

      for (String handAddress : handAddresses)
      {
         BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

         peripheral.cancelConnection();

         bluetoothCentralManager.removeBond(handAddress);
      }
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

   // Blocking
   private static void bluetoothCtlScan(boolean on, int durationSeconds)
   {
      try
      {
         long startTimeMillis = System.currentTimeMillis();

         ProcessBuilder processBuilder = new ProcessBuilder("bluetoothctl", "scan", on ? "on" : "off");

         Process process = processBuilder.start();

         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         while (reader.readLine() != null)
         {
            if (System.currentTimeMillis() - startTimeMillis > (durationSeconds * 1000L))
            {
               process.destroyForcibly();
               Thread.sleep(100);
               break;
            }
         }
         process.waitFor();
      }
      catch (IOException | InterruptedException e)
      {
         e.printStackTrace();
      }
   }
}
