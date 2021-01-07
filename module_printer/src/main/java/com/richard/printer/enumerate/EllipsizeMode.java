package com.richard.printer.enumerate;

/**
 * author Richard
 * date 2020/12/30 14:23
 * version V1.0
 * description: 列文本显示模式
 */
public enum EllipsizeMode {

    /**
     * 无，完整显示（但是格式会参差不齐）
     */
    NONE,

    /**
     * 文本一行若显示不完，则结尾处以省略号代替
     */
    ELLIPSIS,

    /**
     * 文本显示不完时，则另起一行单独显示
     */
    LINE;

}
