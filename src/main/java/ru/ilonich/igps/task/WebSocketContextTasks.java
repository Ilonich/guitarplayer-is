package ru.ilonich.igps.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ilonich.igps.config.socket.WebSocketSessionsContextHolder;

@Component
public class WebSocketContextTasks {

    @Scheduled(fixedRate = 50000)
    public void closeUselessSessions() {
        WebSocketSessionsContextHolder.closeSessionsWithoutUsernameAssociation();
    }

    @Scheduled(fixedRate = 600000)
    public void showStats() {
        WebSocketSessionsContextHolder.logStats();
    }
}
