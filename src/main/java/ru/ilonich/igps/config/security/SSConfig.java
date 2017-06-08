package ru.ilonich.igps.config.security;

import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.config.security.misc.KeyPair;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.UserService;
import ru.ilonich.igps.utils.PasswordUtil;

@Configuration
@EnableWebSecurity
@Import(JpaConfig.class)
public class SSConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SecuredRequestCheckService securedRequestCheckService;

    @Autowired
    private LoadingCache<String, KeyPair> keyStore;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationConfigurer configurer = auth.userDetailsService(userService);
        configurer.passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("*.{js}")
                .antMatchers("*.{ico}")
                .antMatchers("*.{html}");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/authenticate").anonymous()
                .antMatchers("/api/public/**").anonymous()
                .antMatchers("/").anonymous()
                .antMatchers("/favicon.ico").anonymous()
                .antMatchers("/api/**").authenticated()
                .and()
                .csrf()
                .disable()
                .headers()
                .frameOptions().disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                .permitAll()
                .and()
                .apply(authTokenConfigurer())
                .and()
                .apply(hmacSecurityConfigurer());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordUtil.getPasswordEncoder();
    }

    private HmacSecurityFilterConfigurer hmacSecurityConfigurer(){
        return new HmacSecurityFilterConfigurer(securedRequestCheckService);
    }

    private XAuthTokenFilterConfigurer authTokenConfigurer(){
        return new XAuthTokenFilterConfigurer(authenticationService, keyStore, securedRequestCheckService);
    }

}
