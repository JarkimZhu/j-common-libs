package me.jarkimzhu.libs.message;

/**
 * @author JarkimZhu
 *         Created on 2017/1/23.
 * @since jdk1.8
 */
public interface IMessageClient {
    void sendMessage(IMessage message) throws Exception;
}
