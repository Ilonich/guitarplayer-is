package ru.ilonich.igps.service;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ilonich.igps.UserTestData;
import ru.ilonich.igps.config.MailConfig;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.events.OnPasswordResetSuccesEvent;
import ru.ilonich.igps.events.OnRegistrationSuccessEvent;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.utils.PasswordUtil;

import javax.annotation.Resource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import java.security.Security;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = {JpaConfig.class, MailConfig.class})
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
public class UserServiceImplMailEventsTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    private GreenMail greenMail;

    @BeforeClass
    public static void before(){
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
    }

    @Before
    public void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTPS);
        greenMail.start();

        javaMailSender.setPort(3465);
        javaMailSender.setHost("localhost");
        javaMailSender.setUsername("test");
        javaMailSender.setPassword("xxx");
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

        Message message = messages[0];
        assertEquals("Подтверждение аккаунта", message.getSubject());
        assertEquals(UserTestData.SOME_NEW.getEmail(), message.getAllRecipients()[0].toString());

        MimeMultipart test = (MimeMultipart) message.getContent();
        assertTrue(test.getContentType().startsWith("multipart/mixed;"));
        assertEquals(1, test.getCount());

        MimeMultipart inside = (MimeMultipart) test.getBodyPart(0).getContent();
        assertTrue(inside.getContentType().startsWith("multipart/related;"));
        assertEquals(2, inside.getCount());

        BodyPart htmlPart = inside.getBodyPart(0);
        assertEquals("text/html;charset=UTF-8", htmlPart.getContentType());

        MimeBodyPart logoPart = (MimeBodyPart) inside.getBodyPart(1);
        assertEquals("image/jpeg", logoPart.getContentType());
        assertEquals("<igpsLogo>", logoPart.getContentID());

        String htmlString = (String) htmlPart.getContent();
        Document doc = Jsoup.parse(htmlString);
        assertTrue(doc.select("a").attr("href").startsWith("url"));
        assertTrue(doc.select("body").text().contains(UserTestData.SOME_NEW.getUsername()));
    }

    @Test
    public void initiateReset() throws Exception {
        userService.initiatePasswordReset(UserTestData.CONFIDANT.getEmail(), "urlz");
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);

        Message message = messages[0];
        assertEquals("Подтверждение смены пароля", message.getSubject());
        assertEquals(UserTestData.CONFIDANT.getEmail(), message.getAllRecipients()[0].toString());

        MimeMultipart test = (MimeMultipart) message.getContent();
        assertTrue(test.getContentType().startsWith("multipart/mixed;"));
        assertEquals(1, test.getCount());

        MimeMultipart inside = (MimeMultipart) test.getBodyPart(0).getContent();
        assertTrue(inside.getContentType().startsWith("multipart/related;"));
        assertEquals(2, inside.getCount());

        BodyPart htmlPart = inside.getBodyPart(0);
        assertEquals("text/html;charset=UTF-8", htmlPart.getContentType());

        MimeBodyPart logoPart = (MimeBodyPart) inside.getBodyPart(1);
        assertEquals("image/jpeg", logoPart.getContentType());
        assertEquals("<igpsLogo>", logoPart.getContentID());

        String htmlString = (String) htmlPart.getContent();
        Document doc = Jsoup.parse(htmlString);
        assertTrue(doc.select("a").attr("href").startsWith("urlz"));
        assertTrue(doc.select("body").text().contains(UserTestData.CONFIDANT.getUsername()));
    }

    @Test
    public void confirmReset() throws Exception {
        userService.confirmPasswordReset("ZjQ2YTc3NDYtODc5MC00Yjc0LWFiMjYtMzVlODYzN2ZhNTE1");
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);

        Message message = messages[0];
        assertEquals("Новый пароль", message.getSubject());
        assertEquals(UserTestData.TYPICAL_USER.getEmail(), message.getAllRecipients()[0].toString());

        MimeMultipart test = (MimeMultipart) message.getContent();
        assertTrue(test.getContentType().startsWith("multipart/mixed;"));
        assertEquals(1, test.getCount());

        MimeMultipart inside = (MimeMultipart) test.getBodyPart(0).getContent();
        assertTrue(inside.getContentType().startsWith("multipart/related;"));
        assertEquals(2, inside.getCount());

        BodyPart htmlPart = inside.getBodyPart(0);
        assertEquals("text/html;charset=UTF-8", htmlPart.getContentType());

        MimeBodyPart logoPart = (MimeBodyPart) inside.getBodyPart(1);
        assertEquals("image/jpeg", logoPart.getContentType());
        assertEquals("<igpsLogo>", logoPart.getContentID());

        String htmlString = (String) htmlPart.getContent();
        Document doc = Jsoup.parse(htmlString);
        User user = userService.getById(UserTestData.TYPICAL_USER.getId());
        String newPass = doc.select("p").text();
        assertTrue(PasswordUtil.isMatch(newPass, user.getPassword()));
    }

    @Test
    public void mailLocale() throws Exception {
        mailService.sendNewPasswordMessageFromEvent(new OnPasswordResetSuccesEvent(new Locale("en"), "12345", "test@tt.com"));
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);

        Message message = messages[0];
        assertEquals("New password", message.getSubject());

        MimeMultipart test = (MimeMultipart) message.getContent();
        MimeMultipart inside = (MimeMultipart) test.getBodyPart(0).getContent();
        BodyPart htmlPart = inside.getBodyPart(0);
        String htmlString = (String) htmlPart.getContent();
        Document doc = Jsoup.parse(htmlString);
        assertTrue(doc.text().startsWith("Your new password:"));
    }

}
