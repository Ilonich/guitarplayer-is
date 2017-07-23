package ru.ilonich.igps.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class PasswordGeneratorUtilTest {
    @Test
    public void generate() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Callable<Boolean>> tasks = new ArrayList<>();
        Set<String> strings = new CopyOnWriteArraySet<>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(() -> {
                strings.add(PasswordGeneratorUtil.generate(8));
                return true;
            });
        }
        service.invokeAll(tasks);
        service.shutdown();
        service.awaitTermination(2, TimeUnit.SECONDS);
        assertEquals(1000, strings.size());
    }

}