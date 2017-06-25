package ru.ilonich.igps.to;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.model.enumerations.Authority;
import ru.ilonich.igps.model.enumerations.Location;
import ru.ilonich.igps.model.enumerations.Role;
import ru.ilonich.igps.utils.PasswordUtil;
import ru.ilonich.igps.utils.custom.PasswordsEqualConstraint;

import javax.validation.constraints.Pattern;

@PasswordsEqualConstraint
public class RegisterTO implements TransferObject {

    @NotBlank
    @SafeHtml
    @Length(min = 2, max = 24)
    @Pattern(regexp = "^((?![\\t]|[\\v]|[\\r]|[\\n]|[\\f]|  )[\\s\\S])*$")
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 5, max = 24)
    @Pattern(regexp = "^((?![\\t]|[\\v]|[\\r]|[\\n]|[\\f]| )[\\s\\S])*$")
    private String password;
    @NotBlank
    @Length(min = 5, max = 24)
    @Pattern(regexp = "^((?![\\t]|[\\v]|[\\r]|[\\n]|[\\f]| )[\\s\\S])*$")
    private String passwordcopy;

    public RegisterTO() {
    }

    public RegisterTO(String username, String email, String password, String passwordcopy) {
        this.username = username.trim();
        this.email = email;
        this.password = password;
        this.passwordcopy = passwordcopy;
    }

    public String getUsername() {
        return username;
    }

    @JsonSetter("username")
    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordcopy() {
        return passwordcopy;
    }

    public void setPasswordcopy(String passwordcopy) {
        this.passwordcopy = passwordcopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterTO that = (RegisterTO) o;

        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return getPasswordcopy() != null ? getPasswordcopy().equals(that.getPasswordcopy()) : that.getPasswordcopy() == null;

    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getPasswordcopy() != null ? getPasswordcopy().hashCode() : 0);
        return result;
    }

    public LoginTO asLoginTO() {
        return new LoginTO(getEmail(), getPassword());
    }

    public User createUser() {
        return new User(null, getEmail(), PasswordUtil.encode(getPassword()), false, false, getUsername(), "", 0, 0, Authority.REGULAR, Location.UNKNOWN, Role.USER);
    }
}
