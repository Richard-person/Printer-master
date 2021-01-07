# Printer-master
这是一个支持USB端口、网络端口、蓝牙端口的小票打印库，提供较全的打印指令，便捷添加格式自适应打印内容

##源码示例
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
            PrintParams printParams = new PrintParams(TicketSpec.SPEC_58);
    //        printParams.add("消费小票", 1, false, Align.CENTER);
    //        printParams.addNextRow();
    //        printParams.addRow(0, String.format("单号:%s", "AD123213212321231231223434343432"));
    //        printParams.addSplitLine(0, true);
    //
    //        //第一种
    //        float[] widthWeigh = new float[]{2.4F, 1, 1, 1};
    //        printParams.addRow(0, false, widthWeigh, "名称", "数量", "单价", "小计");
    //        printParams.addRow(0, widthWeigh, "青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭青椒肉丝炒饭", "1", "0.99", "0.99");
    //        printParams.addRow(0, widthWeigh, "牛排4人套餐", "1", "0.99", "0.99");
    //        printParams.addRow(0, widthWeigh, "青椒肉丝炒饭", "1", "0.99", "0.999999999999999999999999999");
    //        printParams.addRow(0, widthWeigh, "牛排4人套餐", "1", "0.99", "0.99");
    //        printParams.addSplitLine(0, true);


            //第二种
            int fontSize = 0;
            printParams.add("消费小票", 1, true, Align.CENTER);
            printParams.addNextRow();
            printParams.addRow(fontSize, new ColumnItem(String.format("单号:%s", "AD1232132123212312312234343")));
            printParams.addSplitLine(0, true);

            printParams.addRow(
                    fontSize
                    , new ColumnItem("名称", 1.4F)
                    , new ColumnItem("数量", 1)
                    , new ColumnItem("单价", 1)
                    , new ColumnItem("合计", 1)
            );

            printParams.addRow(
                    fontSize
                    , new ColumnItem("土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝", 1.4F, false, EllipsizeMode.LINE)
                    , new ColumnItem("10", 1)
                    , new ColumnItem("99900", 1)
                    , new ColumnItem("0.99", 1)
            );

            printParams.addRow(
                    fontSize
                    , new ColumnItem("青椒土豆肉丝", 1.4F, false,EllipsizeMode.LINE)
                    , new ColumnItem("100", 1)
                    , new ColumnItem("0.99", 1)
                    , new ColumnItem("0.999999999999999999999999999", 1,false,EllipsizeMode.LINE)
            );

            printParams.addRow(
                    fontSize
                    , new ColumnItem("青椒土豆肉丝青椒土豆肉丝", 1.4F, false,EllipsizeMode.LINE)
                    , new ColumnItem("100", 1)
                    , new ColumnItem("0.99", 1)
                    , new ColumnItem("0.99", 1)
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
    //                    POSPrinter printer = PrinterManager.get().connectNetPort("172.16.2.249", 9100);
                        POSPrinter printer = PrinterManager.get().connectUSBPort(getApplicationContext(), "/dev/bus/usb/001/004");

                        //重置复位打印机
                        printDataList.add(0, PrinterCmdUtil.resetPrinter());

                        //设置行距
                        printDataList.add(1, PrinterCmd.setLineSpacing(80));

                        //打印并换行
                        printDataList.add(PrinterCmdUtil.printLineFeed());

                        //送纸150像素
                        printDataList.add(PrinterCmd.printFeedPaper(150));

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