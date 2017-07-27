package ru.ilonich.igps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static Logger LOG = LoggerFactory.getLogger(AsyncConfig.class);

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, paramArray) -> {
            LOG.error("Exception in async method: {}\n" +
                    "!Exception message - {} \n " +
                    "!Method name - {} \n " +
                    "!Params - {}",
                    throwable.getClass().getSimpleName(), throwable.getMessage(), method.getName(),
                    Arrays.stream(paramArray).map( o -> toString()).collect(Collectors.joining("]\n[", "[", "]")));
        };
    }

}