package com.richard.printer.port;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.richard.printer.enumerate.ErrorCode;
import com.richard.printer.enumerate.PortType;
import com.richard.printer.model.PortInfo;
import com.richard.printer.model.ReturnMessage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * author Richard
 * date 2020/8/18 10:42
 * version V1.0
 * description: 蓝牙连接相关实现
 */
public class BluetoothPort extends PrinterPort {

    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBtAdapter = null;
    private BluetoothDevice mBtDevice = null;
    private BluetoothSocket mBtSocket = null;
    private OutputStream mOutPut = null;
    private InputStream mInPut = null;

    public BluetoothPort(PortInfo portInfo) {
        super(portInfo);
        if (portInfo.getPortType() == PortType.Bluetooth
                && BluetoothAdapter.checkBluetoothAddress(portInfo.getBluetoothId())) {
            this.mPortInfo.setParIsOK(true);
            this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            this.mPortInfo.setParIsOK(false);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public ReturnMessage openPort() {
        if (!this.mPortInfo.isParIsOK()) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, "PortInfo error !\n");
        }

        try {
            if (this.mBtAdapter == null) {
                return new ReturnMessage(ErrorCode.OpenPortFailed, "Not Bluetooth adapter !\n");
            }

            if (!this.mBtAdapter.isEnabled()) {
                return new ReturnMessage(ErrorCode.OpenPortFailed, "Bluetooth adapter was closed !\n");
            }

            this.mBtAdapter.cancelDiscovery();
            this.mBtDevice = this.mBtAdapter.getRemoteDevice(this.mPortInfo.getBluetoothId());
            this.mBtSocket = this.mBtDevice.createRfcommSocketToServiceRecord(this.SPP_UUID);
            this.mBtSocket.connect();
            this.mOutPut = null;
            this.mOutPut = this.mBtSocket.getOutputStream();
            this.mInPut = null;
            this.mInPut = this.mBtSocket.getInputStream();
            this.mIsOpen = true;
        } catch (Exception var2) {
            return new ReturnMessage(ErrorCode.OpenPortFailed, var2.toString());
        }

        return new ReturnMessage(ErrorCode.OpenPortSuccess, "Open bluetooth port success !\n");
    }

    @Override
    public ReturnMessage closePort() {
        try {
            if (this.mOutPut != null) {
                this.mOutPut.flush();
            }

            if (this.mBtSocket != null) {
                this.mBtSocket.close();
            }

            this.mIsOpen = false;
            this.mOutPut = null;
            this.mInPut = null;
        } catch (Exception var2) {
            return new ReturnMessage(ErrorCode.ClosePortFailed, var2.toString());
        }

        return new ReturnMessage(ErrorCode.ClosePortSuccess, "Close bluetooth port success !\n");
    }

    @Override
    public ReturnMessage write(int data) {
        if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
            try {
                this.mOutPut.write(data);
            } catch (Exception var3) {
                this.closePort();
                return new ReturnMessage(ErrorCode.WriteDataFailed, var3.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send 1 byte .\n", 1);
        } else {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "bluetooth port was close !\n");
        }
    }

    @Override
    public ReturnMessage write(byte[] data) {
        if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
            try {
                this.mOutPut.write(data);
            } catch (Exception var3) {
                this.closePort();
                return new ReturnMessage(ErrorCode.WriteDataFailed, var3.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send " + data.length + " bytes .\n", data.length);
        } else {
            return new ReturnMessage(ErrorCode.WriteDataFailed, "bluetooth port was close !\n");
        }
    }

    @Override
    public ReturnMessage write(byte[] data, int offset, int count) {
        if (this.mIsOpen && this.mBtSocket.isConnected() && this.mOutPut != null) {
            try {
                this.mOutPut.write(data, offset, count);
            } catch (Exception var5) {
                return new ReturnMessage(ErrorCode.WriteDataFailed, var5.toString());
            }

            return new ReturnMessage(ErrorCode.WriteDataSuccess, "Send " + count + " bytes .\n", count);
        } else {
            this.closePort();
            return new ReturnMessage(ErrorCode.WriteDataFailed, "bluetooth port was close !\n");
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer, int offset, int count) {
        if (this.mIsOpen && this.mBtSocket.isConnected() && this.mInPut != null) {
            int readBytes;
            try {
                readBytes = this.mInPut.read(buffer, offset, count);
            } catch (Exception var6) {
                return new ReturnMessage(ErrorCode.ReadDataFailed, var6.toString());
            }

            return new ReturnMessage(ErrorCode.ReadDataSuccess, "Read " + count + " bytes .\n", readBytes);
        } else {
            this.closePort();
            return new ReturnMessage(ErrorCode.ReadDataFailed, "bluetooth port was close !\n");
        }
    }

    @Override
    public ReturnMessage read(byte[] buffer) {
        return this.read(buffer, 0, buffer.length);
    }

    @Override
    public int read() {
        if (this.mIsOpen && this.mBtSocket.isConnected() && this.mInPut != null) {
            try {
                return this.mInPut.read();
            } catch (Exception var2) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean portIsOpen() {
        byte[] b = new byte[4];
        ReturnMessage msg = this.read(b);
        if (msg.getReadByteCount() == -1) {
            this.mIsOpen = false;
        } else {
            this.mIsOpen = true;
        }

        return this.mIsOpen;
    }
}
