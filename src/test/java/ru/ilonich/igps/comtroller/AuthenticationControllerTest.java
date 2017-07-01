package ru.ilonich.igps.comtroller;


import com.google.common.cache.LoadingCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.model.tokens.KeyPair;
import ru.ilonich.igps.service.LoginAttemptService;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;

import javax.servlet.http.Cookie;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;
import static ru.ilonich.igps.utils.JsonUtil.*;

public class AuthenticationControllerTest extends AbstractControllerTest {

    @Autowired
    private LoadingCache<String, KeyPair> keyStore;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @After
    public void clearLoginAttempt(){
        loginAttemptService.clearCache();
    }

    @Test
    public void authenticateMod() throws Exception {
        MvcResult result = authenticate("mod@igps.ru", "banme").andReturn();
        Assert.assertNotNull(result);
        if (result.getResponse().getStatus() == 200) {
            assertTrue(!result.getResponse().getHeader(X_SECRET.toString()).isEmpty());
            assertTrue(!result.getResponse().getHeader(CSRF_CLAIM_HEADER.toString()).isEmpty());
            assertTrue(!result.getResponse().getHeader(X_TOKEN_ACCESS.toString()).isEmpty());
            assertTrue(result.getResponse().getCookie(JWT_APP_COOKIE.toString()) != null);
        }
        AuthTO user = readValue(result.getResponse().getContentAsString(), AuthTO.class);
        assertEquals(user.getUsername(), UserTestData.moderator.getUsername());
    }

    @Test
    public void badCredentials() throws Exception {
        authenticate("mod@igps.ru","wrongPassword").andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    public void locale() throws Exception {
        mockMVC.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new LoginTO("mod@igps.ru","wrongPassword")))
                .cookie(new Cookie("locale", "en")))
        .andDo(print()).andExpect(content().string("{\"url\":\"http://localhost/api/authenticate\",\"cause\":\"BadCredentialsException\",\"details\":[\"Wrong login or password\"]}"));
    }


    @Test
    public void tooManyLoginAttempts() throws Exception {
        for (int i = 0; i < 10; i++) {
            authenticate("mod@igps.ru","wrongPassword");
        }
        authenticate("mod@igps.ru","wrongPassword").andExpect(status().isLocked()).andDo(print());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void registerSuccess() throws Exception {
        RegisterTO registerTO = new RegisterTO("someone", "test@test.com", "12345", "12345");
        MvcResult result = mockMVC.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(registerTO)))
                .andExpect(status().is(201))
                .andDo(print())
                .andReturn();
        assertTrue(!result.getResponse().getHeader(X_SECRET.toString()).isEmpty());
        assertTrue(!result.getResponse().getHeader(CSRF_CLAIM_HEADER.toString()).isEmpty());
        assertTrue(!result.getResponse().getHeader(X_TOKEN_ACCESS.toString()).isEmpty());
        assertTrue(result.getResponse().getCookie(JWT_APP_COOKIE.toString()) != null);
        assertTrue(keyStore.getIfPresent(registerTO.getEmail()) != null);
    }

    @Test
    public void logoutSuccess() throws Exception {
        LoginTO loginTO = new LoginTO("mod@igps.ru", "banme");
        mockMVC.perform(authenticatedRequest(loginTO, RequestMethod.GET, "/api/logout", null))
                .andExpect(status().is(200));
        assertTrue(keyStore.getIfPresent(loginTO.getLogin()) == null);
    }

    @Test
    public void registerEmailInvalid() throws Exception {
        mockMVC.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(new RegisterTO("someone", "asd@@.asd.ro", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameAlreadyExists() throws Exception {
        mockMVC.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("Модератор", "asd@asd.ri", "12345", "12345"))))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidPattern() throws Exception {
        mockMVC.perform(post("/api/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(new RegisterTO("123\r\n 1234  1235", "asd@asd.ri", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidLength() throws Exception {
        mockMVC.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("1", "asd@asd.ry", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidBlank() throws Exception {
        mockMVC.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("", "asd@asd.ry", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerPasswordsNotEquals() throws Exception {
        mockMVC.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("fga", "asd@asd.ry", "123455", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}