package com.richard.hardwaredemo;

import android.os.Bundle;
import android.view.View;

import com.richard.printer.command.PrinterCmd;
import com.richard.printer.command.PrinterCmdUtil;
import com.richard.printer.enumerate.Align;
import com.richard.printer.enumerate.EllipsizeMode;
import com.richard.printer.enumerate.TicketSpec;
import com.richard.printer.model.ColumnItem;
import com.richard.printer.utils.POSPrinter;
import com.richard.printer.utils.PrintParams;
import com.richard.printer.utils.PrinterManager;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View btn_test_print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定控件
        btn_test_print = findViewById(R.id.btn_test_print);

        btn_test_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                generatorPrintData();
                startPrint(generatorPrintData());

//                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//                HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//                while (deviceIterator.hasNext()){
//                    UsbDevice usbDevice = deviceIterator.next();
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Log.d("testtt",String.format("%s[%s]",usbDevice.getManufacturerName(),usbDevice.getDeviceName()));
//                    } else {
//                        Log.d("testtt",String.format("%s[%s]",usbDevice.getDeviceName(),usbDevice.getDeviceName()));
//                    }
//
//                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        PrinterManager.get().disconnectAll();
        super.onDestroy();
    }

    /**
     * 测试打印
     */
    private PrintParams generatorPrintData() {
        PrintParams printParams = new PrintParams(TicketSpec.SPEC_80);
//        printParams.add("消费小票", 1, false, Align.CENTER);

//        printParams.addNextRow();
//        printParams.addRow(0, String.format("单号:%s", "AD123213212321231231223434343432"));
//        printParams.addSplitLine(0, true);

        //第一种
//        float[] widthWeigh = new float[]{2F, 1, 1, 1};
//        printParams.addRow("名称", "数量", "单价", "小计");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "牛排4人套餐", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "青椒肉丝炒饭", "1", "0.99", "0.999999999999999999999999999");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "牛排4人套餐", "1", "0.99", "0.99");
//        printParams.addSplitLine(0, true);


        //第二种
        int fontSize = 0;
        float[] widthWeigh = new float[]{2F, 1, 1, 1};
        printParams.addRow(
                fontSize
                , widthWeigh
                , new ColumnItem("名称", Align.LEFT)
                , new ColumnItem("数量", Align.LEFT)
                , new ColumnItem("单价", Align.LEFT)
                , new ColumnItem("合计", Align.RIGHT)
        );

        printParams.addRow(
                fontSize
                , widthWeigh
                , new ColumnItem("土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("10", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("9990099900999009990099900", false, EllipsizeMode.ELLIPSIS, Align.LEFT)
                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.RIGHT)
        );

        printParams.addRow(
                fontSize
                , widthWeigh
                , new ColumnItem("青椒土豆肉丝", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("100", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("0.999999999999999999999999999", false, EllipsizeMode.COLUMN_LINE, Align.RIGHT)
        );

        printParams.addRow(
                fontSize
                , widthWeigh
                , new ColumnItem("青椒土豆肉丝青椒土豆肉丝", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("100", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.RIGHT)
        );


        return printParams;
    }

    /**
     * 开始打印
     *
     * @param printDataList 必填 打印数据
     * @return 是否打印成功
     */
    public void startPrint(final List<byte[]> printDataList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接打印机并打印
                    POSPrinter printer = PrinterManager.get().connectNetPort("172.16.2.249", 9100);
//                    POSPrinter printer = PrinterManager.get().connectUSBPort(getApplicationContext(), "/dev/bus/usb/001/004");

                    //重置复位打印机
                    printDataList.add(0, PrinterCmdUtil.resetPrinter());

                    //设置行距
                    printDataList.add(1, PrinterCmd.setLineSpacing(80));

                    //打印并换行
                    printDataList.add(PrinterCmdUtil.printLineFeed());

                    //送纸150像素
                    printDataList.add(PrinterCmd.printFeedPaper(700));

                    //进纸切割
                    printDataList.add(PrinterCmdUtil.feedPaperCutPartial());

                    //重置复位打印机
                    printDataList.add(0, PrinterCmdUtil.resetPrinter());

                    //写入打印内容数据
                    printer.write(printDataList);

                    //端口连接
                    printer.disconnect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}