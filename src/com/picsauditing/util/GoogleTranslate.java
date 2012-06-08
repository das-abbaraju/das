package com.picsauditing.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleTranslate {

	private static String apikey = "AIzaSyBuCaFEPZ4Uzi9Y5HK0nUJUirHaVXSLBrk";
	private static boolean useV1 = false;
	private static final Logger logger = LoggerFactory.getLogger(GoogleTranslate.apikey);
	public static void main(String[] args) {
		logger.info("start");
		String es = translate("This is a test of translation.", "en", "es");
		logger.info(es);
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

				HttpClient client = new HttpClient();
				String url = String.format(
						"https://www.googleapis.com/language/translate/v2?key=%s&q=%s&source=%s&target=%s", apikey,
						URLEncoder.encode(text, "UTF-8"), from, to);
				HttpMethod method = new GetMethod(url);
				int responseCode = client.executeMethod(method);
				if (responseCode != 200) {
					return null;
				}

				InputStreamReader reader = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
				JSONObject response = (JSONObject) JSONValue.parse(reader);
				JSONObject data = (JSONObject) response.get("data");
				JSONArray translations = (JSONArray) data.get("translations");
				JSONObject translation = (JSONObject) translations.get(0);
				return translation.get("translatedText").toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
