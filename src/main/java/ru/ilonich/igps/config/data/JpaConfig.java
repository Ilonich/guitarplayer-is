package ru.ilonich.igps.config.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import ru.ilonich.igps.config.security.misc.KeyPair;
import ru.ilonich.igps.utils.HmacSigner;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"ru.ilonich.igps.repository.user"})
@EnableCaching
@ComponentScan({"ru.ilonich.igps.repository", "ru.ilonich.igps.service"})
public class JpaConfig implements TransactionManagementConfigurer {

    @Value("${dataSource.url}")
    private String url;
    @Value("${dataSource.username}")
    private String username;
    @Value("${dataSource.password}")
    private String password;
    @Value("${dataSource.initializeDb}")
    private String initDb;
    @Value("${resources.init.sql}")
    private String initSql;
    @Value("${resources.populate.sql}")
    private String populateSql;
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.show-sql}")
    private String showSql;
    @Value("${hibernate.format-sql}")
    private String formatSQL;
    @Value("${hibernate.sql-comments}")
    private String sqlComments;
    @Value("${hibernate.cache-class}")
    private String cacheClass;
    @Value("${hibernate.cache-resource}")
    private String cacheResource;
    @Value("${hibernate.second-lvl}")
    private String secondLvl;
    @Value("${hibernate.query-cache}")
    private String queryCache;

    @Bean
    public DataSource dataSource(){
        DataSource dataSource = configureDataSource();
        if (Boolean.valueOf(initDb)) {
            DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);
        }
        return dataSource;
    }

    private DataSource configureDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTestQuery("SELECT 1 FROM users LIMIT 1");
        config.setConnectionInitSql("SELECT 1 FROM users LIMIT 1");
        //Метод org.postgresql.jdbc.PgConnection.get/setNetworkTimeout() ещё не реализован)
        config.setConnectionTimeout(20000);
        config.setValidationTimeout(20000);
        config.setIdleTimeout(300000);
        config.setMaximumPoolSize(12);

        return new HikariDataSource(config);
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean configureEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("ru.ilonich.igps.model");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
        jpaProperties.put(org.hibernate.cfg.Environment.SHOW_SQL, showSql);
        jpaProperties.put(org.hibernate.cfg.Environment.FORMAT_SQL, formatSQL);
        jpaProperties.put(org.hibernate.cfg.Environment.USE_SQL_COMMENTS, sqlComments);
        jpaProperties.put(org.hibernate.cfg.Environment.CACHE_REGION_FACTORY, cacheClass);
        jpaProperties.put(EhCacheRegionFactory.NET_SF_EHCACHE_CONFIGURATION_RESOURCE_NAME, cacheResource);
        jpaProperties.put(org.hibernate.cfg.Environment.USE_SECOND_LEVEL_CACHE, secondLvl);
        jpaProperties.put(org.hibernate.cfg.Environment.USE_QUERY_CACHE, queryCache);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Override
    @Bean(name = "transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new JpaTransactionManager();
    }

    private DatabasePopulator createDatabasePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setContinueOnError(true);
        databasePopulator.addScript(new ClassPathResource("db/init.sql"));
        databasePopulator.addScript(new ClassPathResource("db/populate.sql"));
        return databasePopulator;
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehcache().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean ehcache(){
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("cache/ehcache.xml"));
        factoryBean.setShared(true);
        return factoryBean;
    }

    @Bean
    public LoadingCache<String, KeyPair> keyStore(){
        return CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .initialCapacity(10)
                .maximumSize(Long.MAX_VALUE)
                .build(new CacheLoader<String, KeyPair>() {
                    @Override
                    public KeyPair load(String login) throws Exception {
                        return new KeyPair(HmacSigner.generateSecret(), HmacSigner.generateSecret()); //TODO from DB
                    }
                });
    }
}
