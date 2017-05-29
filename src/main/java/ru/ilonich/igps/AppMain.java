package ru.ilonich.igps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.ilonich.igps.config.web.WebConfig;

import java.util.stream.Stream;

@Configuration
@EnableAutoConfiguration(exclude = {
        JacksonAutoConfiguration.class, DataSourceAutoConfiguration.class,
        TransactionAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class,
        JmxAutoConfiguration.class, PersistenceExceptionTranslationAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class, SecurityAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, JdbcTemplateAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class, HttpEncodingAutoConfiguration.class,
        MultipartAutoConfiguration.class, WebClientAutoConfiguration.class,
        WebSocketAutoConfiguration.class, ConfigurationPropertiesAutoConfiguration.class,
        ProjectInfoAutoConfiguration.class, ServerPropertiesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class, JtaAutoConfiguration.class,
        SpringDataWebAutoConfiguration.class
})
@Import({WebConfig.class})
public class AppMain extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(AppMain.class);

    public static void main(String[] args) {
        ApplicationContext ac = configureApplication(new SpringApplicationBuilder()).run(args);
        Stream.of(ac.getBeanDefinitionNames()).forEach(System.out::println);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(AppMain.class).bannerMode(Banner.Mode.OFF);
    }
}
