package org.tree.commons.support.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.tree.commons.support.service.ServiceConfig;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author er_dong_chen
 * @date 2018/12/14
 */
@Component
public class EmailSender {
    @Autowired
    private ServiceConfig config;
    private JavaMailSenderImpl sender;

    public EmailSender() {
        sender = new JavaMailSenderImpl();
    }

    public EmailSender(ServiceConfig config) {
        this.config = config;
        sender = new JavaMailSenderImpl();
    }

    @PostConstruct
    public void init() {
        sender.setHost(config.getEmailHost());
        sender.setPort(config.getEmailPort());
        sender.setUsername(config.getEmailUsername());
        sender.setPassword(config.getEmailPassword());
    }

    public void send(String to, String subject, String text) {
        if (config.getEmailUsername().matches("\\w+@.*\\.com")) {
            send(config.getEmailUsername(), to, subject, text);
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
        if (config.getEmailUsername().matches("\\w+@.*\\.com")) {
            sendHtml(config.getEmailUsername(), to, subject, html);
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
