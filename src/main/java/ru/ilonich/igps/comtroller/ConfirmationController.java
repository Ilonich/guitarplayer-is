package ru.ilonich.igps.comtroller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ConfirmationController {

    @GetMapping(value = "/email/{token}")
    public void confirmEmail(@PathVariable("token") String token){

    }

    @GetMapping(value = "/reset/{token}")
    public void confirmPasswordReset(@PathVariable("token") String token){

    }
}
