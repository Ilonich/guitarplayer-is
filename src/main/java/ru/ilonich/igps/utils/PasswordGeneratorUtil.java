package ru.ilonich.igps.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordGeneratorUtil {

    private final static SecureRandom RANDOM;
    private final static char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    static {
        try {
            RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SecureRandom: " + e.getMessage());
        }
    }

    public static String generate(int length){
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = CHARS[RANDOM.nextInt(CHARS.length)];
        }
        return new String(password);
    }

    public static String generate(){
        return generate(8);
    }


}
