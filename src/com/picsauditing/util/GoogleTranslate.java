package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GoogleTranslate {

	private static String apikey = "AIzaSyBuCaFEPZ4Uzi9Y5HK0nUJUirHaVXSLBrk";
	private static boolean useV1 = false;

	public void main(String[] args) {
		System.out.println("start");
		String es = translate("<a class=\"cats are fun\" href=\"ContractorDashboard.action?id=1\">Ancon Marine</a>",
				"en", "es");
		System.out.println(es);
		String en = translate(es, "es", "en");
		System.out.println(en);
	}

	public static String translate(String text, String from, String to) {
		try {
			if (useV1) {
				HttpURLConnection con = (HttpURLConnection) new URL(
						"https://ajax.googleapis.com/ajax/services/language/translate").openConnection();
				con.setRequestMethod("GET");

				StringBuffer toSend = new StringBuffer("v=1.0");
				toSend.append("&q=").append(text);
				toSend.append("&langpair=").append(from).append("|").append(to);

				con.setDoOutput(true);
				con.getOutputStream().write(toSend.toString().getBytes("UTF-8"));

				InputStreamReader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
				JSONObject response = (JSONObject) JSONValue.parse(reader);
				JSONObject responseData = (JSONObject) response.get("responseData");
				return StringEscapeUtils.unescapeXml(responseData.get("translatedText").toString());
			} else {
				HttpURLConnection con = (HttpsURLConnection) new URL("https://www.googleapis.com/language/translate/v2")
						.openConnection();
				con.setRequestMethod("GET");

				StringBuffer toSend = new StringBuffer();
				toSend.append("key=").append(apikey);
				toSend.append("&q=").append(text);
				toSend.append("&source=").append(from);
				toSend.append("&target=").append(to);
				
				con.setDoOutput(true);
				con.getOutputStream().write(toSend.toString().getBytes("UTF-8"));

				InputStreamReader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
				JSONObject response = (JSONObject) JSONValue.parse(reader);
				JSONObject responseData = (JSONObject) response.get("data");
				JSONArray translations = (JSONArray) responseData.get("translations");
				JSONObject translation = (JSONObject) translations.get(0);
				return StringEscapeUtils.unescapeXml(translation.get("translatedText").toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
