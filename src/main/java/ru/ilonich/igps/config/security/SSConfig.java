package ru.ilonich.igps.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import ru.ilonich.igps.service.AuthenticationService;
import ru.ilonich.igps.service.SecuredRequestCheckService;
import ru.ilonich.igps.service.UserService;
import ru.ilonich.igps.utils.MessageUtil;
import ru.ilonich.igps.utils.PasswordUtil;

@Configuration
@EnableWebSecurity
public class SSConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SecuredRequestCheckService securedRequestCheckService;

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
                .antMatchers("*.{html}")
                .antMatchers("*.{css}")
                .antMatchers("*.{ttf}")
                .antMatchers("*.{woff}")
                .antMatchers("*.{eot}")
                .antMatchers("*.{svg}")
                .antMatchers("*.{woff2}");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)).and()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/authenticate").anonymous()
                .antMatchers("/api/register").anonymous()
                .antMatchers("/api/reset").anonymous()
                .antMatchers("/api/validate").anonymous()
                .antMatchers("/api/confirm/**").anonymous()
                .antMatchers("/api/websocket").anonymous()
                .antMatchers("/api/websocket/**").anonymous()
                .antMatchers("/").anonymous()
                .antMatchers("/api/**").authenticated().and()
                .csrf().disable().headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .logout().permitAll().and()
                .apply(authTokenConfigurer()).and()
                .apply(hmacSecurityConfigurer()).and()
                .requiresChannel().anyRequest().requiresSecure();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordUtil.getPasswordEncoder();
    }

    private HmacSecurityFilterConfigurer hmacSecurityConfigurer(){
        return new HmacSecurityFilterConfigurer(securedRequestCheckService);
    }

    private XAuthTokenFilterConfigurer authTokenConfigurer(){
        return new XAuthTokenFilterConfigurer(authenticationService, securedRequestCheckService);
    }

}
