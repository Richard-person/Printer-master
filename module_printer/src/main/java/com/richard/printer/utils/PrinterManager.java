package com.richard.printer.utils;

import android.content.Context;

import com.richard.printer.enumerate.PortType;
import com.richard.printer.exception.PrinterException;

import androidx.collection.SimpleArrayMap;

/**
 * author Richard
 * date 2021/1/5 14:41
 * version V1.0
 * description: 打印机管理类
 */
public final class PrinterManager {

    private static PrinterManager helper;
    private final SimpleArrayMap<String, POSPrinter> printerMap = new SimpleArrayMap<>();

    private PrinterManager() {
    }

    public static PrinterManager get() {
        if (helper == null) {
            synchronized (PrinterManager.class) {
                if (helper == null) {
                    helper = new PrinterManager();
                }
            }
        }

        return helper;
    }

    /**
     * 连接网络打印机
     *
     * @param ip   IP 地址
     * @param port 端口
     */
    public POSPrinter connectNetPort(String ip, int port) throws PrinterException {
        String key = String.format("%s:%s", ip, port);
        POSPrinter printer = printerMap.get(key);
        if (printer == null) {
            printer = new POSPrinter(PortType.Ethernet, ip, port);
            printerMap.put(key, printer);
        }
        printer.connect();

        return printer;
    }

    /**
     * 连接蓝牙打印机
     *
     * @param bluetoothID 蓝牙ID
     */
    public POSPrinter connectBTPort(String bluetoothID) throws PrinterException {
        POSPrinter printer = printerMap.get(bluetoothID);
        if (printer == null) {
            printer = new POSPrinter(PortType.Bluetooth, bluetoothID);
            printerMap.put(bluetoothID, printer);
        }
        printer.connect();

        return printer;
    }

    /**
     * 连接USB打印机
     *
     * @param context     context
     * @param usbPathName USB路径名称
     */
    public POSPrinter connectUSBPort(Context context, String usbPathName) throws PrinterException {
        POSPrinter printer = printerMap.get(usbPathName);
        if (printer == null) {
            printer = new POSPrinter(PortType.USB, context, usbPathName);
            printerMap.put(usbPathName, printer);
        }
        printer.connect();

        return printer;
    }

    /**
     * 断开所有打印机连接
     */
    public void disconnectAll() {
        try {
            for (int i = 0; i < printerMap.size(); i++) {
                printerMap.valueAt(i).disconnect();
            }
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}
