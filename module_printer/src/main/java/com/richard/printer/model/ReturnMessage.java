package com.richard.printer.model;

import com.richard.printer.enumerate.ErrorCode;

/**
 * author Richard
 * date 2020/8/18 11:03
 * version V1.0
 * description: 打印机结果消息
 */
public class ReturnMessage {
    private ErrorCode mErrorCode;
    private String mErrorStrings;
    private int mReadBytes;
    private int mWriteBytes;

    public ReturnMessage() {
        this.mReadBytes = -1;
        this.mWriteBytes = -1;
        this.mErrorCode = ErrorCode.UnknownError;
        this.mErrorStrings = "Unknown error\n";
        this.mReadBytes = -1;
        this.mWriteBytes = -1;
    }

    public ReturnMessage(ErrorCode ec, String es) {
        this.mReadBytes = -1;
        this.mWriteBytes = -1;
        this.mErrorCode = ec;
        this.mErrorStrings = es;
    }

    public ReturnMessage(ErrorCode ec, String es, int count) {
        this.mReadBytes = -1;
        this.mWriteBytes = -1;
        this.mErrorCode = ec;
        this.mErrorStrings = es;
        switch (ec) {
            case ReadDataSuccess:
                this.mWriteBytes = count;
                break;
            case ReadDataFailed:
                this.mReadBytes = count;
        }

    }

    public ErrorCode getErrorCode() {
        return this.mErrorCode;
    }

    public String getErrorStrings() {
        return this.mErrorStrings;
    }

    public int getReadByteCount() {
        return this.mReadBytes;
    }

    public int getWriteByteCount() {
        return this.mWriteBytes;
    }

}
