package org.tree.commons.support.service.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author er_dong_chen
 * @date 2018/12/14
 */
@Lazy
@Component
public class EmailSender {
    private JavaMailSenderImpl sender;

    @Value("${email.host:}")
    private String emailHost;
    @Value("${email.port:25}")  // 25 是 SMTP 标准端口
    private int emailPort;
    @Value("${email.username:}")
    private String emailUsername;
    @Value("${email.password:}")
    private String emailPassword;

    public EmailSender() {
        sender = new JavaMailSenderImpl();
    }

    @PostConstruct
    public void init() {
        sender.setHost(emailHost);
        sender.setPort(emailPort);
        sender.setUsername(emailUsername);
        sender.setPassword(emailPassword);
    }

    public void send(String to, String subject, String text) {
        if (emailUsername.matches("\\w+@.*\\.com")) {
            send(emailUsername, to, subject, text);
            return;
        }
        System.err.println("error: 需要输入己方的邮箱地址 !");
    }

    public void send(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
        System.out.println("success to send email");
    }

    public void sendHtml(String to, String subject, String html) throws MessagingException {
        if (emailUsername.matches("\\w+@.*\\.com")) {
            sendHtml(emailUsername, to, subject, html);
            return;
        }
        System.err.println("error: 需要输入己方的邮箱地址 !");
    }

    public void sendHtml(String from, String to, String subject, String html) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        sender.send(message);
        System.out.println("success to send email");
    }
}
