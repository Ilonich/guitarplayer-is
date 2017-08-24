package ru.ilonich.igps.model.tokens;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "secret_keys_store")
public final class LoginSecretKeysPair implements Serializable {
    private static final long serialVersionUID = -4071505;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "public_secret")
    private String publicKey;

    @Column(name = "private_secret")
    private String privateKey;

    @Column(name = "expiries")
    private OffsetDateTime expirationDate;

    public LoginSecretKeysPair(){}

    public LoginSecretKeysPair(Integer userId, String publicKey, String privateKey, OffsetDateTime expirationDate) {
        this.userId = userId;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expirationDate = expirationDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public OffsetDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(OffsetDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !this.getClass().equals(Hibernate.getClass(obj))) {
            return false;
        }

        LoginSecretKeysPair that = (LoginSecretKeysPair) obj;

        return this.getUserId().equals(that.getUserId())
                && this.getPrivateKey().equals(that.getPrivateKey())
                && this.getPublicKey().equals(that.getPublicKey())
                && this.getExpirationDate().equals(that.getExpirationDate());
    }
}
