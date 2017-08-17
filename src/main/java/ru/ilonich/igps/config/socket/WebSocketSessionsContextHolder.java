package ru.ilonich.igps.config.socket;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class WebSocketSessionsContextHolder {
    private WebSocketSessionsContextHolder() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketSessionsContextHolder.class);

    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, String> sessionUsernameMap = new ConcurrentHashMap<>();

    //for O(1) contains (is registered user online)
    private static final Set<Integer> containsIdSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void logStats() {
        LOG.info("WebSocket connection stats: \n Current sessions count [{}]" +
                        " \n Current sessions associated with username count: [{}]" +
                        " \n Current unique username count: [{}]" +
                        " \n Current registered users online count: [{}]",
                sessionMap.size(),
                sessionUsernameMap.size(),
                sessionUsernameMap.values().stream().distinct().count(),
                containsIdSet.size());
    }

    public static boolean isRegisteredUserHasAnySession(Integer userId) {
        return containsIdSet.contains(userId);
    }

    public static Set<Integer> getUserIdsThatHasAssociationWithAnySession() {
        return Collections.unmodifiableSet(containsIdSet);
    }

    public static void closeSessionsWithoutUsernameAssociation() {
        Sets.SetView<String> difference = com.google.common.collect.Sets.difference(sessionMap.keySet(), sessionUsernameMap.keySet());
        Set<String> dif = difference.immutableCopy();
        dif.forEach(closeAndRemoveFromContext);
        if (dif.size() > 0) {
            LOG.info("Closed {} sessions without association", dif.size());
        }
    }

    static void addUserId(Integer userId) {
        if (containsIdSet.add(userId)) {
            LOG.trace("User id[{}] added to websocket context", userId);
        } else {
            LOG.trace("User id[{}] added to websocket context again", userId);
        }
    }

    static void removeUserId(Integer userId) {
        if (containsIdSet.remove(userId)) {
            LOG.trace("User id[{}] removed from websocket context", userId);
        } else {
            LOG.trace("User id[{}] was not contained in websocket context", userId);
        }
    }

    static void registerSession(WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    static void deregisterSession(String sessionId) {
        WebSocketSession session = sessionMap.remove(sessionId);
        if (session.isOpen()) {
            LOG.warn("Session [{}] was not closed, trying to close...", sessionId);
            try {
                session.close();
                LOG.warn("Session [{}] closed manually", sessionId);
            } catch (IOException e) {
                LOG.error("Error while closing websocket session: {}", e);
            }
        }
    }

    static WebSocketSession getSessionById(String sessionId) {
        return sessionMap.get(sessionId);
    }

    static boolean isSessionClosed(String sessionId) {
        return !sessionMap.get(sessionId).isOpen();
    }

    static void associateSessionWithUsername(String sessionId, String username) {
        String result = sessionUsernameMap.put(sessionId, username);
        if (result != null){
            LOG.trace("Session [{}] was already associated with the username[{}], new one [{}]", sessionId, result, username);
        }
    }

    static String removeSessionUsernameAssociation(String sessionId) {
        String username = sessionUsernameMap.remove(sessionId);
        if (username == null) {
            LOG.trace("Session [{}] was not associated with the user, unnecessary remove call", sessionId);
        }
        return username;
    }

    static boolean isUsernameAssociatedWithAnySession(String username) { //TODO avoid full traversal (ну, если юзеров на сайте будет больше 3.5)
        return sessionUsernameMap.containsValue(username);
    }

    static String getUsernameAssociatedWithSession(String sessionId) {
        return sessionUsernameMap.get(sessionId);
    }

    static void closeSessions() {
        sessionMap.keySet().forEach(closeAndRemoveFromContext);
    }

    private static final Consumer<String> closeAndRemoveFromContext = s -> {
        try {
            WebSocketSession session = sessionMap.get(s);
            session.close(CloseStatus.SERVICE_RESTARTED);
            sessionMap.remove(s);
            sessionUsernameMap.remove(s);
        } catch (IOException e) {
            LOG.error("Error while closing websocket session: {}", e);
        }
    };

}
