package ru.ilonich.igps.exception;

public class ExpiredAuthenticationException extends Exception {
    private String issuer;

    public ExpiredAuthenticationException(String iss) {
        super("Authentication token expired for " + iss);
        this.issuer = iss;
    }

    public String getIssuer() {
        return issuer;
    }
}
