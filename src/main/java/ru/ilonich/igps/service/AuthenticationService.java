package ru.ilonich.igps.service;

import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.to.LoginTO;

import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    User authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException;
    void logout();
    void authenticateByToken(String username);
}
