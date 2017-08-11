package ru.ilonich.igps.service;

import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.model.tokens.LoginSecretKeysPair;

import java.time.OffsetDateTime;

public interface LoginSecretKeysPairStoreService {
    void store(LoginSecretKeysPair loginSecretKeysPair);
    void remove(String login);
    int removeAllExpiried(OffsetDateTime now);
    LoginSecretKeysPair get(String login) throws ExpiredAuthenticationException;
}
