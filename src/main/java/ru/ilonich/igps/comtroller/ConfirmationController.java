package ru.ilonich.igps.comtroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ilonich.igps.service.UserService;

@RestController
@RequestMapping(value = "/api/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfirmationController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/email/{token}")
    public ResponseEntity confirmEmail(@PathVariable("token") String token){
        if (userService.confirmRegistration(token)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.GONE);
        }
    }

    @GetMapping(value = "/reset/{token}")
    public ResponseEntity confirmPasswordReset(@PathVariable("token") String token){
        if (userService.confirmPasswordReset(token)){
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity(HttpStatus.GONE);
        }
    }
}
