package ru.ilonich.igps.comtroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ilonich.igps.comtroller.advice.ExceptionInfoHandler;
import ru.ilonich.igps.exception.ApplicationException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.LoginAttemptService;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.ErrorInfo;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private static final String BAD_CREDENTIALS = "exception.badCredentials";

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ExceptionInfoHandler exceptionInfoHandler;

    @PostMapping(value = "/authenticate")
    public AuthTO authenticate(@Valid @RequestBody LoginTO loginTO, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (loginAttemptService.isBlocked(request.getRemoteAddr())){
            throw new ApplicationException("exception.blocked", HttpStatus.LOCKED);
        }
        AuthTO result = authenticationService.authenticate(loginTO, response);
        loginAttemptService.loginSucceeded(request.getRemoteAddr());
        return result;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthTO> register(@Valid @RequestBody RegisterTO registerTO, HttpServletResponse response) throws Exception {
        User registered = authenticationService.register(registerTO);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("#/users/{id}")
                .buildAndExpand(registered.getId()).toUri();
        log.info("New user registered - {}", registered.toString());
        return ResponseEntity.created(uriOfNewResource)
                .body(authenticationService.authenticate(registerTO.asLoginTO(), response));
    }

    @GetMapping(value = "/logout")
    public void logout(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        authenticationService.logout(authenticatedUser);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorInfo> badCredentials(HttpServletRequest req, BadCredentialsException e) {
        loginAttemptService.loginFailed(req.getRemoteAddr());
        return exceptionInfoHandler.getErrorInfoResponseEntity(req, e, BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
}
