package com.picsauditing.security;

import com.picsauditing.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Misc utilities for dealing with encoded keys of various sorts (passwords, API keys, etc.)
 * <p/>
 * TODO find all other PICS Organizer business logic that depends on Random or SecureRandom and move it to here.
 * <p/>
 * TODO Rename this class to something like EncodedKeyUtils
 */
public class EncodedKey {
    final static Logger logger = LoggerFactory.getLogger(EncodedKey.class);

    /**
     * This is a convenience method to generate a random API key (for use with the REST API). For now, we're using a
     * straight random number returned using a radix of 36 (10 digits and 26 letters) with a max character length of 32.
     * Later, we might want to switch to a GUID, or something else along those lines.
     */
    public static String randomApiKey() {
        KeyCode keyCode = new KeyCode(32);
        keyCode.setMaxTries(20);
        keyCode.generateRandomComplex();
        return keyCode.getKey();
    }

    /**
     * Generate a random user password.
     */
    public static String randomPassword() {
        // TODO Switch this to use BigInteger & SecureRandom to be consistent
        // with randomApiKey()
        return Long.toString(new Random().nextLong());
    }

    public static String newServerSecretKey() {
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance("HmacSHA1");
            SecretKey sk = kg.generateKey(); // 160 bits (430 bits?)
            return Base64.encodeBytes(sk.getEncoded()); // 1 character per 5 bits = 86 characters (+ 1 linefeed + 2
            // trailing equal signs = 89)
        } catch (NoSuchAlgorithmException e) {
            // This shouldn't really happen given that HmacSHA1 is built into
            // java
            logger.error(e.getMessage(),e);
        }
        return null;
    }


    public static void verifySufficientlyComplex(String key) throws SecurityException {
        new KeyCode(key).verifySufficientlyComplex();
    }

}
