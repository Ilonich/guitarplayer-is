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
import ru.ilonich.igps.repository.tokens.PasswordResetTokenRepository;
import ru.ilonich.igps.repository.tokens.VerificationTokenRepository;
import ru.ilonich.igps.utils.PasswordUtil;

import static org.junit.Assert.*;
import static ru.ilonich.igps.UserTestData.USER_MODEL_MATCHER;

@SpringBootTest(classes = JpaConfig.class)
@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Test
    public void getById() throws Exception {
        User user = userService.getById(100003);
        USER_MODEL_MATCHER.assertEquals(UserTestData.TYPICAL_USER, user);
    }

    @Test(expected = NotFoundException.class)
    public void getByIdNotFound() throws Exception {
        userService.getById(100030);
    }

    @Test
    public void loadUserByUsername() throws Exception {
        User user = ((AuthenticatedUser) userService.loadUserByUsername("mod@igps.ru")).getUser();
        USER_MODEL_MATCHER.assertEquals(UserTestData.MODERATOR, user);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameUnknown() throws Exception {
        userService.loadUserByUsername("demon");
    }

    @Test
    public void register() throws Exception {
        User user = userService.register(UserTestData.SOME_NEW, "url");
        assertNotNull(user);
        assertEquals(UserTestData.SOME_NEW.getId(), user.getId());
        assertEquals(UserTestData.SOME_NEW.getId(), verificationTokenRepository.findByEmail(user.getEmail()).getUser().getId());
    }

    @Test
    public void confirmRegistration() throws Exception {
        boolean test = userService.confirmRegistration("YzZhZDk5YjAtYWM1ZC00YjgxLWEyNTAtYWY3MmFiNmFmZGIz");
        assertTrue(test);
        User confirmed = userService.getById(100003);
        assertTrue(confirmed.isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmRegistrationFail() throws Exception {
        assertFalse(userService.confirmRegistration(""));
        userService.confirmRegistration(null);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void fieldValueExists() throws Exception {
        assertTrue(userService.fieldValueExists("Модератор", "username"));
        assertFalse(userService.fieldValueExists("Somth", "username"));
        assertTrue(userService.fieldValueExists("mod@igps.rU", "email"));
        assertFalse(userService.fieldValueExists("asd@asd.ls", "email"));
        userService.fieldValueExists("", "male");
    }

    @Test
    public void initiatePasswordReset() throws Exception {
        assertTrue(userService.initiatePasswordReset("mod@igps.rU", "url"));
        assertFalse(userService.initiatePasswordReset("wrong@email.ro", "url"));
        assertEquals(UserTestData.MODERATOR.getId(), resetTokenRepository.findByEmail(UserTestData.MODERATOR.getEmail()).getUser().getId());
    }

    @Test
    public void confirmPasswordReset() throws Exception {
        String oldPass = UserTestData.TYPICAL_USER.getPassword();
        assertTrue(userService.confirmPasswordReset("ZjQ2YTc3NDYtODc5MC00Yjc0LWFiMjYtMzVlODYzN2ZhNTE1"));
        String newPass = userService.getById(UserTestData.TYPICAL_USER.getId()).getPassword();
        assertTrue(PasswordUtil.isMatch("likeme", oldPass));
        assertFalse(PasswordUtil.isMatch("likeme", newPass));
    }

    @Test(expected = IllegalArgumentException.class)
    public void confirmPasswordResetFail() throws Exception {
        assertFalse(userService.confirmPasswordReset(""));
        userService.confirmPasswordReset(null);
    }
}