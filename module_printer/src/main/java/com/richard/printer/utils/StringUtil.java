package com.richard.printer.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * author Richard
 * date 2020/12/23 10:54
 * version V1.0
 * description: 字符串工具类
 */
public final class StringUtil {


    /**
     * 按字节长度截取字符串
     * 注意：长度是以byte为单位的，一个汉字是2个byte
     *
     * @param text        要截取的字符串
     * @param charsetName 字符编码名称
     * @param limit       每次截取字节数
     * @return 截取结果
     */
    public static List<String> substring(String text, String charsetName, int limit) {
        if (text == null) {
            return new ArrayList<>();
        }

        try {
            charsetName = charsetName == null || "".equals(charsetName) ? "GBK" : charsetName;
            int textLength = text.getBytes(charsetName).length;
            char[] tempChar = text.toCharArray();
            int reInt = 0;
            int index = 0;
            String reStr = "";
            List<String> result = new ArrayList<>();

            while (reInt < textLength) {
                for (int i = 0; i < limit && index < tempChar.length; i++) {
                    String s1 = String.valueOf(tempChar[index]);

                    //若字节长度超过了当前指定的分割字节数量，则忽略该次切割，放到下次切割
                    if (reStr.concat(s1).getBytes(charsetName).length > limit) {
                        break;
                    }

                    reInt += s1.getBytes(charsetName).length;
                    reStr = reStr.concat(s1);
                    index++;
                }

                result.add(reStr);
                reStr = "";
            }

            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
