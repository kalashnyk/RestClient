package ru.kao.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class RestService {

	public static final String SERVER = "https://api.github.com";
	public static final JSONParser PARSER = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

	public static int requestCount = 0;
	public static long lastRequestTime = System.currentTimeMillis();

	public static JSONObject getJSON(String request) throws ParseException, MalformedURLException, IOException,
			InterruptedException, net.minidev.json.parser.ParseException {
		String jsonString = getContent(request);
		return (JSONObject) PARSER.parse(jsonString);
	}

	public static String getContent(String request) throws MalformedURLException, IOException, InterruptedException {

		if (requestCount == 30) { 
			long currentTime = System.currentTimeMillis();
			long diff = currentTime - lastRequestTime;
			if (diff < 1000) {
				Thread.sleep(1000 - diff);
			}
			lastRequestTime = System.currentTimeMillis();
			requestCount = 0;
		}

		URL url = new URL(SERVER + request);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		httpConnection.setRequestProperty("Content-Type", "application/json");
		requestCount++;

		InputStream response = httpConnection.getInputStream();
		int responseCode = httpConnection.getResponseCode();

		if (responseCode != 200) {
			if (responseCode == 429 && httpConnection.getHeaderField("Retry-After") != null) {
				double sleepFloatingPoint = Double.valueOf(httpConnection.getHeaderField("Retry-After"));
				double sleepMillis = 1000 * sleepFloatingPoint;
				Thread.sleep((long) sleepMillis);
				return getContent(request);
			}
			throw new RuntimeException("Response code was not 200. Detected response was " + responseCode);
		}

		String output;
		Reader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[8192];
			int read;
			while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
				builder.append(buffer, 0, read);
			}
			output = builder.toString();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return output;
	}
}
