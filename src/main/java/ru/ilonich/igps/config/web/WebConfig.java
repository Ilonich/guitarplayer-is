package ru.ilonich.igps.config.web;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.IOException;

@Configuration
@Import(MvcConfig.class)
public class WebConfig {
    private static final String KEYSTORE = "ilonich_igps_ru.jks";

    //https://tomcat.apache.org/tomcat-8.5-doc/config/http.html
    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatEmbeddedServletContainerFactory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
        tomcatEmbeddedServletContainerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            Http11Nio2Protocol protocol = (Http11Nio2Protocol) connector.getProtocolHandler();
            protocol.setMaxKeepAliveRequests(-1);
            protocol.setAcceptorThreadCount(2);
            protocol.setMaxHeaderCount(100);
            protocol.setSSLEnabled(true);
            protocol.setMaxThreads(300);
            protocol.setConnectionTimeout(20000);
            protocol.setMaxHttpHeaderSize(32768);
            protocol.setCompression("on");
            protocol.setCompressibleMimeType("application/json,application/xml,text/html,text/xml,text/plain,text/css,text/javascript,application/javascript");
            try {
                File keystore = new ClassPathResource(KEYSTORE).getFile();
                protocol.setKeystoreFile(keystore.getAbsolutePath());
                protocol.setKeystorePass("test123");
                protocol.setKeyAlias("server");
                //File truststore = new ClassPathResource("").getFile();
                //protocol.setTruststoreFile(truststore.getAbsolutePath());
                //protocol.setTruststorePass("");
            } catch (IOException e) {
                throw new IllegalStateException("can't access keystore: [" + KEYSTORE + "] ", e);
            }
            connector.setPort(8443);
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setParseBodyMethods("POST,PUT");
            //connector.addUpgradeProtocol(new Http2Protocol()); // TODO https://stackoverflow.com/questions/38612704/enable-http2-with-tomcat-in-spring-boot/43207609
            connector.setProperty("socket.appReadBufSize", "87380");
            connector.setProperty("socket.rxBufSize", "87380");
            connector.setProperty("socket.performanceLatency", "0");
            connector.setProperty("socket.performanceBandwidth", "1");
            connector.setProperty("socket.performanceConnectionTime", "2");
            connector.setProperty("acceptCount", "500");
            connector.setProperty("server", "IGPS-Server");
        });
        tomcatEmbeddedServletContainerFactory.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcatEmbeddedServletContainerFactory;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }


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
