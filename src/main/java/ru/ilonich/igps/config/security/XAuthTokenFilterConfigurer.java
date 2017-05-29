package ru.ilonich.igps.config.security;

import com.google.common.cache.LoadingCache;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ilonich.igps.config.security.misc.KeyPair;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;

class XAuthTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AuthenticationService authenticationService;
    private LoadingCache<String, KeyPair> keyStore;
    private SecuredRequestCheckService checkService;

    XAuthTokenFilterConfigurer(AuthenticationService authenticationService, LoadingCache<String, KeyPair> keyStore, SecuredRequestCheckService checkService){
        this.authenticationService = authenticationService;
        this.keyStore = keyStore;
        this.checkService = checkService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        XAuthTokenFilter xAuthTokenFilter = new XAuthTokenFilter(authenticationService, keyStore, checkService);
        //Trigger this filter before SpringSecurity authentication validator
        builder.addFilterBefore(xAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}