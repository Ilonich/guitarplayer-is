package ru.ilonich.igps.comtroller;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConfirmationControllerTest extends AbstractControllerTest {

    @Test
    public void confirmEmail() throws Exception {
        mockMVC.perform(get("/api/confirm/email/YzZhZDk5YjAtYWM1ZC00YjgxLWEyNTAtYWY3MmFiNmFmZGIz").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(202))
                .andDo(print());
    }

    @Test
    public void confirmEmailFail() throws Exception {
        mockMVC.perform(get("/api/confirm/email/1").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(410))
                .andDo(print());
    }

    @Test
    public void confirmPasswordReset() throws Exception {
        mockMVC.perform(get("/api/confirm/reset/ZjQ2YTc3NDYtODc5MC00Yjc0LWFiMjYtMzVlODYzN2ZhNTE1").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(202))
                .andDo(print());
    }

    @Test
    public void confirmPasswordResetFail() throws Exception {
        mockMVC.perform(get("/api/confirm/reset/0").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(410))
                .andDo(print());
    }
}