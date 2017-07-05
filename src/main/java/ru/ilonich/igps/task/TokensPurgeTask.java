package ru.ilonich.igps.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.repository.tokens.PasswordResetTokenRepository;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;
import ru.ilonich.igps.service.LoginSecretKeysPairStoreService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@Transactional
public class TokensPurgeTask {

    @Autowired
    LoginSecretKeysPairStoreService storeService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Scheduled(cron = "0 0/30 0 * * *")
    public void purgeExpired(){
        OffsetDateTime offsetNow = OffsetDateTime.now();
        storeService.removeAllExpiried(offsetNow);
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.deleteAllExpiredTokens(now);
        verificationTokenRepository.deleteAllExpiredTokens(now);
    }
}
