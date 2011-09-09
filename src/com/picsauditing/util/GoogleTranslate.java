package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharsetDecoder;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GoogleTranslate {
	public static void main(String[] args) {
		System.out.println("start");
		String es = translate("<a class=\"cats are fun\" href=\"ContractorDashboard.action?id=1\">Ancon Marine</a>",
				"en", "es");
		System.out.println(es);
		String en = translate(es, "es", "en");
		System.out.println(en);
	}

	public static String translate(String text, String from, String to) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(
					"https://ajax.googleapis.com/ajax/services/language/translate").openConnection();
			con.setRequestMethod("GET");

			StringBuffer toSend = new StringBuffer("v=1.0");
			toSend.append("&q=").append(text);
			toSend.append("&langpair=").append(from).append("|").append(to);

			con.setDoOutput(true);
			con.getOutputStream().write(toSend.toString().getBytes("UTF-8"));

			InputStreamReader reader = new InputStreamReader(con.getInputStream());
			JSONObject response = (JSONObject) JSONValue.parse(reader);
			JSONObject responseData = (JSONObject) response.get("responseData");
			return responseData.get("translatedText").toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
