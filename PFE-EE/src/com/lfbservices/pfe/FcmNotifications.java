package com.lfbservices.pfe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class FcmNotifications {


 public final static String AUTH_KEY_FCM ="AIzaSyDpu4063a0w3YrDnqZFlClqZEonXh9p5XM";
 public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";


public static String pushFCMNotification(String userDeviceIdKey, String title, String message) throws Exception{

		    String result = "";
		    URL url = new URL(API_URL_FCM);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		    conn.setUseCaches(false);
		    conn.setDoInput(true);
		    conn.setDoOutput(true);

		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
		    conn.setRequestProperty("Content-Type", "application/json");

		    JSONObject json = new JSONObject();

		    json.put("to", userDeviceIdKey);
		    JSONObject info = new JSONObject();
		    info.put("title", title); 
		    info.put("body", message);  

		                                                             
		    json.put("notification", info);
		    try {
		        OutputStreamWriter wr = new OutputStreamWriter(
		                conn.getOutputStream());
		        wr.write(json.toString());
		        wr.flush();

		        BufferedReader br = new BufferedReader(new InputStreamReader(
		                (conn.getInputStream())));

		        String output;
		        System.out.println("Output from Server .... \n");
		        while ((output = br.readLine()) != null) {
		            System.out.println(output);
		        }
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		       
		    }
		   
		    return result;

		

	}}