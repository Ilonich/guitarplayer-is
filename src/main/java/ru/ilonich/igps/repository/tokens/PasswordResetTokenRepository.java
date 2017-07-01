package ru.ilonich.igps.repository.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.model.tokens.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    @Override
    @Transactional
    PasswordResetToken save(PasswordResetToken verificationToken);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.user.email=?1")
    PasswordResetToken findByEmail(String email);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.token=?1")
    PasswordResetToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expirationDate <= ?1")
    Stream<PasswordResetToken> deleteAllExpiredTokens(LocalDateTime localDateTime);
}
