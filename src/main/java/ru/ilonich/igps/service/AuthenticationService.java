package ru.ilonich.igps.service;

import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;

import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    AuthTO authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException;
    User register(RegisterTO registerTO, String confirmUrl) throws HmacException;
    boolean initiateReset(String email, String url) throws HmacException;
    void logout(AuthenticatedUser authenticatedUser);
    void authenticateByToken(Integer userId);
    void authenticateAnonymous();
}
