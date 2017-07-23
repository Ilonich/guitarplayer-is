package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnPasswordResetEvent;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    JavaMailSender javaMailService;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        javaMailService.send(constructMessage(event));
    }

    private SimpleMailMessage constructMessage(OnPasswordResetEvent event){
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(event.getToken().getUser().getEmail());
        email.setFrom("test@tt.ss");
        email.setSubject("Password reset");
        email.setText(event.getAppUrl() + event.getToken().getToken());
        return email;
    }
}
