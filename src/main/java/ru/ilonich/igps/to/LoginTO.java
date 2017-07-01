package ru.ilonich.igps.to;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class LoginTO implements TransferObject{

    @Email
    @NotBlank
    private String login;
    private String password;

    public LoginTO() {
    }

    public LoginTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    @JsonSetter
    public void setLogin(String login) {
        this.login = login.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
