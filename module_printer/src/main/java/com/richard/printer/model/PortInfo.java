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

    /**
     * 端口类型
     */
    private PortType portType;

    /**
     * usb名称
     */
    private String usbPathName;

    /**
     * usb 产品ID
     */
    private int usbProductId;

    /**
     * usb供应商ID
     */
    private int usbVendorId;

    /**
     * 网络端口
     */
    private int ethernetPort;

    /**
     * 网络IP地址
     */
    private String ethernetIP;

    /**
     * 蓝牙ID
     */
    private String bluetoothId;

    /**
     * 是否已准备
     */
    private boolean parIsOK;

    /**
     * context
     */
    private Context context;

    /**
     * 是否已打开端口连接
     */
    private boolean isOpened;

    public PortInfo() {
        this.portType = PortType.Unknown;
        this.usbPathName = "";
        this.usbProductId = 0;
        this.usbVendorId = 0;
        this.ethernetPort = 0;
        this.ethernetIP = "";
        this.bluetoothId = "";
        this.parIsOK = false;
        this.context = null;
        this.isOpened = false;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public String getUsbPathName() {
        return usbPathName;
    }

    public void setUsbPathName(String usbPathName) {
        this.usbPathName = usbPathName;
    }

    public int getUsbProductId() {
        return usbProductId;
    }

    public void setUsbProductId(int usbProductId) {
        this.usbProductId = usbProductId;
    }

    public int getUsbVendorId() {
        return usbVendorId;
    }

    public void setUsbVendorId(int usbVendorId) {
        this.usbVendorId = usbVendorId;
    }

    public int getEthernetPort() {
        return ethernetPort;
    }

    public void setEthernetPort(int ethernetPort) {
        this.ethernetPort = ethernetPort;
    }

    public String getEthernetIP() {
        return ethernetIP;
    }

    public void setEthernetIP(String ethernetIP) {
        this.ethernetIP = ethernetIP;
    }

    public String getBluetoothId() {
        return bluetoothId;
    }

    public void setBluetoothId(String bluetoothId) {
        this.bluetoothId = bluetoothId;
    }

    public boolean isParIsOK() {
        return parIsOK;
    }

    public void setParIsOK(boolean parIsOK) {
        this.parIsOK = parIsOK;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }
}
