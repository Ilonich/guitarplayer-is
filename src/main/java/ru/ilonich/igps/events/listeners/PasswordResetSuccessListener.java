package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;
import ru.ilonich.igps.service.MailService;

@Component
public class PasswordResetSuccessListener implements ApplicationListener<OnPasswordResetSuccesEvent> {

    @Autowired
    MailService mailService;

    @Override
    public void onApplicationEvent(OnPasswordResetSuccesEvent event) {
        mailService.sendNewPasswordMessageFromEvent(event);
    }
}
