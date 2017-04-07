package me.jarkimzhu.libs.message;

import me.jarkimzhu.libs.user.IUser;

/**
 * @author JarkimZhu
 *         Created on 2017/1/24.
 * @since jdk1.8
 */
public class SimpleMessage implements IMessage {

    private IUser from;
    private IUser[] recipients;
    private String title;
    private String content;
    private String[] attachments;

    @Override
    public IUser getFrom() {
        return from;
    }

    @Override
    public void setFrom(IUser from) {
        this.from = from;
    }

    @Override
    public IUser[] getRecipients() {
        return recipients;
    }

    @Override
    public void setRecipients(IUser[] recipients) {
        this.recipients = recipients;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String[] getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }
}
