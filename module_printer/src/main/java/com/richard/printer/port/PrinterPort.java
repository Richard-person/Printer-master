package com.richard.printer.port;

import com.richard.printer.model.PortInfo;
import com.richard.printer.model.ReturnMessage;

import java.util.Queue;

/**
 * author Richard
 * date 2020/8/18 10:31
 * version V1.0
 * description: 打印机抽象类
 */
public abstract class PrinterPort {

    protected PortInfo mPortInfo = null;
    protected Queue<Byte> mRxdQueue = null;
    protected Queue<Byte> mTxdQueue = null;
    protected boolean mIsOpen = false;

    public PrinterPort(PortInfo portInfo) {
        this.mPortInfo = portInfo;
    }

    public abstract int read();

    public abstract boolean portIsOpen();

    public abstract ReturnMessage openPort();

    public abstract ReturnMessage closePort();

    public abstract ReturnMessage write(int data);

    public abstract ReturnMessage write(byte[] data);

    public abstract ReturnMessage write(byte[] data, int offset, int count);

    public abstract ReturnMessage read(byte[] buffer, int offset, int count);

    public abstract ReturnMessage read(byte[] data);

    public int getRxdCount() {
        return this.mRxdQueue != null ? this.mRxdQueue.size() : 0;
    }

    public int getTxdCount() {
        return this.mTxdQueue != null ? this.mTxdQueue.size() : 0;
    }

}
