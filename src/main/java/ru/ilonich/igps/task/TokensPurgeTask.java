package ru.ilonich.igps.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.repository.tokens.PasswordResetTokenRepository;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;
import ru.ilonich.igps.service.LoginSecretKeysPairStoreService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
@Transactional
public class TokensPurgeTask {
    private static final Logger LOG = LoggerFactory.getLogger(TokensPurgeTask.class);

    @Autowired
    LoginSecretKeysPairStoreService storeService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Scheduled(cron = "0 */15 * ? * *")
    public void purgeExpired(){
        OffsetDateTime offsetNow = OffsetDateTime.now();
        int secretCount = storeService.removeAllExpiried(offsetNow);
        LocalDateTime now = LocalDateTime.now();
        int resetCount = passwordResetTokenRepository.deleteAllExpiredTokens(now);
        int confirmCount = verificationTokenRepository.deleteAllExpiredTokens(now);
        LOG.info("Tokens purge finished, stats: \n {} secret tokens deleted \n {} reset tokens deleted \n {} confirmation tokens deleted",
                secretCount, resetCount, confirmCount);
    }
}
