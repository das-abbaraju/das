package com.picsauditing.security;

import com.picsauditing.util.Base64;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

public class SessionSecurity {
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
			logger.error("Server is missing a secret key specification. Here's a suggestion: {}",
					suggestedSecretKeyspec);
			System.out.println("Server is missing a secret key specification. Here's a suggestion: "
					+ suggestedSecretKeyspec);
			System.exit(-1);
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
		try {
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
		} catch (Exception e) {
			logger.error("There was a problem with checking the cookie. Removing bad cookie and returning false: {}",
					e.getMessage());
			return false;
		}
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
			Integer appUserID = Integer.parseInt(cookieParts[1]);
			Date cookieCreationTime = new Date(Long.parseLong(cookieParts[2]));
			sessionCookie.setUserID(userID);
			sessionCookie.setAppUserID(appUserID);
			sessionCookie.setCookieCreationTime(cookieCreationTime);
			sessionCookie.setEmbeddedData(cookieParts[3]);
		} catch (Exception e) {
			logger.error("unable to create SessionCookie object from session cookie passed: {}", e.getMessage());
		}
		return sessionCookie;
	}

	public static boolean switchToUserIsSet(HttpServletRequest request) {
		String sessionCookieValue = clientSessionCookieValue(request);
		if (sessionCookieValue != null && SessionSecurity.cookieIsValid(sessionCookieValue)) {
			SessionCookie sessionCookie = SessionSecurity.parseSessionCookie(sessionCookieValue);
			if (sessionCookie != null && sessionCookie.getData("switchTo") != null) {
				return true;
			}
		}
		return false;
	}

	public static String clientSessionCookieValue(HttpServletRequest request) {
		Cookie cookie = CookieSupport.cookieFromRequest(request, CookieSupport.SESSION_COOKIE_NAME);
		if (cookie != null) {
			try {
				return URLDecoder.decode(cookie.getValue(), "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// this won't happen unless somehow US-ASCII is removed
				logger.error("URLEncoder was given a bad encoding format: {}", e.getMessage());
			}
		}
		return null;
	}

}
