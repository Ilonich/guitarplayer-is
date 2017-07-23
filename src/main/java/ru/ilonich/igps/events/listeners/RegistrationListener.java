package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    @Autowired
    JavaMailSender javaMailService;

    @Override
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        javaMailService.send(constructMessage(event));
    }

    private SimpleMailMessage constructMessage(OnRegistrationSuccessEvent event){
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(event.getToken().getUser().getEmail());
        email.setFrom("test@tt.ss");
        email.setSubject("Registration verification");
        email.setText(event.getAppUrl() + event.getToken().getToken());
        return email;
    }
}
