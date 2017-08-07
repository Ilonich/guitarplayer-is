package ru.ilonich.igps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.ilonich.igps.events.OnPasswordResetEvent;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.PasswordResetToken;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.user.UserRepository;
import ru.ilonich.igps.utils.PasswordGeneratorUtil;
import ru.ilonich.igps.utils.ValidationUtil;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getById(Integer id) {
        User user = userRepository.getById(id);
        ValidationUtil.checkNotFoundWithId(user, id);
        return user;
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException(String.format("User with email \"%s\" not found", email));
        }
        return new AuthenticatedUser(user);
    }

    @Override
    public User register(User user, String confirmationUrl) throws HmacException {
        ValidationUtil.checkNew(user);
        VerificationToken token = userRepository.registerAndCreateVerificationToken(user);
        eventPublisher.publishEvent(new OnRegistrationSuccessEvent(token, LocaleContextHolder.getLocale(), confirmationUrl));
        return token.getUser();
    }

    @Override
    public boolean confirmRegistration(String token) {
        Assert.notNull(token);
        return userRepository.confirmVerification(token) != null;
    }

    @Override
    public boolean initiatePasswordReset(String email, String confirmationUrl) throws HmacException {
        PasswordResetToken token = userRepository.createPasswordResetTokenForUserByEmail(email);
        if (token != null) {
            eventPublisher.publishEvent(new OnPasswordResetEvent(token, LocaleContextHolder.getLocale(), confirmationUrl));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean confirmPasswordReset(String token) {
        Assert.notNull(token);
        String newPass = PasswordGeneratorUtil.generate();
        User user = userRepository.confirmReset(token, newPass);
        if (user != null){
            eventPublisher.publishEvent(new OnPasswordResetSuccesEvent(LocaleContextHolder.getLocale(), newPass, user.getEmail()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        Assert.notNull(fieldName);
        switch (fieldName) {
            case "email":
                return value != null && this.userRepository.existsByEmail(value.toString());
            case "username":
                return value != null && this.userRepository.existsByUsername(value.toString());
            default:
                throw new UnsupportedOperationException("Field name not supported");
        }
    }
}
