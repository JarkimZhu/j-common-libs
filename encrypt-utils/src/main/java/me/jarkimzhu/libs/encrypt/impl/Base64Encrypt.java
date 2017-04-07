package me.jarkimzhu.libs.encrypt.impl;

import me.jarkimzhu.libs.encrypt.IEncrypt;

import java.util.Base64;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public class Base64Encrypt extends AbstractEncrypt implements IEncrypt {

    @Override
    public String decode(String encrypt) {
        byte[] sBytes = stringToBytes(encrypt);
        byte[] dBytes = Base64.getDecoder().decode(sBytes);
        return bytesToString(dBytes);
    }

    @Override
    public String encode(String value) {
        byte[] vBytes = stringToBytes(value);
        byte[] eBytes = Base64.getEncoder().encode(vBytes);
        return bytesToString(eBytes);
    }
}
