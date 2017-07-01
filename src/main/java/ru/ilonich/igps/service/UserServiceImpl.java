package ru.ilonich.igps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.UserRepository;
import ru.ilonich.igps.utils.ValidationUtil;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getById(Integer id) {
        User user = userRepository.getById(id);
        ValidationUtil.checkNotFoundWithId(user, id);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException(String.format("User with email \"%s\" not found", email));
        }
        return new AuthenticatedUser(user);
    }

    @Override
    public VerificationToken registerAndCreateVerificationToken(User user) throws HmacException {
        ValidationUtil.checkNew(user);
        return userRepository.registerAndCreateVerificationToken(user);
    }

    @Override
    public boolean confirmRegistration(String token) {
        Assert.notNull(token);
        return userRepository.confirmRegistration(token);
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        Assert.notNull(fieldName);
        if (fieldName.equals("email")) {
            return value != null && this.userRepository.existsByEmail(value.toString());
        } else if (fieldName.equals("username")) {
            return value != null && this.userRepository.existsByUsername(value.toString());
        } else {
            throw new UnsupportedOperationException("Field name not supported");
        }
    }
}
