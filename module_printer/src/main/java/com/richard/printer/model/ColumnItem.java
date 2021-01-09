package com.richard.printer.model;

import com.richard.printer.enumerate.Align;
import com.richard.printer.enumerate.EllipsizeMode;

import java.io.Serializable;

/**
 * author Richard
 * date 2020/8/19 16:08
 * version V1.0
 * description: 打印列 信息
 */
public class ColumnItem implements Serializable {

    private static final long serialVersionUID = -6867565424687687621L;

    /**
     * item名称
     */
    private String text;

    /**
     * 列宽占比
     */
    private float widthWeigh = 1;

    /**
     * 是否加粗
     */
    private boolean isBold;

    /**
     * 文本显示模式,详见EllipsizeMode
     * @see EllipsizeMode
     */
    private EllipsizeMode ellipsizeMode = EllipsizeMode.LINE;

    /**
     * 对齐方式 详见Align
     * @see Align
     */
    private Align align = Align.LEFT;

    public ColumnItem(String text) {
        this.text = text;
    }

    public ColumnItem(String text, float widthWeigh) {
        this.text = text;
        this.widthWeigh = widthWeigh;
    }

    public ColumnItem(String text, float widthWeigh,Align align) {
        this.text = text;
        this.widthWeigh = widthWeigh;
        this.align = align;
    }

    public ColumnItem(String text, float widthWeigh, boolean isBold) {
        this.text = text;
        this.widthWeigh = widthWeigh;
        this.isBold = isBold;
    }

    public ColumnItem(String text, float widthWeigh, boolean isBold, EllipsizeMode ellipsizeMode) {
        this.text = text;
        this.widthWeigh = widthWeigh;
        this.isBold = isBold;
        this.ellipsizeMode = ellipsizeMode;
    }

    public ColumnItem(String text, float widthWeigh, boolean isBold, EllipsizeMode ellipsizeMode, Align align) {
        this.text = text;
        this.widthWeigh = widthWeigh;
        this.isBold = isBold;
        this.ellipsizeMode = ellipsizeMode;
        this.align = align;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getWidthWeigh() {
        return widthWeigh;
    }

    public void setWidthWeigh(float widthWeigh) {
        this.widthWeigh = widthWeigh;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public EllipsizeMode getEllipsizeMode() {
        return ellipsizeMode;
    }

    public void setEllipsizeMode(EllipsizeMode ellipsizeMode) {
        this.ellipsizeMode = ellipsizeMode;
    }
}
