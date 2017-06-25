package ru.ilonich.igps.service;

import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;

import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    AuthTO authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException;
    public User register(RegisterTO registerTO);
    void logout();
    void authenticateByToken(String username);
    void authenticateAnonymous();
}
