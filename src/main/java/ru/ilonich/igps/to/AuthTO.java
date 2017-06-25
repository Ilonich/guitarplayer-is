package ru.ilonich.igps.to;

import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.enumerations.Role;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class AuthTO implements TransferObject {
    private String username;
    private Collection<Role> roles;

    public AuthTO() {
    }

    public AuthTO(String username, Set<Role> roles) {
        this.username = username;
        this.roles = Collections.unmodifiableCollection(roles);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = Collections.unmodifiableCollection(roles);
    }

    public static AuthTO fromUser(User user){
        return new AuthTO(user.getUsername(), user.getRoles());
    }
}
