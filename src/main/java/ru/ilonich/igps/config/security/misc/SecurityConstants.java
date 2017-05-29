package ru.ilonich.igps.config.security.misc;

public enum SecurityConstants {
    JWT_APP_COOKIE("hmac-app-jwt"),
    CSRF_CLAIM_HEADER("X-HMAC-CSRF"),
    JWT_CLAIM_LOGIN("login"),
    HMAC_SHA_256("HmacSHA256"),
    HMAC_SHA_1("HmacSHA1"),
    HMAC_MD5("HmacMD5"),
    NONE("NONE"),
    X_TOKEN_ACCESS("X-TokenAccess"),
    X_SECRET("X-Secret"),
    AUTHENTICATION("Authentication"),
    X_DIGEST("X-Digest"),
    X_ONCE("X-Once"),
    X_ISS("X-ISS"),
    ENCODING_CLAIM_PROPERTY("l-lev");

    private String value;

    SecurityConstants(String value){
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
