package com.richard.printer.enumerate;

/**
 * author Richard
 * date 2020/8/18 10:49
 * version V1.0
 * description: 打印机相关错误码
 */
public enum ErrorCode {
    OpenPortFailed,
    OpenPortSuccess,
    ClosePortFailed,
    ClosePortSuccess,
    WriteDataFailed,
    WriteDataSuccess,
    ReadDataSuccess,
    ReadDataFailed,
    UnknownError;
}
