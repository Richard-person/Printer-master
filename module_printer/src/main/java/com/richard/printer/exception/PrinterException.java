package com.richard.printer.exception;

/**
 * author Richard
 * date 2020/8/10 10:10
 * version V1.0
 * description: 自定义打印机相关异常
 */
public class PrinterException extends Exception{

    public PrinterException(String message){
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
