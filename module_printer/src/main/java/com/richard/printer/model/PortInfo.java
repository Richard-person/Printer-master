package com.richard.printer.model;

import android.content.Context;

import com.richard.printer.enumerate.PortType;

import java.io.Serializable;

/**
 * author Richard
 * date 2020/8/18 10:44
 * version V1.0
 * description: 端口信息
 */
public class PortInfo implements Serializable {

    private PortType mPortType;
    private String mUsbPathName;
    private int mUsbPid;
    private int mUsbVid;
    private int mEthernetPort;
    private String mEthernetIP;
    private String mBluetoothID;
    private boolean mParIsOK;
    private Context mContext;
    private boolean mIsOpened;

    public PortInfo() {
        this.mPortType = PortType.Unknown;
        this.mUsbPathName = "";
        this.mUsbPid = 0;
        this.mUsbVid = 0;
        this.mEthernetPort = 0;
        this.mEthernetIP = "";
        this.mBluetoothID = "";
        this.mParIsOK = false;
        this.mContext = null;
        this.mIsOpened = false;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public PortType getPortType() {
        return this.mPortType;
    }

    public void setPortType(PortType portType) {
        mPortType = portType;
    }

    public String getUsbPathName() {
        return mUsbPathName;
    }

    public void setUsbPathName(String usbPathName) {
        mUsbPathName = usbPathName;
    }

    public int getUsbPid() {
        return mUsbPid;
    }

    public void setUsbPid(int usbPid) {
        mUsbPid = usbPid;
    }

    public int getUsbVid() {
        return mUsbVid;
    }

    public void setUsbVid(int usbVid) {
        mUsbVid = usbVid;
    }

    public int getEthernetPort() {
        return mEthernetPort;
    }

    public void setEthernetPort(int ethernetPort) {
        mEthernetPort = ethernetPort;
    }

    public String getEthernetIP() {
        return mEthernetIP;
    }

    public void setEthernetIP(String ethernetIP) {
        mEthernetIP = ethernetIP;
    }

    public String getBluetoothID() {
        return mBluetoothID;
    }

    public void setBluetoothID(String bluetoothID) {
        mBluetoothID = bluetoothID;
    }

    public boolean isParIsOK() {
        return mParIsOK;
    }

    public void setParIsOK(boolean parIsOK) {
        mParIsOK = parIsOK;
    }

    public boolean isOpened() {
        return mIsOpened;
    }

    public void setOpened(boolean opened) {
        mIsOpened = opened;
    }
}
