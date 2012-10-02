package com.picsauditing.security;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.util.Base64;
import com.picsauditing.util.Strings;

public class SessionSecurity {
	public static final String SESSION_COOKIE_NAME = "PICS_ORG_SESSION";
	public static final String SESSION_COOKIE_DOMAIN = "picsorganizer.com";

	private static final Logger logger = LoggerFactory.getLogger(SessionSecurity.class);
	private static String secretKeyspec;
	private static String suggestedSecretKeyspec;
	private static SecretKey serverSecretKey;

	public static SecretKey serverSecretKey() {
		if (serverSecretKey != null) {
			return serverSecretKey;
		}
		String secretKeyspec = serverKeySpec();
		if (secretKeyspec == null) {
			if (suggestedSecretKeyspec == null) {
				suggestedSecretKeyspec = EncodedKey.newServerSecretKey();
			}
			logger.error("server is missing a secret key specification. Here's a suggestion: {}",
					suggestedSecretKeyspec);
			return null;
		}
		byte[] keyspec = Base64.decode(secretKeyspec);
		serverSecretKey = new SecretKeySpec(keyspec, 0, keyspec.length, "HmacSHA1");
		return serverSecretKey;
	}

	private static String serverKeySpec() {
		if (secretKeyspec == null) {
			return System.getProperty("sk");
		}
		return secretKeyspec;
	}

	public static boolean cookieIsValid(SessionCookie picsSessionCooke) {
		return cookieIsValid(picsSessionCooke.toString());
	}

	public static boolean cookieIsValid(String picsSessionCooke) {
		if (Strings.isEmpty(picsSessionCooke)) {
			return false;
		}
		// split the meat from the validation hash
		int indexOfSplit = picsSessionCooke.lastIndexOf('|');
		String cookieData = picsSessionCooke.substring(0, indexOfSplit);
		String cookieValidHash = picsSessionCooke.substring(indexOfSplit + 1);

		// re-encode the meat with a known good server key
		String checkHash = EncodedMessage.hmacBase64(cookieData, serverSecretKey());

		// if either hash is null/empty then it is not valid
		// else if they match, the data or hash was not altered
		return (!(Strings.isEmpty(cookieValidHash) || Strings.isEmpty(checkHash))
		&& cookieValidHash.equals(checkHash));
	}

	public static void addValidationHashToSessionCookie(SessionCookie sessionCookie) {
		String hmac = EncodedMessage.hmacBase64(sessionCookie.toString(), serverSecretKey());
		sessionCookie.setValidationHash(hmac);
	}

	public static SessionCookie parseSessionCookie(String picsSessionCooke) {
		SessionCookie sessionCookie = new SessionCookie();
		if (Strings.isEmpty(picsSessionCooke)) {
			return sessionCookie;
		}
		try {
			String[] cookieParts = picsSessionCooke.split("\\|");
			Integer userID = Integer.parseInt(cookieParts[0]);
			Date cookieCreationTime = new Date(Long.parseLong(cookieParts[1]));
			sessionCookie.setUserID(userID);
			sessionCookie.setCookieCreationTime(cookieCreationTime);
			sessionCookie.setEmbeddedData(cookieParts[2]);
		} catch (Exception e) {
			logger.error("unable to create SessionCookie object from session cookie passed: {}", e.getMessage());
		}
		return sessionCookie;
	}
}
