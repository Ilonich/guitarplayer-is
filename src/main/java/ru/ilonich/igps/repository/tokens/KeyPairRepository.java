package ru.ilonich.igps.repository.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.model.tokens.KeyPair;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Transactional(readOnly = true)
public interface KeyPairRepository extends JpaRepository<KeyPair, String> {

    @Override
    @Transactional
    KeyPair save(KeyPair keyPair);

    @Query("SELECT k FROM KeyPair k WHERE k.emailLogin=?1")
    KeyPair getById(String loginEmail);

    @Transactional
    @Modifying
    @Query("DELETE FROM KeyPair k WHERE k.expirationDate <= ?1")
    Stream<KeyPair> deleteAllExpiredTokens(LocalDateTime localDateTime);

    @Transactional
    @Modifying
    @Query("DELETE FROM KeyPair k WHERE k.emailLogin=?1")
    int deleteById(String loginEmail);
}
