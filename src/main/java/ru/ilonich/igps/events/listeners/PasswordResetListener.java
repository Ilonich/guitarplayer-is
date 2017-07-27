package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnPasswordResetEvent;
import ru.ilonich.igps.service.MailService;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    MailService mailService;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        mailService.sendConfirmResetMessageFromEvent(event);
    }
}
