package ru.ilonich.igps.config.web;

import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static ru.ilonich.igps.config.security.misc.SecurityConstants.CSRF_CLAIM_HEADER;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.X_SECRET;
import static ru.ilonich.igps.config.security.misc.SecurityConstants.X_TOKEN_ACCESS;

public class AngularCliCorsFilter extends CorsFilter {

    private static final List<String> HEADERS_TO_EXPOSE = Arrays.asList(
            HttpHeaders.WWW_AUTHENTICATE,
            CSRF_CLAIM_HEADER.toString(),
            X_TOKEN_ACCESS.toString(),
            X_SECRET.toString()
    );

    public AngularCliCorsFilter() {
        super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setExposedHeaders(HEADERS_TO_EXPOSE);
        configuration.setMaxAge(172800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
