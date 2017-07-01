package ru.ilonich.igps.service;

import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.config.security.HmacSecurityFilter;
import ru.ilonich.igps.config.security.misc.HmacToken;
import ru.ilonich.igps.model.tokens.KeyPair;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.AuthenticatedUser;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.tokens.VerificationToken;
import ru.ilonich.igps.repository.tokens.KeyPairRepository;
import ru.ilonich.igps.to.AuthTO;
import ru.ilonich.igps.to.LoginTO;
import ru.ilonich.igps.to.RegisterTO;
import ru.ilonich.igps.model.AnonymousUser;
import ru.ilonich.igps.utils.HmacSigner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.*;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private KeyPairRepository keyPairRepository;

    @Autowired(required = false)
    JavaMailSender javaMailService;

    @Autowired
    private LoadingCache<String, KeyPair> keyStore;


    @Override
    public AuthTO authenticate(LoginTO loginTO, HttpServletResponse response) throws HmacException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginTO.getLogin(),loginTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        //Get Hmac signed token
        String csrfId = UUID.randomUUID().toString();
        Map<String,String> encodingClaim = Collections.singletonMap(ENCODING_CLAIM_PROPERTY.toString(), HMAC_SHA_256.toString());
        Map<String,String> customClaims = new HashMap<>(encodingClaim);
        customClaims.put(JWT_CLAIM_LOGIN.toString(), user.getUsername());
        customClaims.put(CSRF_CLAIM_HEADER.toString(), csrfId);

        String privateSecret = HmacSigner.generateSecret();
        String publicSecret = HmacSigner.generateSecret();
        KeyPair keyPair = new KeyPair(user.getUsername(), publicSecret, privateSecret, OffsetDateTime.now().plusSeconds(HmacSecurityFilter.JWT_TTL));
        keyStore.put(user.getUsername(), keyPair);
        keyPairRepository.save(keyPair);

        HmacToken privateToken = HmacSigner.getSignedToken(privateSecret, user.getUsername(), HmacSecurityFilter.JWT_TTL, customClaims);
        HmacToken publicToken = HmacSigner.getSignedToken(publicSecret, user.getUsername(), HmacSecurityFilter.JWT_TTL, encodingClaim);

        Cookie jwtCookie = new Cookie(JWT_APP_COOKIE.toString(), privateToken.getJwt());
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(HmacSecurityFilter.JWT_TTL);
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        response.setHeader(X_SECRET.toString(), publicSecret);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HMAC_SHA_256.toString());
        response.setHeader(CSRF_CLAIM_HEADER.toString(), csrfId);
        response.setHeader(X_TOKEN_ACCESS.toString(), publicToken.getJwt());

        return AuthTO.fromUser(user.getUser());
    }

    @Override
    public User register(RegisterTO registerTO) throws HmacException {
        VerificationToken token = userService.registerAndCreateVerificationToken(registerTO.createUser());
        //javaMailService.send(constructMessage(token.getEmail(), token.getToken()));
        return token.getUser();
    }

    @Override
    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null){
            keyStore.invalidate(authenticatedUser.getUsername());
            keyPairRepository.deleteById(authenticatedUser.getUsername());
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
}
