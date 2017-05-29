package ru.ilonich.igps.config.web;

import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@Import(MvcConfig.class)
public class WebConfig {

    @Bean
    public DispatcherServlet dispatcherServlet(){
        return new DispatcherServlet();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        ServletRegistrationBean sb = new ServletRegistrationBean(dispatcherServlet(), "/"); //   /api/
        sb.setLoadOnStartup(1);
        sb.setAsyncSupported(true);
        sb.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
        return sb;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(encodingFilter());
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("encodingFilter");
        return filterRegistrationBean;
    }

    @Bean
    public CharacterEncodingFilter encodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true, true);
    }
}
