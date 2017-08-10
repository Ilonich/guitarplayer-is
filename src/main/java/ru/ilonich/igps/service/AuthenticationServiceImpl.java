package ru.ilonich.igps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.LoginSecretKeysPair;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;
import ru.ilonich.igps.model.AnonymousUser;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;
import java.util.*;

import static ru.ilonich.igps.config.security.HmacSecurityFilter.CLAIM_WITH_CURRENT_ENCODING;
import static ru.ilonich.igps.config.security.HmacSecurityFilter.JWT_TTL;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

//TODO переделать для входа/выхода с аккаунта с разных мест/устройств
@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginSecretKeysPairStoreService keysStoreService;

    @Override
    public AuthTO authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException {
        AuthenticatedUser user = authenticateAndGetAuthenticationPrincipal(loginTO);

        String csrfId = UUID.randomUUID().toString();
        Map<String,String> customClaims = new HashMap<>(CLAIM_WITH_CURRENT_ENCODING);
        customClaims.put(CSRF_CLAIM_HEADER.toString(), csrfId);
        customClaims.put(JWT_CLAIM_LOGIN.toString(), user.getUsername());

        String privateSecret = HmacSigner.generateSecret();
        String publicSecret = HmacSigner.generateSecret();
        LoginSecretKeysPair loginSecretKeysPair = new LoginSecretKeysPair(user.getUsername(), publicSecret, privateSecret, OffsetDateTime.now().plusSeconds(JWT_TTL));
        keysStoreService.store(loginSecretKeysPair);

        HmacToken privateToken = HmacSigner.getSignedToken(privateSecret, user.getUsername(), JWT_TTL, customClaims);
        HmacToken publicToken = HmacSigner.getSignedToken(publicSecret, user.getUsername(), JWT_TTL, CLAIM_WITH_CURRENT_ENCODING);

        response.addCookie(buildPrivateJwtCookie(privateToken.getJwt()));
        response.setHeader(X_TOKEN_ACCESS.toString(), publicToken.getJwt());
        response.setHeader(CSRF_CLAIM_HEADER.toString(), csrfId);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HMAC_SHA_256.toString());
        response.setHeader(X_SECRET.toString(), publicSecret);

        return AuthTO.fromUser(user.getUser());
    }

    @Override
    public User register(RegisterTO registerTO, String confirmUrl) throws HmacException {
        return userService.register(registerTO.createUser(), confirmUrl);
    }

    @Override
    public boolean initiateReset(String email, String url) throws HmacException {
        return userService.initiatePasswordReset(email, url);
    }

    @Override
    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null){
            keysStoreService.remove(authenticatedUser.getUsername());
        }
    }

    @Override
    public void authenticateByToken(String email) {
        UserDetails userDetails = userService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Override
    public void authenticateAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(AnonymousUser.ANONYMOUS_TOKEN);
    }

    private AuthenticatedUser authenticateAndGetAuthenticationPrincipal(LoginTO loginTO){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginTO.getLogin(),loginTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (AuthenticatedUser) authentication.getPrincipal();
    }

    private Cookie buildPrivateJwtCookie(String jwt){
        Cookie jwtCookie = new Cookie(JWT_APP_COOKIE.toString(), jwt);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(JWT_TTL);
        jwtCookie.setHttpOnly(true);
        return jwtCookie;
    }
}
