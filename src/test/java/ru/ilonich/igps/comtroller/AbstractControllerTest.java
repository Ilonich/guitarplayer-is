package ru.ilonich.igps.comtroller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.config.security.SSConfig;
import ru.ilonich.igps.config.web.MvcConfig;
import ru.ilonich.igps.service.UserService;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.utils.HmacSigner;
import ru.ilonich.igps.utils.JpaUtil;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;
import static ru.ilonich.igps.utils.JsonUtil.writeValue;

@WebAppConfiguration
@SpringBootTest(classes = {MvcConfig.class})
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public abstract class AbstractControllerTest {
    protected MockMvc mockMVC;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    protected UserService userService;

    @Autowired
    private JpaUtil jpaUtil;

    private static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter();

    static {
        CHARACTER_ENCODING_FILTER.setEncoding("UTF-8");
        CHARACTER_ENCODING_FILTER.setForceEncoding(true);
    }

    @PostConstruct
    public void setup() {
        this.mockMVC = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilters(CHARACTER_ENCODING_FILTER, springSecurityFilterChain)
                .build();
    }

    /**
     * Authenticate a user with its credentials.
     *
     * @param login    username
     * @param password password
     * @throws Exception BadCredentialsException
     */
    protected ResultActions authenticate(String login, String password) throws Exception {
        LoginTO loginTO = new LoginTO();
        loginTO.setLogin(login);
        loginTO.setPassword(password);
        ResultActions result = mockMVC.perform(post("/api/authenticate").secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(loginTO)));
        return result;
    }

    /**
     * Send request as logged user.
     *
     * @param loginTO            to authenticate
     * @param requestMethod      request method type
     * @param url                api reference
     * @param json               transfer object
     * @throws Exception         HmacException
     * @return MockHttpServletRequestBuilder
     */
    protected MockHttpServletRequestBuilder authenticatedRequest(LoginTO loginTO, RequestMethod requestMethod, String url, String json) throws Exception{
        MvcResult afterAuthResult = authenticate(loginTO.getLogin(), loginTO.getPassword()).andReturn();
        String body = json == null ? "" : json;
        String secret = afterAuthResult.getResponse().getHeader(X_SECRET.toString());
        String date = OffsetDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String message = String.format("%1$shttp://localhost%2$s%3$s%4$s", requestMethod.name(), body, url, date);
        Cookie cookie = afterAuthResult.getResponse().getCookie(JWT_APP_COOKIE.toString());
        String csrf = HmacSigner.getJwtClaim(cookie.getValue(), CSRF_CLAIM_HEADER.toString());
        String digest = HmacSigner.encodeMac(secret, message, HMAC_SHA_256.toString());

        MockHttpServletRequestBuilder builder;

        switch(requestMethod) {
            case GET:
                builder = get(url);
                break;
            case POST:
                builder = post(url);
                break;
            case PUT:
                builder = put(url);
                break;
            case PATCH:
                builder = patch(url);
                break;
            case OPTIONS:
                builder = options(url);
                break;
            case DELETE:
                builder = delete(url);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return builder.secure(true).header(X_DIGEST.toString(), digest)
                .header(X_ONCE.toString(), date)
                .header(CSRF_CLAIM_HEADER.toString(), csrf)
                .cookie(afterAuthResult.getResponse().getCookies())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }


    @Before
    public void setUp() {
        //userService.evictCache();
        jpaUtil.clear2ndLevelHibernateCache();
    }
}
