package ru.ilonich.igps.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.config.security.misc.SecurityConstants;
import ru.ilonich.igps.exception.HmacException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class HmacSignerTest extends AbstractPrintTotalResultsTest {
    @Test
    public void getSignedToken() throws Exception {
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), "yolo@cc.com", 20, Collections.singletonMap("claim", "something"));
        assertNotNull(hmacToken);
        assertNotNull(hmacToken.getJwt());
        assertNotNull(hmacToken.getSecret());
        assertNotNull(hmacToken.getJwtID());
    }

    @Test
    public void generateSecret() throws Exception {
        assertNotNull(HmacSigner.generateSecret());
    }

    @Test
    public void isJwtExpired() throws Exception {
        //ttl - time to love, set to 0;
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), "yolo@cc.com", 0, Collections.singletonMap("claim", "something"));
        assertTrue(HmacSigner.isJwtExpired(hmacToken.getJwt()));
    }

    @Test
    public void verifyJWT() throws Exception {
        String secret = HmacSigner.generateSecret();
        HmacToken hmacToken = HmacSigner.getSignedToken(secret, "yolo@cc.com", 20, null);
        assertTrue(HmacSigner.verifyJWT(hmacToken.getJwt(), secret));
    }

    @Test
    public void getJwtClaim() throws Exception {
        Map<String,String> claims = new HashMap<>();
        claims.put(SecurityConstants.ENCODING_CLAIM_PROPERTY.toString(), "claimValue");
        claims.put("otherProperty", "otherClaimValue");
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), "yolo@cc.com", 20, claims);
        assertNotNull(hmacToken);
        assertNotNull(hmacToken.getJwt());
        assertNotNull(hmacToken.getSecret());
        assertNotNull(hmacToken.getJwtID());

        String claimEncoding = HmacSigner.getJwtClaim(hmacToken.getJwt(), SecurityConstants.ENCODING_CLAIM_PROPERTY.toString());
        assertEquals(claimEncoding, claims.get(SecurityConstants.ENCODING_CLAIM_PROPERTY.toString()));

        String otherClaim = HmacSigner.getJwtClaim(hmacToken.getJwt(), "otherProperty");
        assertEquals(otherClaim, claims.get("otherProperty"));
    }

    @Test
    public void getJwtIss() throws Exception {
        String login = "yolo@cc.com";
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), login, 20, Collections.singletonMap("claim", "something"));
        assertNotNull(hmacToken);
        assertNotNull(hmacToken.getJwt());
        assertNotNull(hmacToken.getSecret());
        assertNotNull(hmacToken.getJwtID());

        String iss = HmacSigner.getJwtIss(hmacToken.getJwt());
        assertNotNull(iss);
        assertEquals(login, iss);
    }

    @Test
    public void encodeMac() throws HmacException {
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), "1", 20, Collections.singletonMap("claim", "something"));
        assertNotNull(hmacToken);

        String message = "cutomMessage";
        String encodedHmac = HmacSigner.encodeMac(hmacToken.getSecret(),message, SecurityConstants.HMAC_SHA_256.toString());
        assertNotNull(encodedHmac);
    }

    @Test(expected = HmacException.class)
    public void encodeMacWithWrongAlgorithm() throws HmacException{
        HmacToken hmacToken = HmacSigner.getSignedToken(HmacSigner.generateSecret(), "1", 20, Collections.singletonMap("claim", "something"));
        assertNotNull(hmacToken);

        String message = "customMessage";
        String encodedHmac = HmacSigner.encodeMac(hmacToken.getSecret(), message, "wrongAlgorithm");
        assertNotNull(encodedHmac);
    }

}