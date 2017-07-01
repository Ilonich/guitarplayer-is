package ru.ilonich.igps.model.tokens;

import org.hibernate.Hibernate;
import ru.ilonich.igps.exception.HmacException;
import ru.ilonich.igps.model.HasId;
import ru.ilonich.igps.model.User;
import ru.ilonich.igps.utils.HmacSigner;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_verifications")
@Access(AccessType.FIELD)
public class VerificationToken implements HasId {

    @Id
    @Column(name = "id")
    private Integer id;

    @OneToOne()
    @PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "expiries")
    private LocalDateTime expirationDate;

    public VerificationToken() {
    }

    public VerificationToken(User user) throws HmacException {
        this.id = user.getId();
        this.user = user;
        this.token = HmacSigner.generateSecret();
        this.expirationDate = LocalDateTime.now().plusHours(12);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }

        VerificationToken that = (VerificationToken) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId() : 0;
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id='" + id + '\'' +
                ", user=" + user.getUsername() +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
