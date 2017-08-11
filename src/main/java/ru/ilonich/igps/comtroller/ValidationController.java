package ru.ilonich.igps.comtroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ilonich.igps.exception.ApplicationException;
import ru.ilonich.igps.service.FrontendValidationService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ValidationController {

    @Autowired
    private FrontendValidationService validationService;

    @PostMapping
    public Map<String, Boolean> validate(@RequestBody Map<String, String> propertyAndValue) {
        if (propertyAndValue.size() > 1 || propertyAndValue.size() == 0){
            throw new ApplicationException("exception.badrequest", HttpStatus.BAD_REQUEST);
        }
        Map.Entry<String, String> single = propertyAndValue.entrySet().iterator().next();
        return Collections.singletonMap("valid", validationService.validate(single.getKey(), single.getValue()));
    }
}
