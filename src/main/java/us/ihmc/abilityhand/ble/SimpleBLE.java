package us.ihmc.abilityhand.ble;

import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import org.apache.commons.lang3.SystemUtils;
import us.ihmc.tools.nativelibraries.NativeLibraryDescription.Architecture;
import us.ihmc.tools.nativelibraries.NativeLibraryDescription.OperatingSystem;
import us.ihmc.tools.nativelibraries.NativeLibraryLoader;

import java.util.Arrays;
import java.util.List;

public class SimpleBLE
{
   private static String libraryFile;

   static
   {
      SimpleBLENativeLibrary simpleBLENativeLibrary = new SimpleBLENativeLibrary();

      Architecture arch = getArchitecture();
      OperatingSystem platform = getOS();

      String packageName = simpleBLENativeLibrary.getPackage(platform, arch);

      List<String> files = NativeLibraryLoader.extractLibraryWithDependenciesAbsolute(packageName,
                                                                                      simpleBLENativeLibrary.getLibraryWithDependencies(platform, arch));

      for (String file : files)
      {
         if (file.contains("-c"))
         {
            libraryFile = file;
         }
      }
   }

   private static OperatingSystem getOS()
   {
      if (SystemUtils.IS_OS_WINDOWS)
      {
         return OperatingSystem.WIN64;
      }
      else if (SystemUtils.IS_OS_MAC)
      {
         return OperatingSystem.MACOSX64;
      }
      else if (SystemUtils.IS_OS_LINUX)
      {
         return OperatingSystem.LINUX64;
      }
      else
      {
         throw new UnsatisfiedLinkError(
               "Cannot load library. Operating system not supported by native library loader: " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION);
      }
   }

   private static Architecture getArchitecture()
   {
      return Architecture.x64;
   }

   public interface libsimpleble extends Library
   {
      libsimpleble INSTANCE = Native.load(libraryFile, libsimpleble.class);

      boolean simpleble_adapter_is_bluetooth_enabled();

      int simpleble_adapter_get_count();

      Pointer simpleble_adapter_get_handle(int index);

      boolean simpleble_adapter_scan_start(Pointer handle);

      boolean simpleble_adapter_scan_stop(Pointer handle);

      boolean simpleble_adapter_scan_for(Pointer handle, int timeout);

      int simpleble_adapter_scan_get_results_count(Pointer handle);

      Pointer simpleble_adapter_scan_get_results_handle(Pointer handle, int index);

      boolean simpleble_peripheral_is_connectable(Pointer handle, boolean[] connectable);

      boolean simpleble_peripheral_is_connected(Pointer handle, boolean[] connected);

      String simpleble_peripheral_identifier(Pointer handle);

      String simpleble_peripheral_address(Pointer handle);

      void simpleble_peripheral_release_handle(Pointer handle);

      boolean simpleble_peripheral_connect(Pointer handle);

      boolean simpleble_peripheral_write_command(Pointer handle, byte[] data, size_t length, uuid_t.ByValue service, uuid_t.ByValue characteristic);

      boolean simpleble_peripheral_write_request(Pointer handle, uuid_t service, uuid_t characteristic, byte[] data, size_t length);

      boolean simpleble_peripheral_services_get(Pointer handle, int index, service_t services);

      int simpleble_peripheral_services_count(Pointer handle);

      boolean simpleble_peripheral_disconnect(Pointer handle);

      public static class simpleble_peripheral_t extends PointerType
      {
      }

      class size_t extends IntegerType
      {
         public static final size_t ZERO = new size_t();

         private static final long serialVersionUID = 1L;

         public size_t()
         {
            this(0);
         }

         public size_t(long value)
         {
            super(Native.SIZE_T_SIZE, value, true);
         }
      }

      public static class uuid_t extends Structure
      {
         public static class ByValue extends uuid_t implements Structure.ByValue
         {
         }

         private static final List<String> FIELDS = Arrays.asList("value");

         public byte[] value = new byte[37]; // SIMPLEBLE_UUID_STR_LEN

         @Override
         protected List<String> getFieldOrder()
         {
            return FIELDS;
         }
      }

      public static class descriptor_t extends Structure
      {
         public static class ByValue extends descriptor_t implements Structure.ByValue
         {
         }

         private static final List<String> FIELDS = Arrays.asList("uuid");

         public uuid_t uuid;

         @Override
         protected List<String> getFieldOrder()
         {
            return FIELDS;
         }
      }

      public class characteristic_t extends Structure
      {
         public static class ByValue extends characteristic_t implements Structure.ByValue
         {
         }

         private static final List<String> FIELDS = Arrays.asList("uuid",
                                                                  "can_read",
                                                                  "can_write_request",
                                                                  "can_write_command",
                                                                  "can_notify",
                                                                  "can_indicate",
                                                                  "descriptor_count",
                                                                  "descriptors");

         public uuid_t uuid;
         public byte can_read;
         public byte can_write_request;
         public byte can_write_command;
         public byte can_notify;
         public byte can_indicate;
         public size_t descriptor_count;
         public descriptor_t[] descriptors = new descriptor_t[16];

         @Override
         protected List<String> getFieldOrder()
         {
            return FIELDS;
         }
      }

      public class service_t extends Structure
      {
         public static class ByValue extends service_t implements Structure.ByValue
         {
         }

         private static final List<String> FIELDS = Arrays.asList("uuid", "data_length", "data", "characteristic_count", "characteristics");

         public uuid_t uuid;
         public size_t data_length;
         public byte[] data = new byte[27];
         public size_t characteristic_count;
         public characteristic_t[] characteristics = new characteristic_t[16];

         @Override
         protected List<String> getFieldOrder()
         {
            return FIELDS;
         }
      }
   }

   public boolean isBluetoothEnabled()
   {
      return libsimpleble.INSTANCE.simpleble_adapter_is_bluetooth_enabled();
   }

   public int getAdapterCount()
   {
      return libsimpleble.INSTANCE.simpleble_adapter_get_count();
   }

   public Pointer getAdapterHandle(int index)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_get_handle(index);
   }

   public boolean scanStart(Pointer adapterHandle)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_scan_start(adapterHandle);
   }

   public boolean scanStop(Pointer adapterHandel)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_scan_stop(adapterHandel);
   }

   public boolean scanFor(Pointer adapterHandle, int timeout)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_scan_for(adapterHandle, timeout);
   }

   public int getScanResultsCount(Pointer adapterHandle)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_scan_get_results_count(adapterHandle);
   }

   public Pointer getPeripheralHandle(Pointer adapterHandle, int index)
   {
      return libsimpleble.INSTANCE.simpleble_adapter_scan_get_results_handle(adapterHandle, index);
   }

   public String getPeripheralIdentifier(Pointer peripheralHandle)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_identifier(peripheralHandle);
   }

   public String getPeripheralAddress(Pointer peripheralHandle)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_address(peripheralHandle);
   }

   public void releasePeripheral(Pointer peripheralHandle)
   {
      libsimpleble.INSTANCE.simpleble_peripheral_release_handle(peripheralHandle);
   }

   public boolean isPeripheralConnectable(Pointer peripheralHandle, boolean[] connectable)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_is_connectable(peripheralHandle, connectable);
   }

   public boolean isPeripheralConnected(Pointer peripheralHandle, boolean[] connected)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_is_connected(peripheralHandle, connected);
   }

   public boolean connectToPeripheral(Pointer peripheralHandle)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_connect(peripheralHandle);
   }

   public boolean writeCommand(Pointer peripheralHandle,
                               byte[] data,
                               libsimpleble.size_t length,
                               libsimpleble.uuid_t.ByValue serviceUuid,
                               libsimpleble.uuid_t.ByValue characteristicUuid)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_write_command(peripheralHandle, data, length, serviceUuid, characteristicUuid);
   }

   public boolean writeRequest(Pointer peripheralHandle,
                               libsimpleble.uuid_t serviceUuid,
                               libsimpleble.uuid_t characteristicUuid,
                               byte[] data,
                               libsimpleble.size_t length)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_write_request(peripheralHandle, serviceUuid, characteristicUuid, data, length);
   }

   public boolean getPeripheralServices(Pointer peripheralHandle, int index, libsimpleble.service_t services)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_services_get(peripheralHandle, index, services);
   }

   public int peripheralServicesCount(Pointer peripheralHandle)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_services_count(peripheralHandle);
   }

   public boolean peripheralDisconnect(Pointer peripheralHandle)
   {
      return libsimpleble.INSTANCE.simpleble_peripheral_disconnect(peripheralHandle);
   }
}
