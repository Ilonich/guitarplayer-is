package ru.ilonich.igps.comtroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ilonich.igps.comtroller.advice.ExceptionInfoHandler;
import ru.ilonich.igps.exception.ApplicationException;
import ru.ilonich.igps.exception.NotFoundException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.LoginAttemptService;
import ru.ilonich.igps.service.ResetAttemptService;
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
    private static final String AUTH_BLOCKED = "exception.blocked.auth";
    private static final String RESET_BLOCKED = "exception.blocked.reset";
    private static final String NOT_FOUND_EMAIL = "exception.notFound.acc";

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private ResetAttemptService resetAttemptService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ExceptionInfoHandler exceptionInfoHandler;

    @PostMapping(value = "/authenticate")
    public AuthTO authenticate(@Valid @RequestBody LoginTO loginTO, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (loginAttemptService.isBlocked(request.getRemoteAddr())){
            throw new ApplicationException(AUTH_BLOCKED, HttpStatus.LOCKED);
        }
        AuthTO result = authenticationService.authenticate(loginTO, response);
        loginAttemptService.loginSucceeded(request.getRemoteAddr());
        return result;
    }

    @PostMapping(value = "/reset")
    public ResponseEntity initiateReset(@RequestBody String email, HttpServletRequest request) throws Exception {
        if (resetAttemptService.isBlocked(request.getRemoteAddr())){
            throw new ApplicationException(RESET_BLOCKED, HttpStatus.LOCKED);
        }
        log.info("{} forgot password, trying to reset", email);
        if (authenticationService.initiateReset(email, getAppConfirmResetUrl(request))) {
            resetAttemptService.attempt(request.getRemoteAddr());
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            resetAttemptService.attempt(request.getRemoteAddr());
            throw new NotFoundException(NOT_FOUND_EMAIL, HttpStatus.NOT_FOUND, email);
        }
    }

    @PutMapping(value = "/register")
    public ResponseEntity<AuthTO> register(@Valid @RequestBody RegisterTO registerTO, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = authenticationService.register(registerTO, getAppConfirmEmailUrl(request));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("users/{id}")
                .buildAndExpand(user.getId()).toUri();
        log.info("New user registered - {}", user.toString());
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

    private String getAppConfirmEmailUrl(HttpServletRequest request) {
        return "https://" + request.getServerName() + ":" + request.getServerPort() + "/confirm-email";
    }

    private String getAppConfirmResetUrl(HttpServletRequest request) {
        return "https://" + request.getServerName() + ":" + request.getServerPort() + "/confirm-reset";
    }
}
