package me.jarkimzhu.libs.message;

import me.jarkimzhu.libs.user.IUser;

/**
 * @author JarkimZhu
 *         Created on 2017/1/23.
 * @since jdk1.8
 */
public interface IMessage {

    IUser getFrom();

    void setFrom(IUser from);

    IUser[] getRecipients();

    void setRecipients(IUser[] recipients);

    String getTitle();

    void setTitle(String title);

    String getContent();

    void setContent(String content);

    String[] getAttachments();

    void setAttachments(String[] attachments);

}
