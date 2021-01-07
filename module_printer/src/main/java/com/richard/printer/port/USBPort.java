package com.richard.printer.port;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.richard.printer.enumerate.ErrorCode;
import com.richard.printer.enumerate.PortType;
import com.richard.printer.model.PortInfo;
import com.richard.printer.model.ReturnMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * author Richard
 * date 2020/8/18 11:08
 * version V1.0
 * description: USB 端口连接相关实现
 */
public class USBPort extends PrinterPort{

    private UsbManager mUsbManager = null;
    private UsbDevice mUsbDevice = null;
    private UsbInterface mUsbInterface = null;
    private UsbDeviceConnection mUsbDeviceConnection = null;
    private UsbEndpoint mUsbInEndpoint = null;
    private UsbEndpoint mUsbOutEndpoint = null;
    private PendingIntent mPermissionIntent = null;
    private String mUserUsbName = null;
    private final String ACTION_USB_PERMISSION = "com.richard.printer.port.USB_PERMISSION";
    private int usbData;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false) && device != null) {
                        mUsbDevice = device;
                    }
                }
            }

        }
    };

    public USBPort(PortInfo portInfo) {
        super(portInfo);
        if (portInfo.getPortType() != PortType.USB && portInfo.getContext() != null && portInfo.getUsbPathName().equals("")) {
            this.mPortInfo.setParIsOK(false);
        } else {
            this.mPortInfo.setParIsOK(true);
            if (this.mPortInfo.getUsbPathName() != null && !portInfo.getUsbPathName().equals("")) {
                this.mUserUsbName = this.mPortInfo.getUsbPathName();
            }
        }

    }

    private int setUsbData(int d1) {
        return this.usbData = d1;
    }

    public int getUsbData() {
        return this.usbData;
    }

    /**
     * 获取USB设备列表
     */
    private List<UsbDevice> getUsbDeviceList() {
        List<UsbDevice> temList = new ArrayList<>();
        this.mUsbManager = (UsbManager) this.mPortInfo.getContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = this.mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                if (device.getInterface(iInterface).getInterfaceClass() == 7
                        && device.getInterface(iInterface).getInterfaceSubclass() == 1) {
                    temList.add(device);
                    break;
                }
            }
        }

        if (temList.size() == 0) {
            return null;
        }

        return temList;
    }

    @Override
    public ReturnMessage openPort() {
        if (!this.mPortInfo.isParIsOK()) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "PortInfo error !\n");
        }

        List<UsbDevice> temDevList = this.getUsbDeviceList();
        if (temDevList == null) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "Not find XPrinter's USB printer !\n");
        }

        this.mUsbDevice = null;
        if (this.mUserUsbName == null) {
            if (this.mUsbManager.hasPermission(temDevList.get(0))) {
                this.mUsbDevice = temDevList.get(0);
            } else {
                this.mPermissionIntent = PendingIntent.getBroadcast(this.mPortInfo.getContext(), 0, new Intent(this.ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(this.ACTION_USB_PERMISSION);
                filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                filter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                filter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                this.mPortInfo.getContext().registerReceiver(this.mUsbReceiver, filter);
                this.mUsbManager.requestPermission((UsbDevice) temDevList.get(0), this.mPermissionIntent);
            }
        } else {
            boolean isEq = false;
            Iterator<UsbDevice> usbDeviceIterator = temDevList.iterator();
            while (usbDeviceIterator.hasNext()) {
                UsbDevice dev = usbDeviceIterator.next();
                if (dev.getDeviceName().equals(this.mUserUsbName)) {
                    if (this.mUsbManager.hasPermission(dev)) {
                        this.mUsbDevice = dev;
                    } else {
                        this.mPermissionIntent = PendingIntent.getBroadcast(this.mPortInfo.getContext(), 0, new Intent(this.ACTION_USB_PERMISSION), 0);
                        IntentFilter filterx = new IntentFilter(this.ACTION_USB_PERMISSION);
                        filterx.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                        filterx.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                        filterx.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                        filterx.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                        this.mPortInfo.getContext().registerReceiver(this.mUsbReceiver, filterx);
                        this.mUsbManager.requestPermission(dev, this.mPermissionIntent);
                    }

                    isEq = true;
                    break;
                }
            }

            if (!isEq) {
                return new ReturnMessage(ErrorCode.OpenPortFailed, "Not find " + this.mUserUsbName + " !\n");
            }
        }

        if (this.mUsbDevice == null) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "Get USB communication permission failed !\n");
        }

        for (int iInterface = 0; iInterface < this.mUsbDevice.getInterfaceCount(); ++iInterface) {
            if (this.mUsbDevice.getInterface(iInterface).getInterfaceClass() != 7) {
                continue;
            }

            for (int iEndpoint = 0; iEndpoint < this.mUsbDevice.getInterface(iInterface).getEndpointCount(); ++iEndpoint) {
                if (this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint).getType() == 2) {
                    if (this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint).getDirection() == 128) {
                        this.mUsbInEndpoint = this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint);
                    } else {
                        this.mUsbOutEndpoint = this.mUsbDevice.getInterface(iInterface).getEndpoint(iEndpoint);
                    }
                }

                if (this.mUsbInEndpoint != null && this.mUsbOutEndpoint != null) {
                    break;
                }
            }

            this.mUsbInterface = this.mUsbDevice.getInterface(iInterface);
            break;
        }

        this.mUsbDeviceConnection = this.mUsbManager.openDevice(this.mUsbDevice);
        if (this.mUsbDeviceConnection != null && this.mUsbDeviceConnection.claimInterface(this.mUsbInterface, true)) {
            this.mPortInfo.setUsbPathName(this.mUsbDevice.getDeviceName());
            this.mIsOpen = true;
            return new ReturnMessage(ErrorCode.OpenPortSuccess, "Open USB port success !\n");
        } else {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "Can't Claims exclusive access to UsbInterface");
        }
    }

    @Override
    public ReturnMessage closePort() {
        if (this.mUsbDeviceConnection != null) {
            this.mUsbInEndpoint = null;
            this.mUsbOutEndpoint = null;
            this.mUsbDeviceConnection.releaseInterface(this.mUsbInterface);
            this.mUsbDeviceConnection.close();
            this.mUsbDeviceConnection = null;
        }

        this.mIsOpen = false;
        return new ReturnMessage(ErrorCode.ClosePortSuccess, "Close usb connection success !\n");
    }

    @Override
    public ReturnMessage write(int data) {
        byte[] tem = new byte[]{(byte) (data & 255)};
        return this.write(tem);
    }

    @Override
    public ReturnMessage write(byte[] data) {
        return this.write(data, 0, data.length);
    }

    @Override
    public ReturnMessage write(byte[] data, int offset, int count) {
        if (!this.mIsOpen) {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "USB port was closed !\n");
        } else {
            byte[] temData = new byte[count];

            for (int i = offset; i < offset + count; ++i) {
                temData[i - offset] = data[i];
            }

            byte requestTime = 0;

            try {
                int writeCount = this.mUsbDeviceConnection.bulkTransfer(this.mUsbOutEndpoint, temData, temData.length, requestTime);
                Log.i("USBwrite", String.valueOf(writeCount));
                this.setUsbData(writeCount);
                return writeCount < 0
                        ? new ReturnMessage(ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n")
                        : new ReturnMessage(ErrorCode.WriteDataSuccess, "send " + writeCount + " bytes.\n", writeCount);
            } catch (NullPointerException var7) {
                var7.printStackTrace();
                return new ReturnMessage(ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n");
            }
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer, int offset, int count) {
        if (!this.mIsOpen) {
            return new ReturnMessage(ErrorCode.ReadDataFailed, "USB port was closed !\n");
        }

        byte[] temBuffer = new byte[count];
        int readBytes = this.mUsbDeviceConnection.bulkTransfer(this.mUsbInEndpoint, buffer, count, 3000);
        if (readBytes < 0) {
            return new ReturnMessage(ErrorCode.ReadDataFailed, "usb port read bulkTransfer failed !\n");
        } else {
            for (int i = offset; i < offset + readBytes; ++i) {
                buffer[i] = temBuffer[i - offset];
            }
            return new ReturnMessage(ErrorCode.ReadDataSuccess, "Read " + readBytes + " bytes.\n", readBytes);
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer) {
        return this.read(buffer, 0, buffer.length);
    }

    @Override
    public int read() {
        byte[] temBuffer = new byte[1];
        return this.read(temBuffer).getErrorCode() == ErrorCode.OpenPortFailed ? -1 : temBuffer[0];
    }

    @Override
    public boolean portIsOpen() {
        if (this.mUsbDevice != null && this.mUsbInEndpoint != null && this.mUsbOutEndpoint != null) {
            List<String> temStr = getUsbPathNames(this.mPortInfo.getContext());
            if (temStr != null && temStr.size() > 0) {
                Iterator<String> var3 = temStr.iterator();
                while (var3.hasNext()) {
                    String str = var3.next();
                    if (str.equals(this.mUsbDevice.getDeviceName())) {
                        return this.mIsOpen = true;
                    }
                }
                return this.mIsOpen = false;
            } else {
                return this.mIsOpen = false;
            }
        } else {
            return this.mIsOpen = false;
        }
    }


    /**
     * 获取USB路径名称列表
     */
    public static List<String> getUsbPathNames(Context context) {
        List<String> usbNames = new ArrayList<>();
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbList = usbManager.getDeviceList();

        for (UsbDevice device : usbList.values()) {
            for (int iInterface = 0; iInterface < device.getInterfaceCount(); ++iInterface) {
                if (device.getInterface(iInterface).getInterfaceClass() == 7
                        && device.getInterface(iInterface).getInterfaceSubclass() == 1) {
                    usbNames.add(device.getDeviceName());
                    break;
                }
            }
        }

        if (usbNames.size() == 0) {
            usbNames = null;
        }

        return usbNames;
    }
}
