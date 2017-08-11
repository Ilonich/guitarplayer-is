package ru.ilonich.igps.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import ru.ilonich.igps.service.MailServiceImpl;

import java.util.Properties;

@Configuration
@ComponentScan({"ru.ilonich.igps.events.listeners"})
@Import(MailServiceImpl.class)
public class MailConfig {

    @Value("${ya.mail.user}")
    private String username;
    @Value("${ya.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPort(25);
        javaMailSender.setHost("smtp.jeffs.ru");
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setProtocol("smtp");
        return javaMailSender;
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactory() {
        FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();
        fmConfigFactoryBean.setTemplateLoaderPath("classpath:/email-templates/");
        return fmConfigFactoryBean;
    }

}
