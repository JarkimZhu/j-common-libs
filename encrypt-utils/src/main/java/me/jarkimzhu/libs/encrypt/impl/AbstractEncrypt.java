package me.jarkimzhu.libs.encrypt.impl;

import me.jarkimzhu.libs.encrypt.IEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public abstract class AbstractEncrypt implements IEncrypt {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEncrypt.class);

    /**
     * 字符编码
     */
    protected String charset = "UTF-8";

    /**
     * 秘钥
     */
    protected String key;

    /**
     * 结果长度限制
     */
    protected int length;

    @Override
    public IEncrypt charset(String charset) {
        this.charset = charset;
        return this;
    }

    @Override
    public IEncrypt key(String key) {
        this.key = key;
        return this;
    }

    @Override
    public IEncrypt length(int length) {
        this.length = length;
        return this;
    }

    byte[] stringToBytes(String value) {
        byte[] bytes;
        try {
            bytes = value.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            bytes = value.getBytes();
        }
        return bytes;
    }

    String bytesToString(byte[] bytes) {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return new String(bytes);
        }
    }
}
