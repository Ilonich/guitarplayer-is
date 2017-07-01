package ru.ilonich.igps.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.exception.NotFoundException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ru.ilonich.igps.UserTestData.USER_MODEL_MATCHER;

@SpringBootTest(classes = JpaConfig.class)
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Test
    public void getById() throws Exception {
        User user = userService.getById(100003);
        USER_MODEL_MATCHER.assertEquals(UserTestData.typicalUser, user);
    }

    @Test(expected = NotFoundException.class)
    public void getByIdNotFound() throws Exception {
        userService.getById(100030);
    }

    @Test
    public void loadUserByUsername() throws Exception {
        User user = ((AuthenticatedUser) userService.loadUserByUsername("mod@igps.ru")).getUser();
        USER_MODEL_MATCHER.assertEquals(UserTestData.moderator, user);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameUnknown() throws Exception {
        User user = ((AuthenticatedUser) userService.loadUserByUsername("demon")).getUser();
        USER_MODEL_MATCHER.assertEquals(UserTestData.moderator, user);
    }

    @Test
    public void registerAndCreateVerificationToken() throws Exception {
        VerificationToken testToken = userService.registerAndCreateVerificationToken(UserTestData.someNew);
        assertNotNull(testToken);
        assertEquals(UserTestData.someNew.getId(), testToken.getId());
    }

    @Test
    public void confirmRegistration() throws Exception {
        boolean test = userService.confirmRegistration("YzZhZDk5YjAtYWM1ZC00YjgxLWEyNTAtYWY3MmFiNmFmZGIz");
        assertTrue(test);
        User confirmed = userService.getById(100003);
        assertTrue(confirmed.isEnabled());
    }



}