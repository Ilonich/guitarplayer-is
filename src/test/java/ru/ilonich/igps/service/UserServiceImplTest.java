package ru.ilonich.igps.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;

import static ru.ilonich.igps.UserTestData.USER_MODEL_MATCHER;

@SpringBootTest(classes = JpaConfig.class)
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

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

}