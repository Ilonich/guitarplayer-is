package ru.ilonich.igps.config;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MailConfigTest {
    @Test
    @Ignore
    public void javaMailService() throws Exception {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPort(465);
        javaMailSender.setHost("smtp.yandex.ru");
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setUsername("");
        javaMailSender.setPassword("");
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailSender.setJavaMailProperties(mailProperties);
        javaMailSender.setProtocol("smtps");
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo("");
        email.setFrom("");
        email.setSubject("test");
        email.setText("text");
        javaMailSender.send(email);
    }

}