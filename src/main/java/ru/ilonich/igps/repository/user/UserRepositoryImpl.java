package ru.ilonich.igps.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.PasswordResetToken;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.tokens.PasswordResetTokenRepository;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;
import ru.ilonich.igps.utils.PasswordUtil;

@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private CrudUser crudUser;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public User getById(Integer id) {
        return crudUser.getById(id);
    }

    @Override
    public User findByEmail(String email) {
        return crudUser.findByEmail(email.toLowerCase());
    }

    @Override
    public User save(User user) {
        return crudUser.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return crudUser.existsByEmail(email.toLowerCase());
    }

    @Override
    public boolean existsByUsername(String username) {
        return crudUser.existsByUsername(username);
    }

    @Override
    @Transactional
    public VerificationToken registerAndCreateVerificationToken(User newUser) throws HmacException {
        User created = crudUser.save(newUser);
        return verificationTokenRepository.save(new VerificationToken(created));
    }

    @Override
    @Transactional
    public User confirmVerification(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken != null){
            User user = verificationToken.getUser();
            user.setEnabled(true);
            crudUser.save(user);
            verificationTokenRepository.delete(verificationToken);
            return user;
        }
        return null;
    }


    @Override
    @Transactional
    public PasswordResetToken createPasswordResetTokenForUserByEmail(String email) throws HmacException {
        User toReset = crudUser.findByEmail(email.toLowerCase());
        return toReset == null ? null : passwordResetTokenRepository.save(new PasswordResetToken(toReset));
    }

    @Override
    @Transactional
    public User confirmReset(String token, String newPass) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken != null){
            User user = passwordResetToken.getUser();
            user.setPassword(PasswordUtil.encode(newPass));
            crudUser.save(user);
            passwordResetTokenRepository.delete(passwordResetToken);
            return user;
        }
        return null;
    }
}
