package ru.ilonich.igps.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.ilonich.igps.model.User;

public interface UserService extends UserDetailsService {
    User save(User user);
}
