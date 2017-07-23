package ru.ilonich.igps.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.utils.custom.FieldValueExists;

public interface UserService extends UserDetailsService, FieldValueExists {

    User getById(Integer id);

    User register(User user, String confirmationUrl) throws HmacException;

    boolean confirmRegistration(String token);

    boolean initiatePasswordReset(String email, String confirmationUrl) throws HmacException;

    boolean confirmPasswordReset(String token);
}
