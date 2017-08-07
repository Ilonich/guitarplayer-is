package ru.ilonich.igps.repository.user;

import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.PasswordResetToken;
import ru.ilonich.igps.model.tokens.VerificationToken;

public interface UserRepository {
    User getById(Integer id);

    User findByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    VerificationToken registerAndCreateVerificationToken(User newUser) throws HmacException;

    User confirmVerification(String token);

    PasswordResetToken createPasswordResetTokenForUserByEmail(String email) throws HmacException;

    User confirmReset(String token, String newPass);
}
