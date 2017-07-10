package ru.ilonich.igps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.exception.ApplicationException;
import ru.ilonich.igps.repository.user.UserRepository;

@Service
public class FrontendValidationServiceImpl implements FrontendValidationService {
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean validate(String field, String value) {
        switch (field) {
            case "username":
                return !userRepository.existsByUsername(value);
            case "email":
                return !userRepository.existsByEmail(value);
            default:
                throw new ApplicationException("exception.novalidator", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
