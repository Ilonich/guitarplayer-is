package ru.ilonich.igps.service;

import ru.ilonich.igps.events.OnPasswordResetEvent;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;


public interface MailService {

    void sendConfirmEmailMessageFromEvent(OnRegistrationSuccessEvent event);

    void sendConfirmResetMessageFromEvent(OnPasswordResetEvent event);

    void sendNewPasswordMessageFromEvent(OnPasswordResetSuccesEvent event);
}
