package ru.ilonich.igps.repository;

import ru.ilonich.igps.model.User;

public interface UserRepository {
    User findByEmail(String email);
    User save(User user);
}
