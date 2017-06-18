package ru.ilonich.igps.comtroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/authenticate")
    public AuthTO authenticate(@Valid @RequestBody LoginTO loginTO, HttpServletResponse response) throws Exception {
        return authenticationService.authenticate(loginTO, response);
    }

    @PostMapping(value = "/register")
    public RegisterTO register(@Valid @RequestBody RegisterTO registerTO, HttpServletResponse response) throws Exception {
        return null;
    }

    @GetMapping(value = "/logout")
    public void logout(){
        authenticationService.logout();
    }
}
