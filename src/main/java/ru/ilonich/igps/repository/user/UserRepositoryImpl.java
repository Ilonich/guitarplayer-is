package ru.ilonich.igps.repository.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;

@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private CrudUser crudUser;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

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
        return crudUser.existsByEmail(email);
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
    public boolean confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken != null){
            User user = verificationToken.getUser();
            user.setEnabled(true);
            crudUser.save(user);
            return true;
        }
        return false;
    }


}
