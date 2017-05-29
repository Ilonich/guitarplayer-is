package ru.ilonich.igps.config.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import ru.ilonich.igps.service.SecuredRequestCheckService;

class HmacSecurityFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private SecuredRequestCheckService requestCheckService;

    HmacSecurityFilterConfigurer(SecuredRequestCheckService  service){
        this.requestCheckService = service;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        HmacSecurityFilter hmacSecurityFilter = new HmacSecurityFilter(requestCheckService);
        //Trigger this filter before JWT authentication verifying
        builder.addFilterBefore(hmacSecurityFilter, XAuthTokenFilter.class);
    }
}
