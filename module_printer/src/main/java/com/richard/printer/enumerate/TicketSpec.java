package com.richard.printer.enumerate;

/**
 * author Richard
 * date 2020/8/18 10:49
 * version V1.0
 * description: 打印机相关错误码
 */
public enum TicketSpec {
    SPEC_80,
    //    SPEC_76,
    SPEC_58;

    /**
     * 解析成小票规格枚举类型
     */
    public static TicketSpec parse(int spec) {
        switch (spec) {
            case 80:
                return TicketSpec.SPEC_80;
            case 58:
            default:
                return TicketSpec.SPEC_58;
        }
    }
}
