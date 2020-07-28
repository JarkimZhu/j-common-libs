package me.jarkimzhu.libs.message.email;

import me.jarkimzhu.libs.message.IMessage;
import me.jarkimzhu.libs.message.IMessageClient;
import me.jarkimzhu.libs.user.IUser;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * @author JarkimZhu
 *         Created on 2017/1/23.
 * @since jdk1.8
 */
public class EmailMessageClient implements IMessageClient {

    private static final String CHARSET = "UTF-8";

    private Session session;

    /**
     *
     * @param props 创建参数配置, 用于连接邮件服务器的参数配置
     */
    public EmailMessageClient(Properties props) {
        // 根据配置创建会话对象, 用于和邮件服务器交互
        session = Session.getDefaultInstance(props);
    }

    @Override
    public void sendMessage(IMessage message) throws MessagingException, UnsupportedEncodingException {
        // 3. 创建一封邮件
        MimeMessage mimeMessage = createMimeMessage(session, message);
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        try {
            // 5. 使用 邮箱账号 和 密码 连接邮件服务器
            //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
            transport.connect(message.getFrom().getEmail(), message.getFrom().getPassword());
            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } finally {
            // 7. 关闭连接
            transport.close();
        }
    }

    private MimeMessage createMimeMessage(Session session, IMessage message) throws MessagingException, UnsupportedEncodingException {
        // 1. 创建一封邮件
        MimeMessage mimeMessage = new MimeMessage(session);

        // 2. From: 发件人
        mimeMessage.setFrom(new InternetAddress(message.getFrom().getEmail(), message.getFrom().getRealName(), CHARSET));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        for(IUser to : message.getRecipients()) {
            mimeMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to.getEmail(), to.getRealName(), CHARSET));
        }

        // 4. Subject: 邮件主题
        mimeMessage.setSubject(message.getTitle(), CHARSET);

        if(message.getAttachments() == null || message.getAttachments().length == 0) {
            // 5. Content: 邮件正文（可以使用html标签）
            mimeMessage.setContent(message.getContent(), "text/html;charset=UTF-8");
        } else {
            // 5. 发送附件
            Multipart multipart = setupForAttachments(message);
            mimeMessage.setContent(multipart);
        }

        // 6. 设置发件时间
        mimeMessage.setSentDate(new Date());

        // 7. 保存设置
        mimeMessage.saveChanges();

        return mimeMessage;
    }

    private Multipart setupForAttachments(IMessage message) throws MessagingException, UnsupportedEncodingException {
        Multipart multipart = new MimeMultipart();

        BodyPart bpContent = new MimeBodyPart();
        bpContent.setContent(message.getContent(), "text/html;charset=UTF-8");
        // 在组件上添加邮件文本
        multipart.addBodyPart(bpContent);

        for(String filePath : message.getAttachments()) {
            BodyPart bpAttachments = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(filePath);
            bpAttachments.setDataHandler(new DataHandler(fds));
            // 解决附件名称乱码
            bpAttachments.setFileName(MimeUtility.encodeText(fds.getName(), "utf-8", null));
            // 添加附件
            multipart.addBodyPart(bpAttachments);
        }
        return multipart;
    }
}
