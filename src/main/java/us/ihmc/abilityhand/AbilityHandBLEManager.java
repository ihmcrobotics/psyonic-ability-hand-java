package us.ihmc.abilityhand;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothGattCharacteristic.WriteType;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;

import java.util.Collections;
import java.util.HashSet;

import static com.welie.blessed.BluetoothCentralManager.SCANOPTION_NO_NULL_NAMES;

public class AbilityHandBLEManager
{
   private final String[] handAddresses;
   private final BluetoothCentralManager bluetoothCentralManager;

   public AbilityHandBLEManager(String[] handAddresses)
   {
      this.handAddresses = handAddresses;

      bluetoothCentralManager = new BluetoothCentralManager(new BluetoothCentralManagerCallback()
      {
      }, new HashSet<>(Collections.singleton(SCANOPTION_NO_NULL_NAMES)));
   }

   public void connect() throws InterruptedException
   {
      bluetoothCentralManager.scanForPeripherals();

      for (String handAddress : handAddresses)
      {
         BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

         while (!bluetoothCentralManager.getConnectedPeripherals().contains(peripheral))
         {
            System.out.println("Connecting " + peripheral.getAddress());

            bluetoothCentralManager.connectPeripheral(peripheral, new BluetoothPeripheralCallback()
            {
            });

            Thread.sleep(1000);
         }
      }

      System.out.println("Hands connected");
   }

   public void disconnect()
   {
      bluetoothCentralManager.stopScan();

      for (String handAddress : handAddresses)
      {
         BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

         peripheral.cancelConnection();

         bluetoothCentralManager.removeBond(handAddress);
      }

      System.out.println("Disconnected");
   }

   public void sendGripCommand(String handAddress, AbilityHandGripCommand gripCommand)
   {
      BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

      peripheral.writeCharacteristic(BLEUUID.ABILITY_HAND_SERVICE_ID,
                                     BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID,
                                     gripCommand.getAsciiSequence(),
                                     WriteType.WITHOUT_RESPONSE);
   }

   public void sendIndividualCommand(String handAddress, AbilityHandIndividualFingerControlCommand individualCommand){
      BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);
      peripheral.writeCharacteristic(BLEUUID.ABILITY_HAND_SERVICE_ID,
              BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID,
              individualCommand.getAsciiSequence(),
              WriteType.WITHOUT_RESPONSE);
   }
   public void sendLegacyGripCommand(String handAddress, AbilityHandLegacyGripCommand legacyGripCommand)
   {
      BluetoothPeripheral peripheral = bluetoothCentralManager.getPeripheral(handAddress);

      peripheral.writeCharacteristic(BLEUUID.ABILITY_HAND_SERVICE_ID,
                                     BLEUUID.ABILITY_HAND_TX_CHARACTERISTIC_ID,
                                     legacyGripCommand.getAsciiSequence(),
                                     WriteType.WITHOUT_RESPONSE);
   }
}
