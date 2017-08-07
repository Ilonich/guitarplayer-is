package ru.ilonich.igps.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.model.tokens.LoginSecretKeysPair;
import ru.ilonich.igps.repository.tokens.KeyPairRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

@SpringBootTest(classes = JpaConfig.class)
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class LoginSecretKeysPairStoreServiceImplTest {
    private static final String TEST_PAIR_ID = "test@asd.ro";
    private static final LoginSecretKeysPair TEST_PAIR = new LoginSecretKeysPair(TEST_PAIR_ID, "public", "private", OffsetDateTime.now());

    @Autowired
    LoginSecretKeysPairStoreService storeService;

    @Autowired
    KeyPairRepository keyPairRepository;

    @Test
    public void store() throws Exception {
        storeService.store(TEST_PAIR);
        LoginSecretKeysPair sameOne = keyPairRepository.getById(TEST_PAIR_ID);
        assertEquals(TEST_PAIR, sameOne);
    }

    @Test(expected = ExpiredAuthenticationException.class)
    public void remove() throws Exception {
        storeService.store(TEST_PAIR);
        storeService.remove(TEST_PAIR_ID);
        assertTrue(keyPairRepository.getById(TEST_PAIR_ID) == null);
        assertTrue(storeService.get(TEST_PAIR_ID) == null);
    }

    @Test(expected = ExpiredAuthenticationException.class)
    public void removeAllExpiried() throws Exception {
        storeService.store(TEST_PAIR);
        storeService.removeAllExpiried(OffsetDateTime.of(2030, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));
        storeService.get(TEST_PAIR_ID);
    }

    @Test
    public void get() throws Exception {
        keyPairRepository.save(TEST_PAIR);
        assertEquals(TEST_PAIR, storeService.get(TEST_PAIR_ID));
    }

}