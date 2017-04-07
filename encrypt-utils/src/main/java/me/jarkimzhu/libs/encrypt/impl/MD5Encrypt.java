package me.jarkimzhu.libs.encrypt.impl;


import me.jarkimzhu.libs.encrypt.IEncrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author JarkimZhu
 *         Created on 2016/11/2.
 * @since jdk1.8
 */
public class MD5Encrypt extends AbstractEncrypt implements IEncrypt {

    @Override
    public String decode(String encrypt) {
        throw new IllegalAccessError("MD5 not support decode");
    }

    @Override
    public String encode(String value) {
        // 加密之后所得字节数组
        byte[] bytes;
        try {
            // 获取MD5算法实例 得到一个md5的消息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //添加要进行计算摘要的信息
            md.update(value.getBytes());
            //得到该摘要
            bytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        return bytesToString(bytes);
    }

    @Override
    String bytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String s = Integer.toHexString(0xff & aByte);
            if (s.length() == 1) {
                sb.append("0").append(s);
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
