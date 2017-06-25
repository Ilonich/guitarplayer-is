package ru.ilonich.igps.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.repository.UserRepository;

@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    CrudUser crudUser;

    @Override
    public User findByEmail(String email) {
        return crudUser.findByEmail(email.toLowerCase());
    }

    @Override
    public User save(User user) {
        return crudUser.save(user);
    }


}
