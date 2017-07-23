package ru.ilonich.igps.events.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;

@Component
public class PasswordResetSuccessListener implements ApplicationListener<OnPasswordResetSuccesEvent> {

    @Autowired
    JavaMailSender javaMailService;

    @Override
    public void onApplicationEvent(OnPasswordResetSuccesEvent event) {
        javaMailService.send(constructMessage(event));
    }

    private SimpleMailMessage constructMessage(OnPasswordResetSuccesEvent event) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(event.getEmail());
        email.setFrom("test@tt.ss");
        email.setSubject("New password");
        email.setText(event.getNewPass());
        return email;
    }
}
