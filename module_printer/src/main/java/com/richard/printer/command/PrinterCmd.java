/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.richard.printer.command;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;

/**
 * ESC-POS Commands
 */
public final class PrinterCmd {

    private static String charsetName = "GBK";

    private static final byte DLE = 16;
    private static final byte ESC = 27;
    private static final byte FS = 28;
    private static final byte GS = 29;

    /**
     * 设置字符编码
     */
    public static void setCharsetName(String charset) {
        charsetName = charset;
    }

    /**
     * 打开钱箱
     *
     * @return command
     */
    public static byte[] openCashBoxRealtime(int m, int t) {
        return new byte[]{DLE, 20, 1, (byte) m, (byte) t};
    }

    /**
     * 执行打印数据保存转换为十六进制
     *
     * @return command
     */
    public static byte[] executePrintDataSaveByTransformToHex() {
        return new byte[]{GS, 40, 65, 2, 0, 0, 1};
    }

    public static byte[] printRasterBmp(int m, Bitmap bitmap, BitmapToByteData.BmpType bmpType, BitmapToByteData.AlignType alignType, int pageWidth) {
        return BitmapToByteData.rasterBmpToSendData(m, bitmap, bmpType, alignType, pageWidth);
    }

    /**
     * 选择中文字符模型
     *
     * @return command
     */
    public static byte[] selectChineseCharModel() {
        return new byte[]{FS, 38};
    }

    /**
     * 选择或取消行模式下的中文字符
     *
     * @return command
     */
    public static byte[] selectOrCancelChineseCharUnderLineModel(int n) {
        return new byte[]{FS, 45, (byte) n};
    }

    /**
     * 取消汉字模型
     *
     * @return command
     */
    public static byte[] CancelChineseCharModel() {
        return new byte[]{FS, 46};
    }

    /**
     * 自定义用户定义的中文字符
     *
     * @return command
     */
    public static byte[] definedUserDefinedChineseChar(int c2, byte[] b) {
        byte[] data = new byte[]{FS, 50, -2, (byte) c2};
        data = byteMerger(data, b);
        return data;
    }

    /**
     * 设置汉字的左右空格
     *
     * @return command
     */
    public static byte[] setChineseCharLeftAndRightSpace(int n1, int n2) {
        return new byte[]{FS, 83, (byte) n1, (byte) n2};
    }

    /**
     * 选择或取消中文字符双WH
     *
     * @return command
     */
    public static byte[] selectOrCancelChineseCharDoubleWH(int n) {
        return new byte[]{FS, 87, (byte) n};
    }

    /**
     * 打印机指令嗡嗡声提示
     *
     * @return command
     */
    public static byte[] printerOrderBuzzingHint(int n, int t) {
        return new byte[]{ESC, 66, (byte) n, (byte) t};
    }

    /**
     * 打印机命令嗡嗡声和警告灯
     *
     * @return command
     */
    public static byte[] printerOrderBuzzingAndWarningLight(int m, int t, int n) {
        return new byte[]{ESC, 67, (byte) m, (byte) t, (byte) n};
    }

    /**
     * 指定或取消各种PDF 417符号选项
     *
     * @return command
     */
    public static byte[] specifiesOrCancelsVariousPDF417SymbolOptions(int m) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 70, (byte) m};
    }

    /**
     * 在符号存储区域打印PDF 417符号数据
     *
     * @return command
     */
    public static byte[] printsThePDF417SymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 3, 0, 48, 81, 48};
    }

    /**
     * 在符号存储区PDF 417中传输符号数据的大小
     *
     * @return command
     */
    public static byte[] transmitsTheSizeOfTheSymbolDataInTheSymbolStorageAreaPDF417() {
        return new byte[]{GS, 40, 107, 3, 0, 48, 82, 48};
    }

    /**
     * 设置二维码符号模块的大小
     *
     * @return command
     */
    public static byte[] setsTheSizeOfTheQRCodeSymbolModule(int n) {
        return new byte[]{GS, 40, 107, 48, 103, (byte) n};
    }

    /**
     * 设置二维码符号的纠错等级
     *
     * @return command
     */
    public static byte[] setsTheErrorCorrectionLevelForQRCodeSymbol(int n) {
        return new byte[]{GS, 40, 107, 48, 105, (byte) n};
    }

    /**
     * 将符号数据存储在二维码符号存储区
     *
     * @return command
     */
    public static byte[] storesSymbolDataInTheQRCodeSymbolStorageArea(String code) {
        byte[] b = strToBytes(code);
        int a = b.length;
        int pL;
        int pH;
        if (a <= 255) {
            pL = a;
            pH = 0;
        } else {
            pH = a / 256;
            pL = a % 256;
        }

        byte[] data = new byte[]{GS, 40, 107, 48, -128, (byte) pL, (byte) pH};
        data = byteMerger(data, b);
        return data;
    }

    /**
     * 在符号存储区打印二维码符号数据
     *
     * @return command
     */
    public static byte[] printsTheQRCodeSymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 48, -127};
    }

    /**
     * 打印二维码
     *
     * @return command
     */
    public static byte[] printQRCode(int n, int errLevel, String code) {
        byte[] b = strToBytes(code);
        int a = b.length;
        int nL;
        int nH;
        if (a <= 255) {
            nL = a;
            nH = 0;
        } else {
            nH = a / 256;
            nL = a % 256;
        }

        byte[] data = new byte[]{GS, 40, 107, 48, 103, (byte) n, 29, 40, 107, 48, 105, (byte) errLevel, 29, 40, 107, 48, -128, (byte) nL, (byte) nH};
        data = byteMerger(data, b);
        byte[] c = new byte[]{GS, 40, 107, 48, -127};
        data = byteMerger(data, c);
        return data;
    }

    /**
     * 传送符号存储区QR码中符号数据的大小
     *
     * @return command
     */
    public static byte[] transmitsTheSizeOfTheSymbolDataInTheSymbolStorageAreaQRCode() {
        return new byte[]{GS, 40, 107, 3, 0, 49, 82, 48};
    }

    /**
     * 指定最大代码符号的模式
     *
     * @return command
     */
    public static byte[] specifiesTheModeForMaxiCodeSymbol(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 50, 65, (byte) n};
    }

    /**
     * 打印符号存储区域中的最大代码符号数据
     *
     * @return command
     */
    public static byte[] printsTheMaxiCodeSymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 3, 0, 50, 81, 48};
    }

    /**
     * 在符号存储区域最大码中传输被编码的符号数据的大小
     *
     * @return command
     */
    public static byte[] transmitsTheSizeOfTheEncodedSymbolDataInTheSymbolStorageAreaMaxiCode() {
        return new byte[]{GS, 40, 107, 3, 0, 50, 82, 48};
    }

    private static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private static byte[] strToBytes(String str) {
        byte[] b = null;
        byte[] data = null;

        try {
            b = str.getBytes("utf-8");
            if (charsetName == null | "".equals(charsetName)) {
                charsetName = "gbk";
            }

            data = (new String(b, "utf-8")).getBytes(charsetName);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return data;
    }

    /**
     * 选择Bmp模型
     *
     * @return command
     */
    public static byte[] selectBmpModel(int m, Bitmap bitmap, BitmapToByteData.BmpType bmpType) {
        return BitmapToByteData.baBmpToSendData(m, bitmap, bmpType);
    }

    /**
     * 打印头替换输入
     *
     * @return command
     */
    public static byte[] printHeadReplaceEnter() {
        return new byte[]{ESC, 60};
    }

    /**
     * @return command
     */
    public static byte[] printAndBackFeed(int n) {
        return new byte[]{ESC, 75, (byte) n};
    }

    /**
     * 选择或取消单向打印
     *
     * @return command
     */
    public static byte[] selectOrCancelUnidirectionPrint(int n) {
        return new byte[]{ESC, 85, (byte) n};
    }

    public static byte[] printAndFeedUnidirection(int n) {
        return new byte[]{ESC, 101, (byte) n};
    }

    /**
     * 选择打印颜色
     *
     * @return command
     */
    public static byte[] selectPrintColor(int n) {
        return new byte[]{ESC, 114, (byte) n};
    }

    /**
     * 设置连接等待时间
     *
     * @return command
     */
    public static byte[] setConnectWaitTime(int t1, int t2) {
        return new byte[]{GS, 122, 48, (byte) t1, (byte) t2};
    }

    /**
     * 取消汉字模型
     *
     * @return command
     */
    public static byte[] cancelChineseCharModel() {
        return new byte[]{FS, 46};
    }

    /**
     * 取消用户定义的中文字符
     *
     * @return command
     */
    public static byte[] cancelUserDefinedChineseChar(int c2) {
        return new byte[]{FS, 63, -2, (byte) c2};
    }

    /**
     * 设置黑色位置记录
     *
     * @return command
     */
    public static byte[] setBlackPositionRecord(int a, int m, int nL, int nH) {
        return new byte[]{GS, 40, 70, 4, 0, (byte) a, (byte) m, (byte) nL, (byte) nH};
    }

    /**
     * 送黑纸到印刷位置
     *
     * @return command
     */
    public static byte[] feedBlackPaperToPrintPosition() {
        return new byte[]{GS, 12};
    }

    /**
     * 设定回滚长度
     *
     * @param n 长度
     * @return command
     */
    public static byte[] setRollBackLength(int n) {
        return new byte[]{ESC, 94, (byte) n};
    }

    /**
     * 设置订单长度
     *
     * @return command
     */
    public static byte[] setOrderLength(int nL, int nH) {
        return new byte[]{ESC, 126, (byte) nL, (byte) nH};
    }

    /**
     * 送纸
     *
     * @return command
     */
    public static byte[] feedPaperToOrderEnd() {
        return new byte[]{ESC, 127};
    }

    /**
     * 打印标题记录和馈送打印开始位置
     *
     * @return command
     */
    public static byte[] printHeaderRecordAndFeedToPrintStartPosition() {
        return new byte[]{GS, 60};
    }

    /**
     * 打印代码128
     *
     * @param content 打印内容
     * @return command
     */
    public static byte[] printCode128(String content) {
        byte[] data = new byte[]{GS, 107, 73, 10, 123, 65, 48, 49, 50, 51, 52, 53, 54, 55};
        byte[] text = strToBytes(content);
        data = byteMerger(data, text);
        byte[] end = new byte[]{13, 10};
        data = byteMerger(data, end);
        return data;
    }

    /**
     * 查询打印机状态
     *
     * @return command
     */
    public static byte[] queryPrinterState() {
        return new byte[]{ESC, 118};
    }

    /**
     * 打开或关闭接收Ip中的标签模型
     *
     * @param open 是否打开
     * @return command
     */
    public static byte[] openOrCloseLabelModelInReceiveIp(Boolean open) {
        if (open) {
            return new byte[]{31, 27, 31, 0, 1, 1, -128, 1};
        } else {
            return new byte[]{31, 27, 31, 0, 1, 1, -128, 0};
        }
    }

    /**
     * 结束标签
     *
     * @return command
     */
    public static byte[] endOfLabel() {
        return new byte[]{31, 27, 31, 0, 1, 1, -127};
    }

    /**
     * 检查标签和间隙
     *
     * @return command
     */
    public static byte[] checkLabelAndGap() {
        return new byte[]{31, 27, 31, 0, 1, 1, -126};
    }

    /**
     * 设置标签宽度
     *
     * @param width 宽度
     * @return command
     */
    public static byte[] setTheLabelWidth(int width) {
        return new byte[]{31, 27, 31, 0, 1, 1, -125, (byte) width};
    }

    /**
     * 选择字体B
     *
     * @return command
     */
    public static byte[] selectFontB() {
        return new byte[]{31, 27, 31, 48, 1};
    }

    /**
     * 选择字体A
     *
     * @return command
     */
    public static byte[] selectFontA() {
        return new byte[]{ESC, 33, 0};
    }


    /**
     * 将打印位置移动到下一个标签位置。
     * HT
     *
     * @return command
     */
    public static byte[] horizontalTab() {
        return new byte[]{9};
    }

    /**
     * 在打印缓冲区中打印数据，并根据当前行距输入一行。
     * LF
     *
     * @return command
     */
    public static byte[] printLineFeed() {
        return new byte[]{10};
    }

    /**
     * (自动换行)启用自动换行时，此命令的功能与LF相同；禁用自动换行时，将忽略此命令。
     * CR
     *
     * @return command
     */
    public static byte[] printCarriageReturn() {
        return new byte[]{13};
    }

    /**
     * 在打印缓冲区中打印数据并返回到标准模式。
     * FF
     *
     * @return command
     */
    public static byte[] printReturnStandardMode() {
        return new byte[]{12};
    }

    /**
     * 在页面模式下，删除当前可打印区域中的所有打印数据。
     * CAN
     *
     * @return command
     */
    public static byte[] cancelPrintData() {
        return new byte[]{24};
    }

    /**
     * 根据以下参数，实时传输由n指定的所选打印机状态：
     * n=1 : Transmit printer status
     * n=2 : Transmit off-line status
     * n=3 : Transmit error status
     * n=4 : Transmit paper roll sensor status
     * DLE EOT n
     *
     * @param n 1≤n≤4
     * @return command
     */
    public static byte[] realTimeStatusTransmission(int n) {
        return new byte[]{DLE, 4, (byte) n};
    }

    /**
     * 响应主机的请求。 n指定请求如下：
     * n=1 : 从错误中恢复并从发生错误的行重新开始打印
     * n=2 : 清除接收和打印缓冲区后从错误中恢复
     * DLE ENQ n
     *
     * @param n 1≤n≤2
     * @return command
     */
    public static byte[] realTimeRequestToPrinter(int n) {
        return new byte[]{DLE, 5, (byte) n};
    }

    /**
     * 将t所指定的脉冲输出到连接器引脚m，如下所示：
     * m=0 : Drawer kick-out connector pin 2.
     * m=1 : Drawer kick-out connector pin 5.
     * The pulse ON time is [ t x 100 ms] and the OFF time is [ t x 100 ms].
     * DLE DC4 n m t
     *
     * @param n n=1
     * @param m m=0,1
     * @param t 1≤t≤8
     * @return command
     */
    public static byte[] generatePulseAtRealTime(int n, int m, int t) {
        return new byte[]{DLE, 20, (byte) n, (byte) m, (byte) t};
    }

    /**
     * 在页面模式下，将所有缓冲的数据集中打印在可打印区域中。
     * ESC FF
     *
     * @return command
     */
    public static byte[] printData() {
        return new byte[]{ESC, 12};
    }

    /**
     * 将字符右侧的字符间距设置为[n x水平或垂直运动单位]。
     * ESC SP n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] setRightSideCharacterSpacing(int n) {
        return new byte[]{ESC, 32, (byte) n};
    }

    /**
     * 使用n选择打印模式，如下所示：
     * n = 0：字体A（12x24）未选择强调模式。未选择双高模式。未选择双倍宽度模式。未选择下划线模式。
     * n = 1：字符字体B（9x24）
     * n = 8：选择了强调模式。
     * n = 16：选择了双高模式。
     * n = 32：选择了全角模式。
     * n = 128：选择下划线模式。
     * ESC ! n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] selectPrintMode(int n) {
        return new byte[]{ESC, 33, (byte) n};
    }

    /**
     * 设置从行首到要打印后续字符的位置的距离。从行首到打印位置的距离为[（nL + nH x 256）x（垂直或水平运动单位）]英寸。
     * ESC $ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setAbsolutePrintPosition(int nL, int nH) {
        return new byte[]{ESC, 36, (byte) nL, (byte) nH};
    }

    /**
     * 取消用户定义的字符集。
     * ESC % n
     *
     * @return command
     */
    public static byte[] cancelUserDefinedCharacterSet() {
        return new byte[]{ESC, 37, 0};
    }

    /**
     * 选择用户定义的字符集。
     * ESC % n
     *
     * @return command
     */
    public static byte[] selectUserDefinedCharacterSet() {
        return new byte[]{ESC, 37, 1};
    }

    /**
     * 定义用户定义的字符y指定垂直方向上的字节数，始终为3。c1指定定义的起始字符代码，而c2指定最终代码。 X指定水平方向上的点数。
     * ESC & y c1 c2 [x1 d1…d(y x x1)]..[ xk d1..d(y x xk)]
     *
     * @param c1   32≤c1≤c2≤126
     * @param c2   32≤c1≤c2≤126
     * @param dots 0 ≤ x ≤ 12 Font A (when font A (12 x 24) is selected)
     *             0 ≤ x ≤ 9 Font B (when font B (9 x 17) is selected)
     *             0 ≤ d1 ... d(y x xk) ≤ 255
     * @return command
     */
    public static byte[] defineUserDefinedCharacters(int c1, int c2, byte[] dots) {
        byte[] part = new byte[]{ESC, 38, 3, (byte) c1, (byte) c2};
        byte[] destination = new byte[part.length + dots.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(dots, 0, destination, part.length, dots.length);
        return destination;
    }

    /**
     * 使用m作为nL和nH指定的点数，选择位图像模式，如下所示：
     * m=0
     * Mode:8-dot single-density
     * Vertical NO. of Dots:8
     * Vertical Dot Density: 60 DPI
     * Horizontal Dot Density: 90 DPI
     * Number of (Data(K)):nL + nH x 256
     * m=1
     * Mode:8-dot double-density
     * Vertical NO. of Dots:8
     * Vertical Dot Density: 60 DPI
     * Horizontal Dot Density: 180 DPI
     * Number of (Data(K)):nL + nH x 256
     * m=32
     * Mode:24-dot single-density
     * Vertical NO. of Dots:24
     * Vertical Dot Density: 180 DPI
     * Horizontal Dot Density: 90 DPI
     * Number of (Data(K)):(nL + nH x 256) x 3
     * m=33
     * Mode:24-dot single-density
     * Vertical NO. of Dots:24
     * Vertical Dot Density: 180 DPI
     * Horizontal Dot Density: 180 DPI
     * Number of (Data(K)):(nL + nH x 256) x 3
     * ESC * m nL nH [d1...dk]
     *
     * @param m     m = 0, 1, 32, 33
     * @param nL    0≤nL ≤255
     * @param nH    0≤nH ≤3
     * @param image 0≤d≤255
     * @return command
     */
    public static byte[] selectBitImageMode(int m, int nL, int nH, byte[] image) {
        byte[] part = new byte[]{ESC, 42, (byte) m, (byte) nL, (byte) nH};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * 关闭下划线模式
     * ESC - n
     *
     * @return command
     */
    public static byte[] turnUnderlineModeOff() {
        return new byte[]{ESC, 45, 0};
    }

    /**
     * 根据以下n值打开或关闭下划线模式。
     * n=0, 48 : Turns off underline mode
     * n=1, 49 : Turns on underline mode (1-dot thick)
     * n=2, 50 : Turns on underline mode (2-dots thick)
     * ESC - n
     *
     * @param n 0≤n ≤2, 48≤n ≤50
     * @return command
     */
    public static byte[] turnUnderlineMode(int n) {
        return new byte[]{ESC, 45, 1};
    }

    /**
     * 选择大约4.23 mm{16"}间距。
     * ESC 2
     *
     * @return command
     */
    public static byte[] selectDefaultLineSpacing() {
        return new byte[]{ESC, 50};
    }

    /**
     * 设置线间距为[n x(垂直或水平移动单位)]英寸。
     * ESC 3 n
     *
     * @param n 0≤n≤255
     * @return command
     */
    public static byte[] setLineSpacing(int n) {
        return new byte[]{ESC, 51, (byte) n};
    }

    /**
     * 选择主机向其发送数据的设备，使用n如下:
     * n=0 : Printer disabled
     * n=1 : Printer enabled
     * ESC = n
     *
     * @param n 1≤n≤255 default 1
     * @return command
     */
    public static byte[] setPeripheralDevice(int n) {
        return new byte[]{ESC, 61, (byte) n};
    }

    /**
     * 取消用户定义的字符。
     * ESC ? n
     *
     * @param n 32 ≤n ≤126
     * @return command
     */
    public static byte[] cancelUserDefinedCharacters(int n) {
        return new byte[]{ESC, 63, (byte) n};
    }

    /**
     * 清除打印缓冲区中的数据，并将打印机模式重置为电源打开时的模式。
     * ESC @
     *
     * @return command
     */
    public static byte[] initializePrinter() {
        return new byte[]{ESC, 64};
    }

    /**
     * 设置是水平制表符位置。n指定用于从行首设置水平制表符位置的列号。k表示要设置的水平选项卡位置的总数。
     * ESC D [n1...nk] NUL
     *
     * @param nk 1≤n ≤255
     *           0≤k ≤32
     * @return command
     */
    public static byte[] setHorizontalTabPositions(byte[] nk) {
        byte[] part = new byte[]{ESC, 68};
        byte[] destination = new byte[part.length + nk.length + 1];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(nk, 0, destination, part.length, nk.length);
        destination[part.length + nk.length] = 0;
        return destination;
    }

    /**
     * 关闭强调模式。
     * ESC E n
     *
     * @return command
     */
    public static byte[] turnOffEmphasizedMode() {
        return new byte[]{ESC, 69, 0};
    }

    /**
     * 打开强调模式。
     * ESC E n
     *
     * @return command
     */
    public static byte[] turnOnEmphasizedMode() {
        return new byte[]{ESC, 69, 1};
    }

    /**
     * 打开或关闭双strike模式，当n的LSB为0时关闭双strike模式，当n的LSB为1时打开双strike模式。
     * ESC G n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] turnDoubleStrikeMode(int n) {
        return new byte[]{ESC, 71, (byte) n};
    }

    /**
     * 在打印缓冲中打印数据并送纸[n x垂直或水平运动装置]。
     * ESC J n
     *
     * @param n 0≤n ≤255
     * @return command
     */
    public static byte[] printFeedPaper(int n) {
        return new byte[]{ESC, 74, (byte) n};
    }

    /**
     * 从标准模式切换到页面模式。
     * ESC L
     *
     * @return command
     */
    public static byte[] selectPageMode() {
        return new byte[]{ESC, 76};
    }

    /**
     * 选择字体样式
     * n=0, 48 : Character font A (12 X 24 ) Selected
     * n=1, 49 : Character font B (9 X 24 ) Selected
     * ESC M n
     *
     * @param n n= 0, 1 , 48, 49
     * @return command
     */
    public static byte[] selectCharacterFont(int n) {
        return new byte[]{ESC, 77, (byte) n};
    }

    /**
     * 从下面选择一个国际字符集n:
     * n=0 : U. S. A
     * n=1 : France
     * n=2 : Germany
     * n=3 : U. K.
     * n=4 : Denmark I
     * n=5 : Sweden
     * n=6 : Italy
     * n=7 : Spain I
     * n=8 : Japan
     * n=9 : Norway
     * n=10 : Denmark II
     * n=11 : Spain II
     * n=12 : Latin America
     *
     * @param n 0≤n≤13 default 0
     * @return command
     */
    public static byte[] selectAnInternationalCharacterSet(int n) {
        return new byte[]{ESC, 82, (byte) n};
    }

    /**
     * 从页面模式切换到标准模式。
     * ESC S
     *
     * @return command
     */
    public static byte[] selectStandardMode() {
        return new byte[]{ESC, 83};
    }

    /**
     * 在页面模式下选择打印方向和开始位置。n指定打印方向和开始位置如下:
     * n=0, 48 : 从左到右(打印方向)左上角(起始位置)
     * n=1, 49 : 从下到上(打印方向)左下角(起始位置)
     * n=2, 50 : 从右到左(打印方向)右下(起始位置)
     * n=3, 51 : 从上到下(打印方向)右上角(起始位置)
     * ESC T n
     *
     * @param n 0≤n ≤3,
     *          48≤n ≤51
     * @return command
     */
    public static byte[] selectPrintDirectionInPageMode(int n) {
        return new byte[]{ESC, 84, (byte) n};
    }

    /**
     * 将90˚顺时针旋转模式打开或关闭。
     * n的用法如下:
     * n=0, 48 : 关闭90˚顺时针旋转模式
     * n=1, 49 : 打开90˚的顺时针旋转模式
     * ESC V n
     *
     * @param n 0≤n≤1,48≤n≤49 default 0
     * @return command
     */
    public static byte[] turn90ClockwiseRotationMode(int n) {
        return new byte[]{ESC, 86, (byte) n};
    }

    /**
     * 水平起始位置、垂直起始位置、打印区域宽度、打印区域高度分别定义为x0、y0、dx、dy。可打印区域的每个设置计算如下:
     * x0 = [(xL + xH x 256) x(水平运动单元)]
     * y0 = [(yL + yH x 256) x(垂直运动单位)]
     * dx = [(dxL + dxH x 256) x(水平运动单位)]
     * dy = [(dyL + dyH x 256) x(垂直运动单位)]
     * ESC W xL xH yL yH dxL dxH dyL dyH
     *
     * @param xL  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param xH  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param yL  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param yH  0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dxL 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dxH 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dyL 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     * @param dyH 0≤ xL xH yL yH dxL dxH dyL dyH ≤255 (except dxL=dxH=0 or dyL=dyH=0)
     *            default xL = xH = yL = yH = 0, dxL = 0, dxH = 2, dyL =126, dyH = 6
     * @return command
     */
    public static byte[] setPrintingAreaInPageMode(int xL, int xH, int yL, int yH,
                                                   int dxL, int dxH, int dyL, int dyH) {
        return new byte[]{ESC, 87, (byte) xL, (byte) xH, (byte) yL, (byte) yH,
                (byte) dxL, (byte) dxH, (byte) dyL, (byte) dyH};
    }

    /**
     * 使用水平或垂直运动单元，根据当前位置设置打印开始位置。此命令设置从当前位置到[(nL+ nH x 256) x(水平或垂直单位)]的距离。
     * ESC \ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setRelativePrintPosition(int nL, int nH) {
        return new byte[]{ESC, 92, (byte) nL, (byte) nH};
    }

    /**
     * 将所有数据对齐到指定的位置n选择如下的调整:
     * n=0,48 :  向左对齐
     * n=1,49 : 置于中心位置
     * n=2,50 : Right justification
     * ESC a n
     *
     * @param n 0≤n≤2,48≤n≤50 default 0
     * @return command
     */
    public static byte[] selectJustification(int n) {
        return new byte[]{ESC, 97, (byte) n};
    }

    /**
     * 选择纸张传感器输出纸张末端信号。n的每个比特的用法如下:
     * n=0 : 纸辊近端传感器禁用纸辊端传感器禁用
     * n=1,2 :启用纸卷近端传感器
     * n=4,8 : 启动纸卷末端传感器
     * ESC c 3 n
     *
     * @param n 0≤n≤255 default 15
     * @return command
     */
    public static byte[] selectPaperSensorToOutputPaperEndSignals(int n) {
        return new byte[]{ESC, 99, 51, (byte) n};
    }

    /**
     * 当检测到纸尾时，选择用于停止打印的纸张传感器。n的使用如下:
     * n=0 :纸卷近端传感器已停用
     * n=1,2 : 启动纸卷近端传感器
     * ESC c 4 n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] selectPaperSensorToStopPrinting(int n) {
        return new byte[]{ESC, 99, 52, (byte) n};
    }

    /**
     * 禁用面板按钮。
     * ESC c 5 n
     *
     * @return command
     */
    public static byte[] disablePanelButtons() {
        return new byte[]{ESC, 99, 53, 0};
    }

    /**
     * 启用面板按钮。
     * ESC c 5 n
     *
     * @return command
     */
    public static byte[] enablePanelButtons() {
        return new byte[]{ESC, 99, 53, 1};
    }

    /**
     * 打印缓冲区中的数据并输入n行。
     * ESC d n
     *
     * @param n 0≤n ≤255
     * @return command
     */
    public static byte[] printFeedNLines(int n) {
        return new byte[]{ESC, 100, (byte) n};
    }

    /**
     * 当收到这个命令时，纸张被切断(只有当自动切割机被加载时)。
     * ESC i
     *
     * @return command
     */
    public static byte[] executePaperFullCut() {
        return new byte[]{ESC, 105};
    }

    /**
     * 当收到这个命令时，纸张被切断(只有当自动切割机被加载时)。
     * ESC m
     *
     * @return command
     */
    public static byte[] executePaperPartialCut() {
        return new byte[]{ESC, 109};
    }

    /**
     * 将t1和t2规定的脉冲输出到连接器引脚m，如下所示:
     * m=0,48 : Drawer kick-out connector pin2.
     * m=1,49 : Drawer kick-out connector pin5.
     * ESC p m t1 t2
     *
     * @param m  m = 0, 1, 48, 49
     * @param t1 0≤t1≤255
     * @param t2 0≤t2≤255
     * @return command
     */
    public static byte[] generatePulse(int m, int t1, int t2) {
        return new byte[]{ESC, 112, (byte) m, (byte) t1, (byte) t2};
    }

    /**
     * 从字符编码表中选择第n页。
     * n=0: PC437[美国,欧洲标准)
     * n=1 : Katakana
     * n=2: PC850[多语言]
     * n=3: PC860[葡萄牙语]
     * n=4: PC863[加拿大法语]
     * n=5: PC865[北欧]
     * n=17: PC866[西里尔字母2]
     * n=255:空间页
     * ESC t n
     *
     * @param n 0≤n≤5, 16≤n≤26, n=255 default 0
     * @return command
     */
    public static byte[] selectCharacterCodeTable(int n) {
        return new byte[]{ESC, 116, (byte) n};
    }

    /**
     * 倒置打印模式关闭。
     * ESC { n
     *
     * @return command
     */
    public static byte[] turnsOffUpsideDownPrintingMode() {
        return new byte[]{ESC, 123, 0};
    }

    /**
     * 打开倒置打印模式。
     * ESC { n
     *
     * @return command
     */
    public static byte[] turnsOnUpsideDownPrintingMode() {
        return new byte[]{ESC, 123, 1};
    }

    /**
     * 用m指定的模式打印NV位图像n。
     * m=0,48 : Normal Mode Vertical=180dpi Horizontal=180dpi
     * m=1,49 : Double-width Mode Vertical=180dpi Horizontal=90dpi
     * m=2,50 : Double-height Mode Vertical=90dpi Horizontal=180dpi
     * m=3,51 : Quadruple Mode Vertical=90dpi Horizontal=90dpi
     * FS p n m
     *
     * @param n 1≤n≤255
     * @param m 0≤m≤3, 48≤m≤51
     * @return command
     */
    public static byte[] printNVBitImage(int n, int m) {
        return new byte[]{FS, 112, (byte) n, (byte) m};
    }

    /**
     * 定义n指定的NV位图像。
     * FS q n [xL xH yL yH d1...dk] 1...[xL xH yL yH d1...dk]n
     *
     * @param n     1≤n≤255
     * @param image 0≤xL≤255
     *              0≤xH≤3 (when 1≤(xL + xH x 256)≤1023)
     *              0≤yL≤255
     *              0≤yL≤1 (when 1≤(yL + yH x 256)≤288)
     *              0≤d≤255
     *              k = (xL + xH x 256) x (yL + yH x 256) x 8
     *              Total defined data area = 2M bits (256K bytes)
     * @return command
     */
    public static byte[] defineNVBitImage(int n, byte[] image) {
        byte[] part = new byte[]{FS, 113, (byte) n};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * 使用位0到3选择字符高度，使用位选择字符宽度
     * 4 to 7, as follows:
     * 0-3bit:
     * n=0 : height = 1(normal)
     * n=1 : height = 2(double-height)
     * n=2 : height = 3
     * n=3 : height = 4
     * n=4 : height = 5
     * n=5 : height = 6
     * n=6 : height = 7
     * n=7 : height = 8
     * 4-7bit:
     * n=0 : height = 1(normal)
     * n=1 : height = 2(double-width)
     * n=2 : height = 3
     * n=3 : height = 4
     * n=4 : height = 5
     * n=5 : height = 6
     * n=6 : height = 7
     * n=7 : height = 8
     * GS ! n
     *
     * @param n 0≤n≤255(1≤vertical number of times≤8, 1≤horizontal number of times≤8) default 0
     * @return command
     */
    public static byte[] selectCharacterSize(int n) {
        return new byte[]{GS, 33, (byte) n};
    }

    /**
     * 在页模式下设置缓冲区字符数据的绝对垂直打印起始位置。这个命令将打印的绝对位置设置为[(nL + nH x 256) x(垂直或水平运动单位)]英寸。
     * 如果[(nL + nH x 256) x(垂直或水平运动单元)]超出指定的打印区域，则忽略此命令。水平启动缓冲位置不移动。参考起始位置是由ESC T指定的。
     * 该命令操作如下，取决于ESC T指定的打印区域的起始位置:
     * 1。当起始位置设置为左上或右下时，此命令将设置垂直方向上的绝对位置。
     * 2. 当起始位置设置为右上或左下时，此命令将在水平方向上设置绝对位置。
     * 水平和垂直运动单元由GS P指定。GS P命令可以更改水平和垂直运动单元。
     * 但该值不能小于最小水平移动量，且必须以最小水平移动量为偶数单位。
     * GS $ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setAbsoluteVerticalPrintPositionInPageMade(int nL, int nH) {
        return new byte[]{GS, 36, (byte) nL, (byte) nH};
    }

    /**
     * 用x和y指定的点数定义下载的位图像。x表示水平方向上的点数。y表示垂直方向上点的个数。
     * GS * x y d1...d (x x y x 8)
     *
     * @param x     1≤n≤255
     * @param y     1≤n≤255
     * @param image x x y≤1536
     *              0≤d≤255
     * @return command
     */
    public static byte[] defineDownloadedBitImage(int x, int y, byte[] image) {
        byte[] part = new byte[]{GS, 42, (byte) x, (byte) y};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * 使用m指定的模式打印下载的位图像。m从下表选择一种模式:
     * m=0,48:正模垂直=180dpi水平=180dpi
     * m=1,49:双宽模式垂直=180dpi水平=90dpi
     * m=2,50:双高模式垂直=90dpi水平=180dpi
     * m=3,51:四模垂直=90dpi水平=90dpi
     * GS / m
     *
     * @param m 0≤m≤3, 48≤m≤51
     * @return command
     */
    public static byte[] printDownloadedBitImage(int m) {
        return new byte[]{GS, 47, (byte) m};
    }

    /**
     * 开始或结束宏定义。
     * GS :
     *
     * @return command
     */
    public static byte[] startOrEndMacroDefinition() {
        return new byte[]{GS, 58};
    }

    /**
     * 关闭黑白反向打印模式。
     * GS B n
     *
     * @return command
     */
    public static byte[] turnOffWhiteBlackReversePrintingMode() {
        return new byte[]{GS, 66, 0};
    }

    /**
     * 打开黑白反向打印模式。
     * GS B n
     *
     * @return command
     */
    public static byte[] turnOnWhiteBlackReversePrintingMode() {
        return new byte[]{GS, 66, 1};
    }

    /**
     * 在打印条码时，选择HRI字符的打印位置。n选择打印位置如下:
     * m=0,48:未打印
     * m=1,49:在条形码上方
     * m=2,50:在条形码下方
     * m=3,51:在条形码上方和下方
     * GS H n
     *
     * @param n 0≤m≤3, 48≤m≤51 default 0
     * @return command
     */
    public static byte[] selectPrintingPositionOfHRICharacters(int n) {
        return new byte[]{GS, 72, (byte) n};
    }

    /**
     * 使用nL和nH设置左边距。左边距设置为[(nL + nH x 256) x(水平运动单元)]英寸。
     * GS L nL nH
     *
     * @param nL 0≤nL≤255 default 0
     * @param nH 0≤nH≤255 default 0
     * @return command
     */
    public static byte[] setLeftMargin(int nL, int nH) {
        return new byte[]{GS, 76, (byte) nL, (byte) nH};
    }

    /**
     * 将水平和垂直运动单元分别设置为1x英寸和1y英寸。当x和u设置为0时，将使用每个值的默认设置。(x = 180, y = 360)
     * GS P x y
     *
     * @param x 0≤x≤255 default 180
     * @param y 0≤y≤255 default 360
     * @return command
     */
    public static byte[] setHorizontalAndVerticalMotionUnits(int x, int y) {
        return new byte[]{GS, 80, (byte) x, (byte) y};
    }

    /**
     * 选择裁纸模式并执行裁纸。m的值选择的模式如下:
     * m=1,49:部分切割(有一点中心未切割)
     * m=66:送纸(裁切位置+ [n x(垂直运动单元)])，
     * 部分切纸(中间一点未切)
     * GS V m
     * GS V m n
     *
     * @param m m=1,49,66
     * @param n 0≤n≤255
     * @return command
     */
    public static byte[] selectCutModeAndCutPaper(int m, int n) {
        if (m == 66) {
            return new byte[]{GS, 86, 66, (byte) n};
        } else {
            return new byte[]{GS, 86, (byte) m};
        }
    }

    /**
     * 将打印区域宽度设置为nL和nH指定的区域。印刷区域宽度设置为[(nL + nH x 256) x水平运动单元]]。
     * GS W nL nH
     *
     * @param nL 0≤nL≤255 default 0
     * @param nH 0≤nH≤255 default 2
     * @return command
     */
    public static byte[] setPrintingAreaWidth(int nL, int nH) {
        return new byte[]{GS, 87, (byte) nL, (byte) nH};
    }

    /**
     * 在页模式中，从当前位置设置相对垂直打印开始位置。这个命令设置从当前位置到[(nL + nH x 256)垂直或水平运动单位]英寸的距离。
     * GS \ nL nH
     *
     * @param nL 0≤nL≤255
     * @param nH 0≤nH≤255
     * @return command
     */
    public static byte[] setRelativeVerticalPrintPositionInPageMode(int nL, int nH) {
        return new byte[]{GS, 92, (byte) nL, (byte) nH};
    }

    /**
     * 执行宏。r指定执行宏的次数。指定执行宏的等待时间。m指定宏执行模式。当m = 0的LSB:宏r乘以不断在指定的时间间隔执行t。
     * 当LSB m = 1:等待指定的时间t后,纸LED指示灯闪烁,打印机等待提要按钮被按下。按下按钮后，打印机执行宏一次。打印机重复这个操作r次。
     * GS ^ r t m
     *
     * @param r 0≤r≤255
     * @param t 0≤t≤255
     * @param m m=0,1
     * @return command
     */
    public static byte[] executeMacro(int r, int t, int m) {
        return new byte[]{GS, 94, (byte) r, (byte) t, (byte) m};
    }

    /**
     * 启用或禁用ASB，并指定要包含的状态项，使用n如下:
     * bit=0,n=0 : Drawer kick-out connector pin 3 status disabled.
     * bit=0,n=1 : Drawer kick-out connector pin 3 status enabled.
     * bit=1,n=0 : On-line/off-line status disabled.
     * bit=1,n=2 : On-line/off-line status enabled,
     * bit=2,n=0 : Error status disabled.
     * bit=2,n=4 : Error status enabled.
     * bit=3,n=0 : Paper roll sensor status disabled.
     * bit=3,n=8 : Paper roll sensor status enabled.
     * GS a n
     *
     * @param n 0≤n≤255 default 0
     * @return command
     */
    public static byte[] setAutomaticStatusBack(int n) {
        return new byte[]{GS, 97, (byte) n};
    }

    /**
     * 为打印条形码时使用的HRI字符选择字体。
     * n从下表中选择字体:
     * n=0,48 : Font A (12 x 24)
     * n=1,49 : Font B (9 x 24)
     * GS f n
     *
     * @param n n = 0, 1, 48, 49
     * @return command
     */
    public static byte[] selectFontForHumanReadableInterpretationCharacters(int n) {
        return new byte[]{GS, 102, (byte) n};
    }

    /**
     * 选择条形码的高度。n指定垂直方向上的点数。
     * GS h n
     *
     * @param n 1≤n≤255 default 162
     * @return command
     */
    public static byte[] selectBarCodeHeight(int n) {
        return new byte[]{GS, 104, (byte) n};
    }

    /**
     * 选择条形码系统并打印条形码。m选择如下的条码系统:
     * m=0 : UPC–A 11≤k≤12 48≤d≤57
     * m=1 : UPC–E 11≤k≤12 48≤d≤57
     * m=2 : EAN13 12≤k≤13 48≤d≤57
     * m=3 : EAN8 7≤k≤8 48≤d≤57
     * m=4 : CODE39 1≤k 48≤d≤57,65≤d≤90,32,36,37,43,45,46,47
     * m=5 : ITF 1≤k(even number) 48≤d≤57
     * m=6 : CODABAR 1≤k 48≤d≤57,65≤d≤68,36,43,45,46,47,58
     * m=65 : UPC–A 11≤k≤12 48≤d≤57
     * m=66 : UPC–E 11≤k≤12 48≤d≤57
     * m=67 : EAN13 12≤k≤13 48≤d≤57
     * m=68 : EAN8 7≤k≤8 48≤d≤57
     * m=69 : CODE39 1≤k≤255 48≤d≤57,65≤d≤90,32,36,37,43,45,46,47
     * m=70 : ITF 1≤k≤255(even number) 48≤d≤57
     * m=71 : CODABAR 1≤k≤255 48≤d≤57,65≤d≤68,36,43,45,46,47,58
     * m=72 : CODE93 1≤k≤255 0≤d≤127
     * m=73 : CODE128 2≤k≤255 0≤d≤127
     * GS k m d1…dk NUL
     * GS k m n d1…dn
     *
     * @param m    0≤m≤6
     *             65≤m≤73
     * @param n    n and d depends on the code system used
     * @param data k and d depends on the code system used
     * @return command
     */
    public static byte[] printBarCode(int m, int n, byte[] data) {
        if (m <= 6) {
            byte[] part = new byte[]{GS, 107, (byte) m};
            byte[] destination = new byte[part.length + data.length + 1];
            System.arraycopy(part, 0, destination, 0, part.length);
            System.arraycopy(data, 0, destination, part.length, data.length);
            destination[part.length + data.length] = 0;
            return destination;
        } else {
            byte[] part = new byte[]{GS, 107, (byte) m, (byte) n};
            byte[] destination = new byte[part.length + data.length];
            System.arraycopy(part, 0, destination, 0, part.length);
            System.arraycopy(data, 0, destination, part.length, data.length);
            return destination;
        }
    }

    /**
     * 传输n指定的状态，如下所示:
     * n=1,49 : 传输纸张传感器状态
     * n=2,50 : 传输抽屉拔出连接器状态
     * GS r n
     *
     * @param n n=1, 2, 49, 50
     * @return command
     */
    public static byte[] transmitStatus(int n) {
        return new byte[]{GS, 114, (byte) n};
    }

    /**
     * 选择光栅位图像模式。
     * The value of m selects the mode, as follows:
     * m=0,48 : Normal Mode Vertical=180dpi Horizontal=180dpi
     * m=1,49 : Double-width Mode Vertical=180dpi Horizontal=90dpi
     * m=2,50 : Double-height Mode Vertical=90dpi Horizontal=180dpi
     * m=3,51 : Quadruple Mode Vertical=90dpi Horizontal=90dpi
     * xL, xH, select the number of data bytes (xL+xHx256) in the horizontal direction for the bit
     * image.
     * yL, yH, select the number of data bytes (xL+xHx256) in the vertical direction for the bit
     * image.
     * GS v 0 m xL xH yL yH d1....dk
     *
     * @param m     0≤m≤3, 48≤m≤51
     * @param xL    0≤xL≤255
     * @param xH    0≤xH≤255
     * @param yL    0≤yL≤255
     * @param yH    0≤yH≤8
     * @param image 0≤d≤255
     *              k=(xL + xH x 256) x (yL + yH x 256) (k≠0)
     * @return command
     */
    public static byte[] printRasterBitImage(int m, int xL, int xH, int yL, int yH, byte[] image) {
        byte[] part = new byte[]{GS, 118, 48, (byte) m, (byte) xL, (byte) xH, (byte) yL, (byte) yH};
        byte[] destination = new byte[part.length + image.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(image, 0, destination, part.length, image.length);
        return destination;
    }

    /**
     * 设置条形码的水平大小。n指定条形码宽度如下:
     * n=2 :
     * 0.282mm (Module width for Mult-level Bar code),
     * 0.282mm (Thin element width),
     * 0.706mm (Thick element width)
     * n=3 :
     * 0.423mm (Module width for Mult-level Bar code),
     * 0.423mm (Thin element width),
     * 1.129mm (Thick element width)
     * n=4 :
     * 0.564mm (Module width for Mult-level Bar code),
     * 0.564mm (Thin element width),
     * 1.411mm (Thick element width)
     * n=5 :
     * 0.706mm (Module width for Mult-level Bar code),
     * 0.706mm (Thin element width),
     * 1.834mm (Thick element width)
     * n=6 :
     * 0.847mm (Module width for Mult-level Bar code),
     * 0.847mm (Thin element width),
     * 2.258mm (Thick element width)
     * Multi-level bar codes are as follows:
     * UPC-A, UPC-E, JAN13 (EAN13), JAN8 (EAN8), CODE93, CODE128
     * Binary-level bar codes are as follows: CODE39, ITF, CODABAR
     * GS w n
     *
     * @param n 2≤n≤6
     * @return command
     */
    public static byte[] setBarCodeWidth(int n) {
        return new byte[]{GS, 119, (byte) n};
    }

    /**
     * 选择二维码的模型。
     * (pL + pH x 256) = 4 (pL=4, pH=0)
     * cn = 49
     * fn = 65
     * n1 Function
     * 49 Selects model 1.
     * 50 Selects model 2.
     * GS ( k pL pH cn fn n1 n2
     *
     * @param n1 n1 = 49,50
     * @param n2 n2 = 0
     * @return command
     */
    public static byte[] selectQRCodeModel(int n1, int n2) {
        return new byte[]{GS, 40, 107, 4, 0, 49, 65, (byte) n1, (byte) n2};
    }

    /**
     * Sets模块的大小为n点为QR码。
     * GS ( k pL pH cn fn n
     *
     * @param n (pL + pH x 256) = 3 (pL=3, pH=0)
     *          cn = 49
     *          fn = 67
     * @return command
     */
    public static byte[] setQRCodeSizeOfModule(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 49, 67, (byte) n};
    }

    /**
     * 选择二维码的纠错级别。
     * n     fuction                             Recovery Capacity %(approx.)
     * 48    Selects Error correction level L    7
     * 49    Selects Error correction level M    15
     * 50    Selects Error correction level Q    25
     * 51    Selects Error correction level H    30
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 49
     * fn = 69
     * GS ( k pL pH cn fn n
     *
     * @param n 47<n<52
     * @return command
     */
    public static byte[] selectQRCodeErrorCorrectionLevel(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 49, 69, (byte) n};
    }

    /**
     * 将二维码符号数据(d1…dk)存储在符号存储区。
     * cn = 49
     * fn = 80
     * m = 48
     * GS ( k pL pH cn fn m d1…dk
     *
     * @param pL   0≤pL<256,
     * @param pH   0≤pH<28
     * @param data 0≤d < 255
     *             k = (pL + pH* 256) - 3
     * @return command
     */
    public static byte[] storeQRCodeDataInTheSymbolStorageArea(int pL, int pH, byte[] data) {
        byte[] part = new byte[]{GS, 40, 107, (byte) pL, (byte) pH, 49, 80, 48};
        byte[] destination = new byte[part.length + data.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(data, 0, destination, part.length, data.length);
        return destination;
    }

    /**
     * 使用的过程对符号存储区域中的二维码符号数据进行编码并打印
     * <Store the data >.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 49
     * fn = 81
     * m = 48
     * GS ( k pL pH cn fn m
     *
     * @return command
     */
    public static byte[] printQRCodeSymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 3, 0, 49, 81, 48};
    }

    /**
     * 设置PDF417的数据区域的列数。
     * n = 0 指定自动处理。
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 65
     * GS ( k pL pH cn fn n
     *
     * @param n 0≤n≤30
     * @return command
     */
    public static byte[] setNumberOfColumnsOfTheDataAreaForPDF417(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 65, (byte) n};
    }

    /**
     * 设置PDF417的数据区域的行数。
     * n = 0 specifies automatic processing.
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 66
     * GS ( k pL pH cn fn n
     *
     * @param n n=0, 3≤n≤90
     * @return command
     */
    public static byte[] setNumberOfRowsOfDataAreaForPDF417(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 66, (byte) n};
    }

    /**
     * 设置一个PDF417符号的模块宽度为n点。
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 67
     * GS ( k pL pH cn fn n
     *
     * @param n 2≤n≤8
     * @return command
     */
    public static byte[] setModuleWidthOfOnePDF417SymbolDots(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 67, (byte) n};
    }

    /**
     * 设置模块高度为[(模块宽度)×n]。
     * (pL + pH x 256) = 3 (pL=3, pH=0)
     * cn = 48
     * fn = 68
     * GS ( k pL pH cn fn n
     *
     * @param n 2≤n≤8
     * @return command
     */
    public static byte[] setPDF417ModuleHeight(int n) {
        return new byte[]{GS, 40, 107, 3, 0, 48, 68, (byte) n};
    }

    /**
     * 设置PDF417符号的错误纠正级别。
     * (pL + pH x 256) = 4 (pL=4, pH=0)
     * cn = 48
     * fn = 69
     * GS ( k pL pH cn fn m n
     *
     * @param m m = 48,49
     * @param n 48≤n≤56 (when m=48 is specified)
     *          1≤n≤40 (when m=49 is specified)
     * @return command
     */
    public static byte[] setErrorCorrectionLevelForPDF417Symbols(int m, int n) {
        return new byte[]{GS, 40, 107, 4, 0, 48, 69, (byte) m, (byte) n};
    }

    /**
     * 在PDF417符号存储区存储符号数据(d1…dk)。m (d1…dk)后的((pL + pH×256)- 3)字节作为符号数据处理。
     * 4≤(pL + pH x 256) ≤65535 (0≤pL≤255, 0≤pH≤255)
     * cn = 48
     * fn = 80
     * m = 48
     * GS ( k pL pH cn fn m d1…dk
     *
     * @param pL   0≤pL≤255
     * @param pH   0≤pH≤255
     * @param data 0≤d≤255
     *             k = (pL + pH*256) - 3
     * @return command
     */
    public static byte[] storeSymbolDataInThePDF417SymbolStorageArea(int pL, int pH, byte[] data) {
        byte[] part = new byte[]{GS, 40, 107, (byte) pL, (byte) pH, 48, 80, 48};
        byte[] destination = new byte[part.length + data.length];
        System.arraycopy(part, 0, destination, 0, part.length);
        System.arraycopy(data, 0, destination, part.length, data.length);
        return destination;
    }

    /**
     * 在符号存储区打印PDF417符号数据。
     * (pL + pH* 256) = 4 (pL=4, pH=0)
     * cn = 48
     * fn = 81
     * m = 48
     * GS ( k pL pH cn fn m
     *
     * @return command
     */
    public static byte[] printPDF417SymbolDataInTheSymbolStorageArea() {
        return new byte[]{GS, 40, 107, 4, 0, 48, 81, 48};
    }
}