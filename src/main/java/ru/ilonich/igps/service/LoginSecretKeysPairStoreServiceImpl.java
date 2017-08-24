package ru.ilonich.igps.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.model.tokens.LoginSecretKeysPair;
import ru.ilonich.igps.repository.tokens.KeyPairRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service("keysStoreService")
public class LoginSecretKeysPairStoreServiceImpl implements LoginSecretKeysPairStoreService{

    @Autowired
    private KeyPairRepository keyPairRepository;

    private LoadingCache<Integer, LoginSecretKeysPair> keyCache;

    public LoginSecretKeysPairStoreServiceImpl() {
        keyCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .initialCapacity(10)
                .maximumSize(Long.MAX_VALUE)
                .build(new CacheLoader<Integer, LoginSecretKeysPair>() {
                    @Override
                    public LoginSecretKeysPair load(Integer userId) throws Exception {
                        LoginSecretKeysPair result = keyPairRepository.getById(userId);
                        if (result == null) {
                            throw new ExpiredAuthenticationException(userId.toString());
                        }
                        return result;
                    }
                });
    }

    @Override
    public void store(LoginSecretKeysPair loginSecretKeysPair) {
        keyPairRepository.save(loginSecretKeysPair);
        keyCache.put(loginSecretKeysPair.getUserId(), loginSecretKeysPair);
    }

    @Override
    public void remove(Integer userId) {
        keyPairRepository.deleteById(userId);
        keyCache.invalidate(userId);
    }

    @Override
    @Transactional
    public int removeAllExpiried(OffsetDateTime now) {
        List<String> toRemoveFromCache = keyPairRepository.findAllExpiredTokensLogins(now);
        int size = toRemoveFromCache.size();
        keyPairRepository.deleteAllExpiredTokens(now);
        keyCache.invalidateAll(toRemoveFromCache);
        return size;
    }

    @Override
    public LoginSecretKeysPair get(Integer userId) throws ExpiredAuthenticationException {
        try {
            return keyCache.get(userId);
        } catch (ExecutionException e) {
            throw (ExpiredAuthenticationException) e.getCause();
        }
    }

    @Override
    public LoginSecretKeysPair nullableGet(Integer userId) {
        try {
            return keyCache.get(userId);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
