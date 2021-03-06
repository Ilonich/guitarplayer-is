package ru.ilonich.igps.events;

import org.springframework.context.ApplicationEvent;
import ru.ilonich.igps.model.tokens.VerificationToken;

import java.util.Locale;

public class OnRegistrationSuccessEvent extends ApplicationEvent {

    private Locale locale;
    private VerificationToken token;
    private String appUrl;

    public OnRegistrationSuccessEvent(VerificationToken token, Locale locale, String appUrl) {
        super(token);
        this.token = token;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public VerificationToken getToken() {
        return token;
    }

    public String getAppUrl() {
        return appUrl;
    }
}
