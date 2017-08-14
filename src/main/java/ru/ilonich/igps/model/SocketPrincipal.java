package ru.ilonich.igps.model;

import ru.ilonich.igps.model.enumerations.Role;

import java.security.Principal;
import java.util.*;

public class SocketPrincipal implements Principal {

    private String name;
    private boolean privileged;
    private static final Set<Role> REMOVAL_SET = new HashSet<>();
    static {
        REMOVAL_SET.add(Role.USER);
        REMOVAL_SET.add(Role.ANONYMOUS);
    }

    //anonymous
    public SocketPrincipal(String address) {
        this.name = address;
        this.privileged = false;
    }

    public SocketPrincipal(User user) {
        this.name = user.getId().toString();
        Set<Role> anotherThenRoleUser = new HashSet<Role>(user.getRoles());
        anotherThenRoleUser.removeAll(REMOVAL_SET);
        this.privileged = !anotherThenRoleUser.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 11 * result + (privileged ? 0 : 1);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        } else if (!(obj instanceof SocketPrincipal)) {
            return false;
        }
        SocketPrincipal another = (SocketPrincipal) obj;
        return another.isPrivileged() == this.privileged &&
                another.getName().equals(this.name);
    }

    @Override
    public String toString() {
        return "SocketPrincipal{" +
                "name='" + name + '\'' +
                ", privileged=" + privileged +
                '}';
    }
}
