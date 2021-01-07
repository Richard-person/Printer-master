package com.richard.printer.utils;

import com.richard.printer.command.PrinterCmd;
import com.richard.printer.command.PrinterCmdUtil;
import com.richard.printer.enumerate.Align;
import com.richard.printer.enumerate.TicketSpec;
import com.richard.printer.model.ColumnItem;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import static com.richard.printer.enumerate.EllipsizeMode.NONE;

/**
 * author Richard
 * date 2020/8/18 16:28
 * version V1.0
 * description: 打印机打印参数构造
 * 注：目前只适配了58和80规格的小票
 */
public class PrintParams extends ArrayList<byte[]> {

    private static final long serialVersionUID = 5569224436177287937L;

    /**
     * 空白占位符
     */
    private final String PLACE_CHAR = "\u0020";

    /**
     * byte 编码格式
     */
    private static final String BYTE_CHARSET = "GBK";

    /**
     * 小票规格，默认80
     */
    private TicketSpec spec = TicketSpec.SPEC_80;

    public PrintParams(TicketSpec spec) {
        this.spec = spec;
        this.add(PrinterCmdUtil.setPrintSpec(spec));
    }

    /**
     * 获取小票规格
     */
    public TicketSpec getSpec() {
        return spec;
    }

    /**
     * 添加元素
     */
    public boolean add(byte[] item) {
        if (item == null || item.length <= 0) {
            return false;
        }
        return super.add(item);
    }

    /**
     * 添加元素
     */
    public boolean add(String item) {
        if (item == null) {
            return false;
        }
        return this.add(PrintParams.getByte(item));
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize) {
        this.add(item, fontSize, false);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     */
    public void add(byte[] item, @IntRange(from = 0, to = 1) int fontSize) {
        this.add(item, fontSize, false, null);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize, boolean isBold) {
        this.add(PrintParams.getByte(item), fontSize, isBold, null);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     * @param align    内容对齐方式
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize, boolean isBold, Align align) {
        this.add(PrintParams.getByte(item), fontSize, isBold, align);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     * @param align    内容对齐方式
     */
    public void add(byte[] item, @IntRange(from = 0, to = 1) int fontSize, boolean isBold, Align align) {
        //设置字体大小
        this.add(PrinterCmdUtil.fontSizeSetBig(fontSize));

        //字体加粗
        if (isBold) {
            this.add(PrinterCmdUtil.emphasizedOn());
        } else {
            this.add(PrinterCmdUtil.emphasizedOff());
        }

        //对齐方式
        if (align == null) {
            align = Align.LEFT;
        }
        int lineMaxLength = this.getLineMaxLength(fontSize);
        int spaceCount;
        switch (align) {
            case CENTER:
                spaceCount = (lineMaxLength - item.length) / 2;
                break;
            case RIGHT:
                spaceCount = lineMaxLength - item.length;
                break;
            case LEFT:
            default:
                spaceCount = 0;
        }
        for (int i = 0; i < spaceCount; i++) {
            this.add(PLACE_CHAR);
        }

        this.add(item);
    }

    /**
     * 添加换行
     */
    public void addNextRow() {
        super.add(PrinterCmd.printLineFeed());
    }

    /**
     * 添加分隔线
     *
     * @param fontSize    分割线倍数值
     * @param isAloneLine 是否单独一行显示
     */
    public void addSplitLine(@IntRange(from = 0, to = 1) int fontSize, boolean isAloneLine) {
        if (isAloneLine) {
            this.addNextRow();
        }
        int length = this.getLineMaxLength(fontSize);
        for (int index = 0; index < length; index++) {
            this.add(new byte[]{45}, fontSize);
        }
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(fontSize, widthWeigh, columns);
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, boolean isBold, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(fontSize, isBold, widthWeigh, columns);
    }

    /**
     * 添加一行
     *
     * @param fontSize   字体倍数值（仅支持0-1）
     * @param widthWeigh 列占宽权重，widthWeigh数量和columns数量必须一致
     * @param columns    列文本，widthWeigh数量和columns数量必须一致
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, @NonNull float[] widthWeigh, @NonNull String... columns) {
        this.addRow(fontSize, false, widthWeigh, columns);
    }

    /**
     * 添加一行
     *
     * @param fontSize   字体倍数值（仅支持0-1）
     * @param isBold     是否加粗
     * @param widthWeigh 列占宽权重，widthWeigh数量和columns数量必须一致
     * @param columns    列文本，widthWeigh数量和columns数量必须一致
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, boolean isBold, @NonNull float[] widthWeigh, @NonNull String... columns) {
        if (widthWeigh.length != columns.length) {
            throw new IllegalArgumentException("widthWeigh 或者 columns的元素数量必须一致");
        }

        int lineMaxLength = this.getLineMaxLength(fontSize);
        float totalColumnWeigh = 0;
        for (float item : widthWeigh) {
            totalColumnWeigh += item;
        }

        //换行
        this.addNextRow();

        //添加内容
        for (int index = 0; index < columns.length; index++) {
            String columnItem = columns[index];
            int columnTextLength = this.getBytesLength(columnItem);

            //该列分配总长度
            int allocColumnLength = (int) Math.floor(widthWeigh[index]
                    / (totalColumnWeigh * 1F) * lineMaxLength);

            //添加列文本内容
            this.addColumn(columnItem, columnTextLength, allocColumnLength, lineMaxLength, fontSize, isBold);

            if (columns.length > 1 && index < columns.length - 1 && columnTextLength >= allocColumnLength) {
                this.addNextRow();

                //占满左边空白列
                int leftPadLength = index + 1;
                for (int lineIndex = 0; lineIndex < leftPadLength; lineIndex++) {
                    int leftPlaceholderLength = (int) Math.floor(widthWeigh[lineIndex]
                            / (totalColumnWeigh * 1F) * lineMaxLength);
                    for (int i = 0; i < leftPlaceholderLength; i++) {
                        this.add(PLACE_CHAR);
                    }
                }
                continue;
            }

            //添加该列右边占位符
            if (index < columns.length - 1) {
                for (int i = 0; i < (allocColumnLength - columnTextLength); i++) {
                    this.add(PLACE_CHAR);
                }
            }
        }
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, ColumnItem... columns) {
        int lineMaxLength = this.getLineMaxLength(fontSize);
        float totalColumnWeigh = 0;
        for (ColumnItem item : columns) {
            totalColumnWeigh += item.getWidthWeigh();
        }

        //换行
        this.addNextRow();

        //添加内容
        for (int index = 0; index < columns.length; index++) {
            ColumnItem columnItem = columns[index];
            int columnTextLength = this.getBytesLength(columnItem.getText());

            //该列分配总长度
            int allocColumnLength = (int) Math.floor(columnItem.getWidthWeigh()
                    / (totalColumnWeigh * 1F) * lineMaxLength);

            //添加列文本内容
            if (columnItem.getEllipsizeMode() != NONE && columnTextLength >= allocColumnLength) {
                switch (columnItem.getEllipsizeMode()) {
                    case LINE:
                        //添加列文本内容
                        this.addColumn(columnItem.getText(), columnTextLength, allocColumnLength, lineMaxLength, fontSize, columnItem.isBold());

                        if (columns.length > 1 && index < columns.length - 1) {
                            this.addNextRow();
                            //占满左边空白列
                            int leftPadLength = index + 1;
                            for (int lineIndex = 0; lineIndex < leftPadLength; lineIndex++) {
                                int leftPlaceholderLength = (int) Math.floor(columns[lineIndex]
                                        .getWidthWeigh() / (totalColumnWeigh * 1F) * lineMaxLength);
                                for (int i = 0; i < leftPlaceholderLength; i++) {
                                    this.add(PLACE_CHAR);
                                }
                            }
                        }
                        continue;
                    case ELLIPSIS:
                        String columnText = columnItem.getText().substring(0,
                                (int) Math.floor(allocColumnLength / 2D) - 2).concat("...");
                        this.add(columnText, fontSize, columnItem.isBold());
                        columnTextLength = this.getBytesLength(columnText);
                        break;
                }
            } else {
                this.addColumn(columnItem.getText(), columnTextLength, allocColumnLength, lineMaxLength, fontSize, columnItem.isBold());
            }

            //添加该列右边占位符
            if (index < columns.length - 1) {
                for (int i = 0; i < (allocColumnLength - columnTextLength); i++) {
                    this.add(PLACE_CHAR);
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 添加列（兼容了长文本多行显示）
     *
     * @param text              文本
     * @param columnTextLength  文本实际占用字节长度
     * @param allocColumnLength 分配的列最大字节长度
     * @param lineMaxLength     一行最大显示字节长度
     * @param fontSize          字体放大倍数
     * @param isBold            是否加粗
     */
    private void addColumn(String text, int columnTextLength, int allocColumnLength, int lineMaxLength, int fontSize, boolean isBold) {
        //添加列文本内容
        if (columnTextLength > allocColumnLength) {
            List<String> splitTextList = StringUtil.substring(text, BYTE_CHARSET, lineMaxLength);
            for (int i = 0, size = splitTextList.size(); i < size; i++) {
                this.add(splitTextList.get(i), fontSize, isBold);
                if (i < size - 1) {
                    this.addNextRow();
                }
            }
        } else {
            this.add(text, fontSize, isBold, null);
        }
    }

    /**
     * 获取text的byte数组
     */
    private static byte[] getByte(String text) {
        if (text == null) {
            return null;
        }
        try {
            return text.getBytes(BYTE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据长度
     *
     * @param text 文本
     */
    private int getBytesLength(String text) {
        return text.getBytes(Charset.forName(BYTE_CHARSET)).length;
    }

    /**
     * 获取一行最大字符数
     * 58mm票据打印机：一行可以打印16个汉字，32个字符；80mm票据打印机,一行可以打印24个汉字,48个字符；421D标签打印机，一行可打印34个汉字，69个字符。
     *
     * @param fontSize 目前只适配了0-1的字体倍数
     * @return 最大字符数(以半角字符为基础 ， 比如 ： 中文单个字符长度为2 ， 数字字母长度为1)
     */
    public int getLineMaxLength(@IntRange(from = 0, to = 1) int fontSize) {
        int maxLineLength = 0;
        switch (spec) {
            case SPEC_58:
                switch (fontSize) {
                    case 1:
                        maxLineLength = 16;
                        break;
                    case 0:
                    default:
                        maxLineLength = 32;
                }
                break;
            case SPEC_80:
            default:
                switch (fontSize) {
                    case 1:
                        maxLineLength = 24;
                        break;
                    case 0:
                    default:
                        maxLineLength = 48;
                }
                break;
        }

        return maxLineLength;
    }
}