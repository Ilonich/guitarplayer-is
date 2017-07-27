package ru.ilonich.igps.comtroller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.exception.ExpiredAuthenticationException;
import ru.ilonich.igps.service.LoginAttemptService;
import ru.ilonich.igps.service.LoginSecretKeysPairStoreService;
import ru.ilonich.igps.service.ResetAttemptService;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.http.Cookie;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;
import static ru.ilonich.igps.utils.JsonUtil.*;

public class AuthenticationControllerTest extends AbstractControllerTest {

    @Autowired
    private LoginSecretKeysPairStoreService keysStoreService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private ResetAttemptService resetAttemptService;

    @After
    public void clearLoginAttempt(){
        loginAttemptService.clearCache();
        resetAttemptService.clearCache();
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
        assertEquals(user.getUsername(), UserTestData.MODERATOR.getUsername());
    }

    @Test
    public void badCredentials() throws Exception {
        authenticate("mod@igps.ru","wrongPassword").andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    public void locale() throws Exception {
        mockMVC.perform(post("/api/authenticate").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new LoginTO("mod@igps.ru","wrongPassword")))
                .cookie(new Cookie("locale", "en")))
        .andDo(print()).andExpect(content().string(containsString("Wrong login or password")));
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
        MvcResult result = mockMVC.perform(put("/api/register").secure(true)
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(registerTO)))
                .andExpect(status().is(201))
                .andDo(print())
                .andReturn();
        assertTrue(!result.getResponse().getHeader(X_SECRET.toString()).isEmpty());
        assertTrue(!result.getResponse().getHeader(CSRF_CLAIM_HEADER.toString()).isEmpty());
        assertTrue(!result.getResponse().getHeader(X_TOKEN_ACCESS.toString()).isEmpty());
        assertTrue(result.getResponse().getCookie(JWT_APP_COOKIE.toString()) != null);
        assertTrue(keysStoreService.get(registerTO.getEmail()) != null);
    }

    @Test
    public void resetSuccess() throws Exception {
        mockMVC.perform(post("/api/reset").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(UserTestData.TYPICAL_USER.getEmail())))
                .andExpect(status().is(202))
                .andDo(print());

    }

    @Test
    public void resetNotFound() throws Exception {
        mockMVC.perform(post("/api/reset").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue("Nothing@asd.as")))
                .andExpect(status().is(404))
                .andDo(print());
    }

    @Test
    public void resetLock() throws Exception {
        mockMVC.perform(post("/api/reset").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(UserTestData.TYPICAL_USER.getEmail())));
        mockMVC.perform(post("/api/reset").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(UserTestData.TYPICAL_USER.getEmail())))
                .andExpect(status().is(423));
    }

    @Test(expected = ExpiredAuthenticationException.class)
    public void logoutSuccess() throws Exception {
        LoginTO loginTO = new LoginTO("mod@igps.ru", "banme");
        mockMVC.perform(authenticatedRequest(loginTO, RequestMethod.GET, "/api/logout", null))
                .andExpect(status().is(200));
        assertTrue(keysStoreService.get(loginTO.getLogin()) == null);
    }

    @Test
    public void expired() throws Exception {
        MvcResult afterAuthResult = authenticate("mod@igps.ru", "banme").andReturn();
        String date = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String digestMessage = "GEThttp://localhost/api/logout" + date;
        String secret = afterAuthResult.getResponse().getHeader(X_SECRET.toString());
        Cookie cookie = afterAuthResult.getResponse().getCookie(JWT_APP_COOKIE.toString());
        String csrf = HmacSigner.getJwtClaim(cookie.getValue(), CSRF_CLAIM_HEADER.toString());
        String digest = HmacSigner.encodeMac(secret, digestMessage, HMAC_SHA_256.toString());
        keysStoreService.remove("mod@igps.ru");
        MvcResult result = mockMVC.perform(get("/api/logout").header(X_DIGEST.toString(), digest).secure(true)
                .header(X_ONCE.toString(), date)
                .header(CSRF_CLAIM_HEADER.toString(), csrf)
                .cookie(afterAuthResult.getResponse().getCookies())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andReturn();
        System.out.println(result.getResponse().getStatus());
    }

    @Test
    public void registerEmailInvalid() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(new RegisterTO("someone", "asd@@.asd.ro", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameAlreadyExists() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("Модератор", "asd@asd.ri", "12345", "12345"))))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidPattern() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeValue(new RegisterTO("123\r\n 1234  1235", "asd@asd.ri", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidLength() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("1", "asd@asd.ry", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUsernameInvalidBlank() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("", "asd@asd.ry", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerPasswordsNotEquals() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("fga", "asd@asd.ry", "123455", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void registerUnsafeHtml() throws Exception {
        mockMVC.perform(put("/api/register").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(new RegisterTO("<script>alert()</script", "asd@asd.ry", "12345", "12345"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}