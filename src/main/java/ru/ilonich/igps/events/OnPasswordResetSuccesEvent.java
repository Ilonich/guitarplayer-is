package ru.ilonich.igps.events;

import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnPasswordResetSuccesEvent extends ApplicationEvent {

    private Locale locale;
    private String newPass;
    private String email;

    public OnPasswordResetSuccesEvent(Locale locale, String newPass, String email) {
        super(newPass);
        this.locale = locale;
        this.newPass = newPass;
        this.email = email;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getNewPass() {
        return newPass;
    }

    public String getEmail() {
        return email;
    }
}
