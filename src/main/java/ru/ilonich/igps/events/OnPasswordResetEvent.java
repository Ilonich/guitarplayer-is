package ru.ilonich.igps.events;

import org.springframework.context.ApplicationEvent;
import ru.ilonich.igps.model.tokens.PasswordResetToken;

import java.util.Locale;

public class OnPasswordResetEvent extends ApplicationEvent {

    private Locale locale;
    private PasswordResetToken token;
    private String appUrl;

    public OnPasswordResetEvent(PasswordResetToken token, Locale locale, String url) {
        super(token);
        this.token = token;
        this.locale = locale;
        this.appUrl = url;
    }

    public Locale getLocale() {
        return locale;
    }

    public PasswordResetToken getToken() {
        return token;
    }

    public String getAppUrl() {
        return appUrl;
    }
}
