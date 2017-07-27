package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;
import ru.ilonich.igps.service.MailService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    @Autowired
    MailService mailService;

    @Override
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        mailService.sendConfirmEmailMessageFromEvent(event);
    }
}
