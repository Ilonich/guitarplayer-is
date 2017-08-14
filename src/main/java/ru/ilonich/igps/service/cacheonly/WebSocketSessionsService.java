package ru.ilonich.igps.service.cacheonly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WebSocketSessionsService {

    private final static Logger LOG = LoggerFactory.getLogger(WebSocketSessionsService.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private final Map<String, List<String>> principalSessionsMap = new ConcurrentHashMap<>();

    public WebSocketSessionsService() {
        scheduler.scheduleAtFixedRate(() -> {
            sessionMap.keySet().forEach(k -> {
                try {
                    WebSocketSession session = sessionMap.get(k);
                    session.close();
                    sessionMap.remove(k);
                } catch (IOException e) { //IOException
                    LOG.error("Error while closing websocket session: {}", e);
                }
            });
            LOG.info("Current sessions count: {} \n Current sessions associated with principal count {} \n Current sessions with unique principal count {}",
                    sessionMap.size(),
                    principalSessionsMap.entrySet().stream().mapToInt((e) -> e.getValue().size()).sum(),
                    principalSessionsMap.size());
        }, 15, 15, TimeUnit.MINUTES);
    }

    public void registerSession(WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    public WebSocketSession getSessionById(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void associatePrincipalWithSession(Principal user, String sessionId) {

    }

    public void dissociatePrincipalWithSession(Principal user, String sessionId) {

    }

    public boolean isUserOnline(Integer userId){
        return principalSessionsMap.containsKey(String.valueOf(userId));
    }

}
