package me.jarkimzhu.libs.utils;

public class StringUtils {

    public static String GBK(String strVal) {
        String strRet = "";
        try {
            strRet = new String(strVal.getBytes("ISO-8859-1"), "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strRet;
    }

    public static String UTF8(String strVal) {
        String strRet = "";
        try {
            strRet = new String(strVal.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strRet;
    }


}
