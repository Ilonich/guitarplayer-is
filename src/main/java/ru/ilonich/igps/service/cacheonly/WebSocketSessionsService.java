package ru.ilonich.igps.service.cacheonly;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ilonich.igps.model.SocketPrincipal;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.service.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class WebSocketSessionsService {

    @Autowired
    private UserService userService;

    private Cache<String, SocketPrincipal> socketSessionsPreparedPrincipalsCache;

    private Set<String> usersOnline = Collections.synchronizedSet(new HashSet<>());

    public WebSocketSessionsService() {
        this.socketSessionsPreparedPrincipalsCache = CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(100)
                .build();
    }

    public void storeForAuthentication(String sessionId, String login, String pwd) {
        User user = userService.getByEmail(login);
        if (user != null) {
            socketSessionsPreparedPrincipalsCache.put(sessionId, new SocketPrincipal(user, pwd));
        }
    }

    public SocketPrincipal getPrincipalCandidate(String sessionId) {
        return socketSessionsPreparedPrincipalsCache.getIfPresent(sessionId);
    }

    public boolean addOnlineUserId(String id) {
        return usersOnline.add(id);
    }

    public boolean removeOnlineUserId(String id) {
        return usersOnline.remove(id);
    }

    public boolean isUserOnline(Integer userId) {
        return usersOnline.contains(userId.toString());
    }
}
