package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    @Autowired
    JavaMailSender javaMailService;

    @Override
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        //TODO
    }
}
