package com.lfbservices.pfe.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lfbservices.pfe.model.SalesPoint;

public class GoogleAPI {

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

	private static final String TYPE_DETAILS = "/details";

	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyBMzBd0cHQW0SxrUeOQWxhuimse0OPI8TI";
	
	
	public static SalesPoint salespointInformations  (String salespointId) throws Exception {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_DETAILS);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + API_KEY);
			sb.append("&placeid=" + URLEncoder.encode(salespointId, "utf-8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			System.out.println("Error processing Places API URL");

		} catch (IOException e) {
			System.out.println("Error connecting to Places API");

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		double lng = 0.0d;
		double lat = 0.0d;
		String name = "";
	

		try {

			JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

			JSONObject geometry = jsonObj.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");
			
			lng = location.getDouble("lng");
			lat = location.getDouble("lat");
			name = jsonObj.getString("name");
		

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return (new SalesPoint(salespointId, lat, lng, name));

	}

	public static SalesPoint details(String salespointId) throws Exception {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_DETAILS);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + API_KEY);
			sb.append("&placeid=" + URLEncoder.encode(salespointId, "utf-8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			System.out.println("Error processing Places API URL");

		} catch (IOException e) {
			System.out.println("Error connecting to Places API");

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		double lng = 0.0d;
		double lat = 0.0d;
		String name = "";
		String formatted_address = "";
		String wilaya = "";

		try {

			JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

			JSONObject geometry = jsonObj.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");
			
			lng = location.getDouble("lng");
			lat = location.getDouble("lat");
			name = jsonObj.getString("name");
			formatted_address = jsonObj.getString("formatted_address");
			JSONArray jsonArray = jsonObj.getJSONArray("address_components");
			JSONObject tempsObject;
			JSONArray tempsArray;
			
			for (int i = 0; i < jsonArray.length(); i++) {
				tempsObject = jsonArray.getJSONObject(i);
				tempsArray = tempsObject.getJSONArray("types");
				
				if (tempsArray.getString(0).contains("administrative_area_level")) {
					wilaya = tempsObject.getString("long_name");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return (new SalesPoint(salespointId, lat, lng, name));

	}

	public static SalesPoint moreDetails(String salespointId) throws Exception {
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append(TYPE_DETAILS);
			sb.append(OUT_JSON);
			sb.append("?sensor=false");
			sb.append("&key=" + API_KEY);
			sb.append("&placeid=" + URLEncoder.encode(salespointId, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			System.out.println("Error processing Places API URL");

		} catch (IOException e) {
			System.out.println("Error connecting to Places API");

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		String phoneNumber="";
		String website="";
		double rating=0.0d;
		String salesPointPhotoReference="";

		try {

			JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
			
			if (jsonObj.has("international_phone_number")) {
				phoneNumber = jsonObj.getString("international_phone_number");
			} else {
				if (jsonObj.has("formatted_phone_number")) {
					phoneNumber = jsonObj.getString("formatted_phone_number");
				} else {
					phoneNumber = "Non disponible.";
				}
			}

			if (jsonObj.has("website")) {
				website = jsonObj.getString("website");
			} else {
				website = "Non Disponible.";
			}
			
			if (jsonObj.has("rating")) {
				rating = BigDecimal.valueOf(jsonObj.getDouble("rating")).doubleValue();
			}
			else {
				rating = 0;
			}
			
			if (jsonObj.has("photos")) {
				JSONArray photos =  jsonObj.getJSONArray("photos");
				salesPointPhotoReference = photos.getJSONObject(0).getString("photo_reference");
			}

		} catch (JSONException e) {
				e.printStackTrace();
		}
		
		return null;
	}

}
