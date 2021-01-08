//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.richard.printer.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.richard.printer.enumerate.ErrorCode;
import com.richard.printer.enumerate.PortType;
import com.richard.printer.exception.PrinterException;
import com.richard.printer.model.PortInfo;
import com.richard.printer.model.ReturnMessage;
import com.richard.printer.port.BluetoothPort;
import com.richard.printer.port.EthernetPort;
import com.richard.printer.port.PrinterPort;
import com.richard.printer.port.USBPort;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 打印
 */
public class POSPrinter {

    private PortInfo mPortInfo = new PortInfo();
    private PrinterPort mPort = null;

    /**
     * POSPrinter构造
     *
     * @param portType    端口类型
     * @param context     context
     * @param usbPathName usb 路径名称
     */
    public POSPrinter(PortType portType, Context context, String usbPathName) {
        this.mPortInfo.setPortType(portType);
        this.mPortInfo.setContext(context);
        if (usbPathName != null && !usbPathName.equals("")) {
            this.mPortInfo.setUsbPathName(usbPathName);
        }
    }

    /**
     * POSPrinter构造
     *
     * @param portType    端口类型
     * @param bluetoothID 蓝牙ID
     */
    public POSPrinter(PortType portType, String bluetoothID) {
        this.mPortInfo.setPortType(portType);
        this.mPortInfo.setBluetoothId(bluetoothID);
    }

    /**
     * POSPrinter构造
     *
     * @param portType     端口类型
     * @param ethernetIP   网络IP地址
     * @param ethernetPort 网口端口号
     */
    public POSPrinter(PortType portType, String ethernetIP, int ethernetPort) {
        this.mPortInfo.setPortType(portType);
        this.mPortInfo.setEthernetIP(ethernetIP);
        this.mPortInfo.setEthernetPort(ethernetPort);
    }

    /**
     * 连接并打开端口
     */
    public void connect() throws PrinterException {
        switch (this.mPortInfo.getPortType()) {
            case USB:
                this.connectUSB(this.mPortInfo.getContext(), this.mPortInfo.getUsbPathName());
                break;
            case Bluetooth:
                this.connectBluetooth(this.mPortInfo.getBluetoothId());
                break;
            case Ethernet:
                this.connectNet(this.mPortInfo.getEthernetIP(), this.mPortInfo.getEthernetPort());
                break;
            default:
                throw new PrinterException("未找到相应的打印机连接设备");
        }
    }

    /**
     * 重置端口连接
     */
    private void resetPort() {
        if (this.mPortInfo != null) {
            this.mPortInfo = null;
        }

        this.mPortInfo = new PortInfo();
        if (this.mPort != null) {
            this.mPort.closePort();
            this.mPort = null;
        }
    }

    /**
     * 连接USB端口打印机
     *
     * @param context     context
     * @param usbPathName usb路径名称
     */
    private void connectUSB(Context context, String usbPathName) throws PrinterException {
        this.resetPort();

        if (context == null) {
            throw new PrinterException("Context不能为空");
        }

        if (usbPathName == null) {
            throw new PrinterException("usbPathName 不能为空");
        }

        this.mPortInfo.setContext(context);
        this.mPortInfo.setPortType(PortType.USB);
        this.mPortInfo.setUsbPathName(usbPathName);
        this.mPort = new USBPort(this.mPortInfo);

        ReturnMessage result = this.mPort.openPort();
        if (result == null) {
            throw new PrinterException("未知打印机异常");
        }

        if (!ErrorCode.OpenPortSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("连接打印机失败，请检查[%s]设备配置是否正确", usbPathName));
        }
    }

    /**
     * 连接蓝牙端口打印机
     *
     * @param bluetoothID 蓝牙id
     */
    private void connectBluetooth(String bluetoothID) throws PrinterException {
        this.resetPort();

        if (!BluetoothAdapter.checkBluetoothAddress(bluetoothID)) {
            throw new PrinterException("蓝牙ID错误");
        }

        this.mPortInfo.setBluetoothId(bluetoothID);
        this.mPortInfo.setPortType(PortType.Bluetooth);
        this.mPort = new BluetoothPort(this.mPortInfo);

        ReturnMessage result = this.mPort.openPort();
        if (result == null) {
            throw new PrinterException("未知打印机异常");
        }

        if (!ErrorCode.OpenPortSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("连接蓝牙端口失败，请检查[%s]设备配置是否正确", bluetoothID));
        }
    }

    /**
     * 连接网络端口打印机
     *
     * @param ip   IP地址
     * @param port 网络端口
     */
    private void connectNet(String ip, int port) throws PrinterException {
        this.resetPort();

        if (port <= 0) {
            throw new PrinterException("网络打印机端口号错误");
        }

        try {
            Inet4Address.getByName(ip);
        } catch (UnknownHostException e) {
            throw new PrinterException(String.format("找不到%s:%s的地址", ip, port));
        }

        this.mPortInfo.setEthernetPort(port);
        this.mPortInfo.setEthernetIP(ip);
        this.mPortInfo.setPortType(PortType.Ethernet);
        this.mPort = new EthernetPort(this.mPortInfo);

        ReturnMessage result = this.mPort.openPort();
        if (result == null) {
            throw new PrinterException("未知打印机异常");
        }

        if (!ErrorCode.OpenPortSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("连接IP地址为%s:%s的打印机失败，请检查网络或IP配置是否正确", ip, port));
        }
    }

    /**
     * 处理打印机写入数据结果
     */
    private void handleWriteResult(ReturnMessage result) throws PrinterException {
        if (result == null) {
            throw new PrinterException(String.format("设备为[%s]出现未知打印机异常", this.getPrinterLinkName()));
        }

        if (!ErrorCode.WriteDataSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("向[%s]打印机写入数据失败", this.getPrinterLinkName()));
        }
    }

    /**
     * 获取打印机连接名称
     */
    public String getPrinterLinkName() {
        if (this.mPortInfo == null) {
            return "";
        }

        switch (this.mPortInfo.getPortType()) {
            case USB:
                return mPortInfo.getUsbPathName();
            case Ethernet:
                return String.format("%s:%s", mPortInfo.getEthernetIP(), mPortInfo.getEthernetPort());
            case Bluetooth:
                return mPortInfo.getBluetoothId();
            case Unknown:
            default:
                return "";
        }
    }

    /**
     * 写入数据
     *
     * @param data 数据
     */
    public void write(byte[] data) throws PrinterException {
        if (data == null || data.length <= 0) {
            return;
        }
        this.handleWriteResult(this.mPort.write(data));
    }

    /**
     * 写入数据到当前已连接打印机
     *
     * @param data 写入数据
     */
    public void write(List<byte[]> data) throws PrinterException {
        if (data == null || data.size() <= 0) {
            return;
        }

        for (byte[] item : data) {
            this.write(item);
        }
    }

    /**
     * 向端口写入数据
     */
    public void write(int data) throws PrinterException {
        data &= 255;
        this.handleWriteResult(this.mPort.write(data));
    }

    /**
     * 向端口写入数据
     */
    public void write(byte[] data, int offset, int count) throws PrinterException {
        if (data == null || data.length <= 0) {
            return;
        }
        this.handleWriteResult(this.mPort.write(data, offset, count));
    }

    /**
     * 读取端口数据
     */
    public void read(byte[] buffer) throws PrinterException {
        this.read(buffer, 0, buffer.length);
    }

    /**
     * 读取端口数据
     */
    public void read(byte[] buffer, int offset, int count) throws PrinterException {
        ReturnMessage result = this.mPort.read(buffer, offset, count);
        if (result == null) {
            throw new PrinterException(String.format("设备为[%s]出现未知打印机异常", this.getPrinterLinkName()));
        }

        if (!ErrorCode.ReadDataSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("读取[%s]打印机端口数据失败", this.getPrinterLinkName()));
        }
    }

    /**
     * 读取端口数据
     */
    public int read() throws PrinterException {
        byte[] tem = new byte[1];
        this.read(tem, 0, 1);
        return tem[0];
    }

    /**
     * 获取连接端口信息
     */
    public PortInfo getPortInfo() {
        this.mPortInfo.setOpened(this.mPort.portIsOpen());
        return this.mPortInfo;
    }

    /**
     * 检查打印机连接状态
     *
     * @return 是否已连接(耗时可能较长)
     */
    public boolean checkLinkedState() {
        if (this.mPortInfo == null || this.mPort == null) {
            return false;
        }
        return this.getPortInfo().isOpened();
    }

    /**
     * 断开连接
     */
    public void disconnect() throws PrinterException {
        if (this.mPort == null) {
            throw new PrinterException(String.format("设备为[%s]没有已打开的端口", this.getPrinterLinkName()));
        }

        ReturnMessage result = this.mPort.closePort();
        if (result == null) {
            throw new PrinterException(String.format("设备为[%s]出现未知打印机异常", this.getPrinterLinkName()));
        }

        if (!ErrorCode.ClosePortSuccess.equals(result.getErrorCode())) {
            throw new PrinterException(String.format("关闭[%s]打印机端口发生异常", this.getPrinterLinkName()));
        }

        this.mPort = null;
    }
}
