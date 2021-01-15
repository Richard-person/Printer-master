package com.richard.printer.enumerate;

/**
 * author Richard
 * date 2020/12/30 14:23
 * version V1.0
 * description: 列文本显示模式
 */
public enum EllipsizeMode {

    /**
     * 文本在该列显示不完时，则结尾处以省略号代替
     */
    ELLIPSIS,

    /**
     * 文本在该列显示不完时，则单独行并跨列显示
     */
    LINE,

    /**
     * 文本在该列显示不完时，则在该列多行显示
     */
    COLUMN_LINE;

}
