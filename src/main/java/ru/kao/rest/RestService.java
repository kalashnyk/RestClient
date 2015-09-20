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
import java.util.Date;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class RestService {

	public static final String SERVER = "https://api.github.com";
	public static final JSONParser PARSER = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

	public static int requestCount = 0;
	public static long lastRequestTime = System.currentTimeMillis();

	public static JSONObject getJSON(String request) throws RESTException, ParseException, MalformedURLException, IOException,
			InterruptedException, net.minidev.json.parser.ParseException {
		String jsonString = getContent(request);
		return (JSONObject) PARSER.parse(jsonString);
	}

	public static String getContent(String request) throws RESTException, MalformedURLException, IOException, InterruptedException {
		if (requestCount == 10) {
			long currentTime = System.currentTimeMillis();
			long diff = currentTime - lastRequestTime;
			if (diff < 60000) {
				Thread.sleep(60000 - diff);
			}
			lastRequestTime = System.currentTimeMillis();
			requestCount = 0;
		}

		URL url = new URL(SERVER + request);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		httpConnection.setRequestProperty("Content-Type", "application/json");
		requestCount++;

		int responseCode = httpConnection.getResponseCode();

		if (responseCode != 200) {
			if (responseCode == 429 && httpConnection.getHeaderField("Retry-After") != null) {
				double sleepFloatingPoint = Double.valueOf(httpConnection.getHeaderField("Retry-After"));
				double sleepMillis = 1000 * sleepFloatingPoint;
				Thread.sleep((long) sleepMillis);
				return getContent(request);
			} else if (responseCode == 403 && httpConnection.getHeaderField("X-RateLimit-Reset") != null) {
				long reset = Long.valueOf(httpConnection.getHeaderField("X-RateLimit-Reset"));
				Date dateFuture = new Date(reset * 1000);
				long sleepFloatingPoint = dateFuture.getTime() - new Date().getTime();
				Thread.sleep(sleepFloatingPoint);
				return getContent(request);
			} else if (responseCode == 422) {
				throw new RESTException("Only the first 1000 search results are available");
			}
			throw new RuntimeException("Response code was not 200. Detected response was " + responseCode);
		}

		InputStream response = httpConnection.getInputStream();

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
