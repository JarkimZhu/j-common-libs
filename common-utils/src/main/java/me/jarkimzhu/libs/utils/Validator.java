package me.jarkimzhu.libs.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    /**
     * 是否为数字类型(String)
     * true:数字类型，false:并非数字类型
     *
     * @param data 要检验的数据
     * @return
     */
    public static boolean isNumber(String data) {
        boolean isNumber = true;
        try {
            long tmp = Long.parseLong(data.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return isNumber;
    }

    /**
     * 是否为英文和数字
     *
     * @param str
     * @return true:英文和数字 | false:非英文和数字
     */
    public static boolean isEnOrNum(String str) {
        return Pattern.matches("^[A-Za-z0-9]+", str);
    }

    /**
     * 是否为双字节字符
     * 中文 | 日文 | 韩文 都属于双字节字符
     *
     * @param str
     * @return true:双字节字符 | false:不是
     */
    public static boolean isDoubleChar(String str) {
        return Pattern.matches("[^\\x00-\\xff]+", str);
    }

    public static boolean notDoubleChar(String str) {
        return !isDoubleChar(str);
    }

    /**
     * 是否为手机号
     *
     * @param str
     * @return true:手机号 | false:不是手机号
     */
    public static boolean isPhone(String str) {
//		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");      

//        Matcher m = p.matcher(str);
        if (str.length() <= 11) {
            return true;
        }
//        System.out.println(m.matches()+"---");      
        return false;
    }

    /**
     * 是否为Email
     *
     * @param email 邮箱地址
     * @return 是否匹配
     */
    public static boolean isEmail(String email) {
        return isEmail(email, false);
    }

    /**
     * 验证邮箱
     *
     * @param email 邮箱地址
     * @param strict 是否启用严格模式
     * @return 是否匹配
     */
    public static boolean isEmail(String email, boolean strict) {
        String reg;
        if(strict) {
            reg = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\\\.][A-Za-z]{2,3}([\\\\.][A-Za-z]{2})?$";
        } else {
            reg = "[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+";
        }
        return Pattern.matches(reg, email);
    }

    /**
     * 验证电话号码
     *
     * @param telPhone
     * @return
     */
    public static boolean isTelPhone(String telPhone) {
        String str = "^([0-9]{3,4}-?)[0-9]{7,8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(telPhone);
        return m.matches();
    }

    /**
     * 验证手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        String str = "^((12[0-9])|(13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 是否包含数字
     *
     * @param value
     * @return
     */
    public static boolean notHasNumber(String value) {
        String str = "^[^0-9]+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    /**
     * 是否是表情
     *
     * @param ch
     * @return
     */
    public static boolean isEmoji(char ch) {
        return !((ch == 0x0) || (ch == 0x9) || (ch == 0xA) || (ch == 0xD)
                || ((ch >= 0x20) && (ch <= 0xD7FF))
                || ((ch >= 0xE000) && (ch <= 0xFFFD)) || ((ch >= 0x10000) && (ch <= 0x10FFFF)));
    }

    /**
     * 清除一个字符串中的emoji表情字符
     */
    public static String cleanEmoji(String s) {
        if (CommonUtils.isBlank(s)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < builder.length(); i++) {
            if (isEmoji(builder.charAt(i))) {
                builder.deleteCharAt(i);
                builder.insert(i, "");// 比字符串中直接替换字符要好，那样会产生很多字符串对象
            }
        }
        return builder.toString().trim();
    }

    public static String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("*");
                return source;
            }
            return source;
        }
        return null;
    }

    public static boolean containsEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                //source = emojiMatcher.replaceAll("*");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 是否是中文
     *
     * @param value
     * @return
     */
    public static boolean isChn(String value) {
        String str = "^[\\u4e00-\\u9fa5]+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    /**
     * 是否是URL
     *
     * @param value
     * @return
     */
    public static boolean isUrl(String value) {
        String str = "^(http|https)://[\\w\\.\\-]+(?:/|(?:/[\\w\\.\\-]+)*)?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
