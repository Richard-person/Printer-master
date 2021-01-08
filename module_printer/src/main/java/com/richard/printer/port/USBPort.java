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
import java.util.Iterator;
import java.util.List;

/**
 * author Richard
 * date 2020/8/18 11:08
 * version V1.0
 * description: USB 端口连接相关实现
 */
public class USBPort extends PrinterPort {

    private final String ACTION_USB_PERMISSION = "com.richard.printer.port.USB_PERMISSION";
    private int PRINTER_TIMEOUT = 3000;//打印超时时间（毫秒）

    private UsbManager mUsbManager = null;
    private UsbDevice mUsbDevice = null;
    private UsbInterface mUsbInterface = null;
    private UsbDeviceConnection mUsbDeviceConnection = null;
    private UsbEndpoint mUsbInEndpoint = null;
    private UsbEndpoint mUsbOutEndpoint = null;
    private String mUserUsbName = null;

    /**
     * usb授权结果接收广播
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!ACTION_USB_PERMISSION.equals(intent.getAction())) {
                return;
            }
            synchronized (this) {
                UsbDevice device = intent.getParcelableExtra("device");
                if (intent.getBooleanExtra("permission", false) && device != null) {
                    mUsbDevice = device;
                }
            }
        }
    };

    public USBPort(PortInfo portInfo) {
        super(portInfo);

        //获取USBManager
        this.mUsbManager = (UsbManager) this.mPortInfo.getContext().getSystemService(Context.USB_SERVICE);

        if (portInfo.getPortType() != PortType.USB
                && portInfo.getContext() != null
                && "".equals(portInfo.getUsbPathName())) {
            this.mPortInfo.setParIsOK(false);
        } else {
            this.mPortInfo.setParIsOK(true);
            if (this.mPortInfo.getUsbPathName() != null && !"".equals(portInfo.getUsbPathName())) {
                this.mUserUsbName = this.mPortInfo.getUsbPathName();
            }
        }
    }

    /**
     * 获取USB设备列表
     */
    private List<UsbDevice> getUSBDeviceList() {
        List<UsbDevice> result = new ArrayList<>();
        for (UsbDevice device : this.mUsbManager.getDeviceList().values()) {
            for (int index = 0; index < device.getInterfaceCount(); ++index) {
                if (device.getInterface(index).getInterfaceClass() != 7
                        || device.getInterface(index).getInterfaceSubclass() != 1) {
                    continue;
                }
                result.add(device);
                break;
            }
        }

        return result;
    }

    /**
     * 请求USB设备权限
     */
    private void requestUSBPermission(UsbDevice usbDevice) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this.mPortInfo.getContext(),
                0, new Intent(this.ACTION_USB_PERMISSION), 0);
        IntentFilter intentFilter = new IntentFilter(this.ACTION_USB_PERMISSION);
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        this.mPortInfo.getContext().registerReceiver(this.mUsbReceiver, intentFilter);
        this.mUsbManager.requestPermission(usbDevice, permissionIntent);
    }

    @Override
    public ReturnMessage openPort() {
        if (!this.mPortInfo.isParIsOK()) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "PortInfo error !\n");
        }

        List<UsbDevice> temDevList = this.getUSBDeviceList();
        if (temDevList == null || temDevList.size() <= 0) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "Not find Printer's USB printer !\n");
        }

        this.mUsbDevice = null;
        if (this.mUserUsbName == null) {
            if (this.mUsbManager.hasPermission(temDevList.get(0))) {
                this.mUsbDevice = temDevList.get(0);
            } else {
                this.requestUSBPermission((UsbDevice) temDevList.get(0));
            }
        } else {
            boolean isFind = false;
            for (UsbDevice item : temDevList) {
                if (item.getDeviceName().equals(this.mUserUsbName)) {
                    if (this.mUsbManager.hasPermission(item)) {
                        this.mUsbDevice = item;
                    } else {
                        this.requestUSBPermission(item);
                    }

                    isFind = true;
                    break;
                }
            }

            if (!isFind) {
                return new ReturnMessage(ErrorCode.OpenPortFailed, "Not find " + this.mUserUsbName + " !\n");
            }
        }

        if (this.mUsbDevice == null) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "Get USB communication permission failed !\n");
        }

        for (int iInterface = 0; iInterface < this.mUsbDevice.getInterfaceCount(); ++iInterface) {
            UsbInterface usbInterface = this.mUsbDevice.getInterface(iInterface);
            if (usbInterface.getInterfaceClass() != 7) {
                continue;
            }

            for (int iEndpoint = 0; iEndpoint < usbInterface.getEndpointCount(); ++iEndpoint) {
                if (usbInterface.getEndpoint(iEndpoint).getType() == 2) {
                    if (usbInterface.getEndpoint(iEndpoint).getDirection() == 128) {
                        this.mUsbInEndpoint = usbInterface.getEndpoint(iEndpoint);
                    } else {
                        this.mUsbOutEndpoint = usbInterface.getEndpoint(iEndpoint);
                    }
                }

                if (this.mUsbInEndpoint != null && this.mUsbOutEndpoint != null) {
                    break;
                }
            }

            this.mUsbInterface = usbInterface;
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
        return this.write(new byte[]{(byte) (data & 255)});
    }

    @Override
    public ReturnMessage write(byte[] data) {
        return this.write(data, 0, data.length);
    }

    @Override
    public ReturnMessage write(byte[] data, int offset, int count) {
        if (!this.mIsOpen) {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "USB port was closed !\n");
        }

        byte[] temData = new byte[count];

        for (int i = offset; i < offset + count; ++i) {
            temData[i - offset] = data[i];
        }

        try {
            int writeCount = this.mUsbDeviceConnection.bulkTransfer(this.mUsbOutEndpoint, temData, temData.length, PRINTER_TIMEOUT);
            return writeCount < 0
                    ? new ReturnMessage(ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n")
                    : new ReturnMessage(ErrorCode.WriteDataSuccess, "send " + writeCount + " bytes.\n", writeCount);
        } catch (NullPointerException var7) {
            var7.printStackTrace();
            return new ReturnMessage(ErrorCode.WriteDataFailed, "usb port write bulkTransfer failed !\n");
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer, int offset, int count) {
        if (!this.mIsOpen) {
            return new ReturnMessage(ErrorCode.ReadDataFailed, "USB port was closed !\n");
        }

        byte[] temBuffer = new byte[count];
        int readBytes = this.mUsbDeviceConnection.bulkTransfer(this.mUsbInEndpoint, buffer, count, PRINTER_TIMEOUT);

        if (readBytes < 0) {
            return new ReturnMessage(ErrorCode.ReadDataFailed, "usb port read bulkTransfer failed !\n");
        }

        for (int i = offset; i < offset + readBytes; ++i) {
            buffer[i] = temBuffer[i - offset];
        }

        return new ReturnMessage(ErrorCode.ReadDataSuccess, "Read " + readBytes + " bytes.\n", readBytes);
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
        if (this.mUsbDevice == null || this.mUsbInEndpoint == null || this.mUsbOutEndpoint == null) {
            return this.mIsOpen = false;
        }

        List<String> temStr = this.getUsbPathNames();
        if (temStr == null || temStr.size() <= 0) {
            return this.mIsOpen = false;
        }

        Iterator<String> var3 = temStr.iterator();
        while (var3.hasNext()) {
            if (var3.next().equals(this.mUsbDevice.getDeviceName())) {
                return this.mIsOpen = true;
            }
        }
        return this.mIsOpen = false;
    }


    /**
     * 获取USB路径名称列表
     */
    public List<String> getUsbPathNames() {
        List<String> result = new ArrayList<>();
        List<UsbDevice> usbDeviceList = this.getUSBDeviceList();

        for (UsbDevice device : usbDeviceList) {
            result.add(device.getDeviceName());
        }

        return result;
    }
}
