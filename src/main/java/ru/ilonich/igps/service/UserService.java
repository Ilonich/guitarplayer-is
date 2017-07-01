package ru.ilonich.igps.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.utils.custom.FieldValueExists;

public interface UserService extends UserDetailsService, FieldValueExists {

    User getById(Integer id);

    VerificationToken registerAndCreateVerificationToken(User user) throws HmacException;

    boolean confirmRegistration(String token);
}
