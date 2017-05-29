package ru.ilonich.igps.config.security.misc;

import java.io.Serializable;

public final class KeyPair implements Serializable {
    private static final long serialVersionUID = -4071505;

    private String publicKey;
    private String privateKey;

    public KeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
