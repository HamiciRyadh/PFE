package com.lfbservices.pfe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.lfbservices.pfe.dao.Access;

public class FcmNotifications {


	public final static String AUTH_KEY_FCM ="AIzaSyDpu4063a0w3YrDnqZFlClqZEonXh9p5XM";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
	
	
	public static String pushFCMNotification(final String userDeviceId, final String salesPointId, final String productBarcode,
											 final int productQuantity, final double productPrice) throws Exception {
		
		String productName = Access.getProductById(productBarcode).getProductName();
		//TODO: Put the provided data in the notification message data
		URL url = new URL(API_URL_FCM);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
	    conn.setRequestProperty("Content-Type", "application/json");
	    
	    JSONObject json = new JSONObject();
	    json.put("to", userDeviceId);
	    
	    
	    JSONObject info = new JSONObject();
	    info.put("title","Du nouveau ! "+ productName ); 
	    info.put("body", "Disponibilité : " + productQuantity + "    -     "+ "Prix :" +productPrice );                  
	    json.put("notification", info);
	    
	    
	    JSONObject data = new JSONObject();
	    data.put("salesPointName", Access.getSalesPointById(salesPointId).getSalesPointName());
	    data.put("productName", Access.getProductById(productBarcode).getProductName());
	    data.put("productQuantity", productQuantity);
	    data.put("productPrice", productPrice);		

	    json.put("data", data);
	    
	    
	    
	    try {
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(json.toString());
	        wr.flush();
	        
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

	        String output;
	        System.out.println("Output from Server .... \n");
	        while ((output = br.readLine()) != null) {
	            System.out.println(output);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return json.toString();
	}
}