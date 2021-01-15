package com.richard.printer.enumerate;

/**
 * author Richard
 * date 2020/12/30 14:23
 * version V1.0
 * description: 列文本显示模式
 */
public enum EllipsizeMode {

    /**
     * 文本一行若显示不完，则结尾处以省略号代替
     */
    ELLIPSIS,

    /**
     * 文本显示不完时，则单独行跨列显示
     */
    LINE,

    /**
     * 当某一列显示不完时，则在该列多行显示
     */
    COLUMN_LINE;

}
