package ru.ilonich.igps.config.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;

class XAuthTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private AuthenticationService authenticationService;
    private SecuredRequestCheckService checkService;

    XAuthTokenFilterConfigurer(AuthenticationService authenticationService, SecuredRequestCheckService checkService){
        this.authenticationService = authenticationService;
        this.checkService = checkService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        XAuthTokenFilter xAuthTokenFilter = new XAuthTokenFilter(authenticationService, checkService);
        //Trigger this filter before SpringSecurity authentication validator
        builder.addFilterBefore(xAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}