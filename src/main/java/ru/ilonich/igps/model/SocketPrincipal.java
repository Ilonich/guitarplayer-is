package ru.ilonich.igps.model;

import ru.ilonich.igps.model.enumerations.Role;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class SocketPrincipal implements Principal {

    private String idAsPrincipalName;
    private String email;
    private String csrfAsPwd;
    private boolean privileged;

    public SocketPrincipal(User user, String csrfCheck) {
        this.idAsPrincipalName = user.getId().toString();
        this.email = user.getEmail();
        this.csrfAsPwd = csrfCheck;
        Set<Role> anotherThenRoleUser = new HashSet<Role>(user.getRoles());
        anotherThenRoleUser.remove(Role.USER);
        this.privileged = !anotherThenRoleUser.isEmpty();
    }

    @Override
    public String getName() {
        return idAsPrincipalName;
    }

    public String getEmail() {
        return email;
    }

    public String getCsrfAsPwd() {
        return csrfAsPwd;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    @Override
    public int hashCode() {
        int result = idAsPrincipalName.hashCode();
        result = 17 * result + email.hashCode();
        result = 23 * result + csrfAsPwd.hashCode();
        result = 31 * (privileged ? 0 : 1);
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
                another.getCsrfAsPwd().equals(this.csrfAsPwd) &&
                another.getName().equals(this.idAsPrincipalName) &&
                another.getEmail().equals(this.email);
    }

    @Override
    public String toString() {
        return "SocketPrincipal{" +
                "idAsPrincipalName='" + idAsPrincipalName + '\'' +
                ", email='" + email + '\'' +
                ", csrfAsPwd='" + csrfAsPwd + '\'' +
                ", privileged=" + privileged +
                '}';
    }
}
