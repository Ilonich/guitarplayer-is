package ru.ilonich.igps.repository.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.model.tokens.LoginSecretKeysPair;

import java.time.OffsetDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface KeyPairRepository extends JpaRepository<LoginSecretKeysPair, String> {

    @Override
    @Transactional
    LoginSecretKeysPair save(LoginSecretKeysPair loginSecretKeysPair);

    @Query("SELECT k FROM LoginSecretKeysPair k WHERE k.emailLogin=?1")
    LoginSecretKeysPair getById(String loginEmail);

    @Query("SELECT k.emailLogin FROM LoginSecretKeysPair k WHERE k.expirationDate <=?1")
    List<String> findAllExpiredTokensLogins(OffsetDateTime now);

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginSecretKeysPair k WHERE k.expirationDate <= ?1")
    void deleteAllExpiredTokens(OffsetDateTime now);

    @Transactional
    @Modifying
    @Query("DELETE FROM LoginSecretKeysPair k WHERE k.emailLogin=?1")
    int deleteById(String loginEmail);
}
