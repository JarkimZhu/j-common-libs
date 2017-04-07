package me.jarkimzhu.libs.encrypt;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public interface IEncrypt {
    /**
     * 设置编解码字符编码方式
     * @param charset 编码
     * @return this, 链式调用
     */
    IEncrypt charset(String charset);

    /**
     * 设置加密秘钥
     * @param key 加密秘钥
     * @return this, 链式调用
     */
    IEncrypt key(String key);

    /**
     * 设置加密后字符串结果的长度
     * @param length 长度
     * @return this, 链式调用
     */
    IEncrypt length(int length);

    /**
     * 解码
     * @param encrypt 密文
     * @return 解码后字符串
     */
    String decode(String encrypt);

    /**
     * 编码
     * @param value 需要编码的字符串
     * @return 编码后的字符串
     */
    String encode(String value);
}
