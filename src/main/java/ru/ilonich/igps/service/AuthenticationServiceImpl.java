package ru.ilonich.igps.service;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.MapMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.config.security.HmacSecurityFilter;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.config.security.misc.KeyPair;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private LoadingCache<String, KeyPair> keyStore;


    @Override
    public User authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginTO.getLogin(),loginTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        //Get Hmac signed token
        String csrfId = UUID.randomUUID().toString();
        Map<String,String> encodingClaim = Collections.singletonMap(ENCODING_CLAIM_PROPERTY.toString(), HMAC_SHA_256.toString());
        Map<String,String> customClaims = new HashMap<>(encodingClaim);
        customClaims.put(JWT_CLAIM_LOGIN.toString(), loginTO.getLogin());
        customClaims.put(CSRF_CLAIM_HEADER.toString(), csrfId);

        //Store in cache map: login(email) = key, generated secret keys pair = value
        KeyPair keyPair = keyStore.getUnchecked(user.getUsername()); // LoadingCache invokes 'load' for new entry = (String login, new KeyPair(HmacSigner.generateSecret(), HmacSigner.generateSecret())
        String privateSecret = keyPair.getPrivateKey();
        String publicSecret = keyPair.getPublicKey();

        HmacToken privateToken = HmacSigner.getSignedToken(privateSecret, user.getUsername(), HmacSecurityFilter.JWT_TTL, customClaims);
        HmacToken publicToken = HmacSigner.getSignedToken(publicSecret, user.getUsername(), HmacSecurityFilter.JWT_TTL, encodingClaim);

        Cookie jwtCookie = new Cookie(JWT_APP_COOKIE.toString(), privateToken.getJwt());
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60*60*24);
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        response.setHeader(X_SECRET.toString(), publicSecret);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HMAC_SHA_256.toString());
        response.setHeader(CSRF_CLAIM_HEADER.toString(), csrfId);
        response.setHeader(X_TOKEN_ACCESS.toString(), publicToken.getJwt());

        return user.getUser();
    }

    @Override
    public void logout() {
        if(SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            AuthenticatedUser user = AuthenticatedUser.safeGet();
            if (user != null){
                keyStore.invalidate(user.getUsername());
            }
        }
    }

    @Override
    public void authenticateByToken(String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
