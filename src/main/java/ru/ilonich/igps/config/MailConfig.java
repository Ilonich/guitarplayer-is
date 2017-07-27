package ru.ilonich.igps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

@Configuration
@ComponentScan({"ru.ilonich.igps.events.listeners"})
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
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
        return javaMailSender;
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactory() {
        FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();
        fmConfigFactoryBean.setTemplateLoaderPath("classpath:/email-templates/");
        return fmConfigFactoryBean;
    }

}
