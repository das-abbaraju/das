package com.picsauditing.integration.google;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.picsauditing.util.Base64;

public class UrlSigner {
	private static final Logger logger = LoggerFactory.getLogger(UrlSigner.class);
	private static byte[] key;

	static {
		String keyString = System.getProperty("gk");
		if (Strings.isNullOrEmpty(keyString)) {
			logger.error("You must set the google api signing key in the system property 'gk'");
		} else {
			// Convert the key from 'web safe' base 64 to binary
			keyString = keyString.replace('-', '+');
			keyString = keyString.replace('_', '/');
			key = Base64.decode(keyString);
		}
	}

	public static String signRequest(String urlString, String clientId) throws MalformedURLException {
		if (null == key) {
			logger.error("You must set the google api signing key in the system property 'gk'");
			return urlString;
		}
		URL url = new URL(urlString);
		String resource = url.getPath() + '?' + url.getQuery() + "&client=" + clientId;
		String signature;
		try {
			signature = computeSignature(resource);
		} catch (Exception e) {
			logger.error("cannot compute signature for google Url: {}", e.getMessage());
			return "";
		}
		return url.getProtocol() + "://" + url.getHost() + resource + "&signature=" + signature;
	}

	private static String computeSignature(String resource) throws NoSuchAlgorithmException,
			InvalidKeyException, UnsupportedEncodingException, URISyntaxException {
		SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(sha1Key);
		byte[] sigBytes = mac.doFinal(resource.getBytes());
		String signature = Base64.encodeBytes(sigBytes).toString();
		// convert the signature to 'web safe' base 64
		signature = signature.replace('+', '-');
		signature = signature.replace('/', '_');
		return signature;
	}
}
