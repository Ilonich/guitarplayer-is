package ru.ilonich.igps.comtroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.to.LoginTO;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User authenticate(@RequestBody LoginTO loginTO, HttpServletResponse response) throws Exception{
        return authenticationService.authenticate(loginTO, response);
    }

    @GetMapping(value = "/logout")
    public void logout(){
        authenticationService.logout();
    }
}
