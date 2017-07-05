package ru.ilonich.igps.events;

import org.springframework.context.ApplicationEvent;
import ru.ilonich.igps.model.User;

import java.util.Locale;

public class OnRegistrationSuccessEvent extends ApplicationEvent {

    private String token;
    private Locale locale;
    private User user;
    private String appUrl;

    public OnRegistrationSuccessEvent(User user, Locale locale, String token, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.token = token;
        this.appUrl = appUrl;
    }
}
