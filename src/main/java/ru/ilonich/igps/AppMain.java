package ru.ilonich.igps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
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
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.ilonich.igps.config.AsyncConfig;
import ru.ilonich.igps.config.MailConfig;
import ru.ilonich.igps.config.TaskConfig;
import ru.ilonich.igps.config.data.JpaConfig;
import ru.ilonich.igps.config.security.SSConfig;
import ru.ilonich.igps.config.socket.WebSocketConfig;
import ru.ilonich.igps.config.web.MvcConfig;
import ru.ilonich.igps.config.web.WebConfig;

import java.util.Arrays;
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
        ProjectInfoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JtaAutoConfiguration.class,
        SpringDataWebAutoConfiguration.class, ServerPropertiesAutoConfiguration.class,
        ValidationAutoConfiguration.class, FreeMarkerAutoConfiguration.class,
        WebSocketMessagingAutoConfiguration.class
})
@Import({WebConfig.class, MvcConfig.class, JpaConfig.class,
        SSConfig.class, MailConfig.class, WebSocketConfig.class,
        AsyncConfig.class, TaskConfig.class})
public class AppMain extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(AppMain.class, args);
        //Stream.of(ac.getBeanDefinitionNames()).forEach(System.out::println);
    }
}
