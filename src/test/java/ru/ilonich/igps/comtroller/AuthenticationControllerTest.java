package ru.ilonich.igps.comtroller;


import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.model.User;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.ilonich.igps.utils.JsonUtil.*;

public class AuthenticationControllerTest extends AbstractControllerTest {

    @Test
    public void authenticateMod() throws Exception {
        MvcResult result = authenticate("mod@igps.ru", "banme");
        User user = readValue(result.getResponse().getContentAsString(), User.class);
        UserTestData.USER_MODEL_MATCHER.assertEquals(user, UserTestData.moderator);
    }

    @Test(expected = BadCredentialsException.class)
    public void badCredentials() throws Exception {
        authenticate("mod@igps.ru","wrongPassword");
    }

    @Test
    public void logoutSuccess() throws Exception {
        MvcResult result = authenticate("mod@igps.ru", "banme");
        mockMVC.perform(authenticatedRequest(result, RequestMethod.GET, "/api/logout", null))
                .andExpect(status().is(200));
    }
}