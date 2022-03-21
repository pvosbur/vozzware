package test.vozzware.util;

import com.vozzware.util.VwResourceMgr;
import org.junit.Test;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import java.util.List;
import java.util.ResourceBundle;

/*
============================================================================================

    Source File Name: 

    Author:           petervosburgh
    
    Date Generated:   12/11/15

    Time Generated:   6:26 AM

============================================================================================
*/
public class VwUSBTester
{
  @Test
  public void testUDBInit() throws Exception
  {
    Context context = new Context();
    int result = LibUsb.init( context );
    if (result != LibUsb.SUCCESS)
    {
      throw new LibUsbException("Unable to initialize libusb.", result);
    }

    // Get Device List

    // Read the USB device list
     DeviceList list = new DeviceList();
     result = LibUsb.getDeviceList(null, list);
     if (result < 0)
     {
       throw new RuntimeException( "Unable to get device list. Result=" + result );
     }

     try
     {
         // Iterate over all devices and scan for the right one
         for (Device device: list)
         {
           DeviceDescriptor descriptor = new DeviceDescriptor();
           result = LibUsb.getDeviceDescriptor(device, descriptor);
           if (result != LibUsb.SUCCESS)
           {
             throw new LibUsbException("Unable to read device descriptor", result);
           }

           System.out.println( descriptor.dump());
         }
     }
     finally
     {
         // Ensure the allocated device list is freed
         LibUsb.freeDeviceList(list, true);
     }

     // Device not found


  }

  @Test
  public void testHighLevel() throws Exception
  {

    ResourceBundle bundel = ResourceBundle.getBundle( "javax.usb" );

    String services = VwResourceMgr.getString( "javax.usb.services" );
    UsbHub hubRoot = UsbHostManager.getUsbServices().getRootUsbHub();

    findDevice( hubRoot );

  }

  private void findDevice( UsbHub hub )
  {
    for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
    {
      UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

      System.out.println( desc.toString() );
      if (device.isUsbHub())
       {
         findDevice((UsbHub) device);
       }
    }

  }
} // end  VwUSBTester{}
