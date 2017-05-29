package ru.ilonich.igps.config.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.config.security.misc.WrappedRequest;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.junit.Assert.*;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

@RunWith(MockitoJUnitRunner.class)
public class HmacSecurityFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecuredRequestCheckService checkService;

    @Mock
    private ServletInputStream inputStream;

    @InjectMocks
    private HmacSecurityFilter hmacSecurityFilter;

    private HmacToken hmacToken;

    private String isoDate;

    private String url = "http://localhost/api/users";

    @Before
    public void setUp() throws HmacException, IOException {
        hmacSecurityFilter = new HmacSecurityFilter(checkService);
        String secret = HmacSigner.generateSecret();
        hmacToken = HmacSigner.getSignedToken(secret, "asd@asd.com", 20 , Collections.singletonMap(ENCODING_CLAIM_PROPERTY.toString(), HMAC_SHA_256.toString()));
        isoDate = OffsetDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
        Mockito.when(request.getInputStream()).thenReturn(inputStream);
    }

    @Test
    public void doFilterNoHmac() throws IOException, ServletException {
        Mockito.when(checkService.canVerify(request)).thenReturn(false);
        hmacSecurityFilter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(Mockito.any(WrappedRequest.class), Mockito.any(HttpServletResponse.class));
    }

}