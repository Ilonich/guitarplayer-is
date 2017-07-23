package ru.ilonich.igps.service;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.config.MailConfig;
import ru.ilonich.igps.config.data.JpaConfig;

import javax.annotation.Resource;
import javax.mail.Message;

import java.security.Security;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {JpaConfig.class, MailConfig.class})
@RunWith(SpringRunner.class)
public class UserServiceImplEventsTest {

    @Autowired
    private UserService userService;

    @Resource
    private JavaMailSenderImpl javaMailService;

    private GreenMail greenMail;

    @BeforeClass
    public static void before(){
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
    }

    @Before
    public void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTPS);
        greenMail.start();

        javaMailService.setPort(3465);
        javaMailService.setHost("localhost");
        javaMailService.setUsername("test");
        javaMailService.setPassword("xxx");
    }

    @After
    public void stopMailServer() {
        greenMail.stop();
    }

    @Test
    public void register() throws Exception {
        userService.register(UserTestData.SOME_NEW, "url");
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
    }

    //TODO

}
