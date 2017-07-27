package ru.ilonich.igps.service;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import ru.ilonich.igps.config.MailConfig;
import ru.ilonich.igps.events.OnPasswordResetEvent;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;
import freemarker.template.Configuration;
import ru.ilonich.igps.utils.MessageUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ConditionalOnBean(MailConfig.class)
@Service
public class MailServiceImpl implements MailService {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String SYSTEM_MAIL = "no-reply@igps.ru";
    private static final String ACCOUNT_CONFIRMATION = "mail.account.confirmation";
    private static final String RESET_CONFIRMATION = "mail.reset.confirmation";
    private static final String NEW_PASSWORD = "mail.new.password";

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    Configuration freeMarkerConfigurationFactory;

    @Autowired
    MessageUtil messageUtil;

    @Override
    public void sendConfirmEmailMessageFromEvent(OnRegistrationSuccessEvent event) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(messageUtil.getMessage(ACCOUNT_CONFIRMATION, event.getLocale()));
            mimeMessageHelper.setFrom(SYSTEM_MAIL);
            mimeMessageHelper.setTo(event.getToken().getUser().getEmail());
            ClassPathResource resource =  new ClassPathResource("email-templates/logo.jpeg");
            Template template = getTemplate(event.getLocale(), MailType.ACCOUNT);
            Map<String, Object> model = new HashMap<>();
            model.put("username", event.getToken().getUser().getUsername());
            model.put("link", event.getAppUrl() + event.getToken().getToken());
            String htmlText = getContentAsString(template, model);
            mimeMessageHelper.setText(htmlText, true);
            mimeMessageHelper.addInline("igpsLogo", resource);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException | IOException e) {
            LOG.warn("Failed to send message ({}) \n {}", event.getToken().getUser().getEmail(), e.getMessage());
        } catch (TemplateException e) {
            LOG.warn("Freemarker template -> string failed", e);
        }
    }



    @Override
    public void sendConfirmResetMessageFromEvent(OnPasswordResetEvent event) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(messageUtil.getMessage(RESET_CONFIRMATION, event.getLocale()));
            mimeMessageHelper.setFrom(SYSTEM_MAIL);
            mimeMessageHelper.setTo(event.getToken().getUser().getEmail());
            Template template = getTemplate(event.getLocale(), MailType.RESET);
            Map<String, Object> model = new HashMap<>();
            model.put("username", event.getToken().getUser().getUsername());
            model.put("link", event.getAppUrl() + event.getToken().getToken());
            String htmlText = getContentAsString(template, model);
            mimeMessageHelper.setText(htmlText, true);
            ClassPathResource resource =  new ClassPathResource("email-templates/logo.jpeg");
            mimeMessageHelper.addInline("igpsLogo", resource);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException | IOException e) {
            LOG.warn("Failed to send message ({}) \n {}", event.getToken().getUser().getEmail(), e.getMessage());
        } catch (TemplateException e) {
            LOG.warn("Freemarker template -> string failed", e);
        }
    }

    @Override
    public void sendNewPasswordMessageFromEvent(OnPasswordResetSuccesEvent event) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(messageUtil.getMessage(NEW_PASSWORD, event.getLocale()));
            mimeMessageHelper.setFrom(SYSTEM_MAIL);
            mimeMessageHelper.setTo(event.getEmail());
            Template template = getTemplate(event.getLocale(), MailType.NEW_PASSWORD);
            Map<String, Object> model = Collections.singletonMap("password", event.getNewPass());
            String htmlText = getContentAsString(template, model);
            mimeMessageHelper.setText(htmlText, true);
            ClassPathResource resource =  new ClassPathResource("email-templates/logo.jpeg");
            mimeMessageHelper.addInline("igpsLogo", resource);
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException | IOException e) {
            LOG.warn("Failed to send message ({}) \n {}", event.getEmail(), e.getMessage());
        } catch (TemplateException e) {
            LOG.warn("Freemarker template -> string failed", e);
        }
    }

    private Template getTemplate(Locale locale, MailType type) throws IOException {
        switch (type) {
            case ACCOUNT:
                return freeMarkerConfigurationFactory.getTemplate("email-confirmation.html", locale);
            case RESET:
                return freeMarkerConfigurationFactory.getTemplate("reset-confirmation.html", locale);
            case NEW_PASSWORD:
                return freeMarkerConfigurationFactory.getTemplate("new-password.html", locale);
            default:
                throw new IllegalArgumentException("MailType not defined");
        }
    }

    private String getContentAsString(Template template, Map<String, Object> model) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private enum MailType {
        ACCOUNT,
        RESET,
        NEW_PASSWORD
    }
}
