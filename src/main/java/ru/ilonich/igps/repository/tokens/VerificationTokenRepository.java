package ru.ilonich.igps.repository.tokens;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.model.tokens.VerificationToken;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{

    @Override
    @Transactional
    VerificationToken save(VerificationToken verificationToken);

    @EntityGraph(attributePaths={"user.roles"})
    @Query("SELECT t FROM VerificationToken t WHERE t.user.id=?1")
    VerificationToken findByEmail(String email);

    @EntityGraph(attributePaths={"user.roles"})
    @Query("SELECT t FROM VerificationToken t WHERE t.token=?1")
    VerificationToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expirationDate <= ?1")
    void deleteAllExpiredTokens(LocalDateTime localDateTime);
}
