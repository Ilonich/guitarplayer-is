package ru.ilonich.igps.config.web;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import ru.ilonich.igps.config.data.misc.JsonMapper;
import ru.ilonich.igps.config.security.SSConfig;
import ru.ilonich.igps.utils.MessageUtil;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan("ru.ilonich.igps.comtroller")
@Import(SSConfig.class)
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(JsonMapper.getMapper()));
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
        converters.add(stringHttpMessageConverter);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(25000);
        configurer.setTaskExecutor(taskExecutor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.js").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.css").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.html").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.ico").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.ttf").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.woff").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.woff2").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.svg").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.eot").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/*.map").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
        //TODO переделать в webpack расположение и сделать нормальный хэндлер
    }

    @Override
    public Validator getValidator() {
        return validator();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public LocalValidatorFactoryBean validator(){
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(25);
        executor.setKeepAliveSeconds(20);
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/backend-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver(){
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(MessageUtil.RU_LOCALE);
        resolver.setCookieName("locale");
        resolver.setCookieMaxAge(-1);
        return resolver;
    }

    @Bean
    public MessageUtil messageUtil(){
        return new MessageUtil(messageSource());
    }
}
