package com.lfbservices.pfe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.lfbservices.pfe.dao.Access;

/**
 * This class handles the process of sending messages to the users' devices.
 */
public class FcmNotifications {

	/**
	 * The base url used to query Firebase's servers and send messages.
	 */
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
	/**
	 * The Firebase API Key used to identify the web sevrice and send the messages.
	 */
	public final static String AUTH_KEY_FCM ="AIzaSyDpu4063a0w3YrDnqZFlClqZEonXh9p5XM";
	
	/**
	 * This method sends a message to the user identified by its userDeviceId, the notification will contain all 
	 * the other parameters of this method so that the mobile application can then show a notification accordingly.
	 * @param userDeviceId The Firebase token id representing the user's device to notify.
	 * @param salesPointId The {@link SalesPoint#salesPointId} of the {@link SalesPoint] where the modification occured.
	 * @param productBarcode The {@link Product#productBarcode} of the {@link Product} for which the modification occured.
	 * @param productQuantityOld The quantity of the {@link Product} prior to the modification.
	 * @param productPriceOld The price of the {@link Product} prior to the modification.
	 * @param productQuantityNew The quantity of the {@link Product} after the modification.
	 * @param productPriceNew The price of the {@link Product} after the modification.
	 * @return A JSON {@link String} representing the query sent to the Firebase's server.
	 * @throws Exception If an error occurred when sending the JSON request.
	 */
	public static String pushFCMNotification(final String userDeviceId, final String salesPointId, final String productBarcode,
											 final int productQuantityOld, final double productPriceOld,
											 final int productQuantityNew, final double productPriceNew) throws Exception {
		
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
	  	    
	    JSONObject data = new JSONObject();
	    data.put("salesPointId", salesPointId);
	    data.put("salesPointName", Access.getSalesPointById(salesPointId).getSalesPointName());
	    data.put("productName", Access.getProductById(productBarcode).getProductName());
	    data.put("productQuantityOld", productQuantityOld);
	    data.put("productPriceOld", productPriceOld);
	    data.put("productQuantityNew", productQuantityNew);
	    data.put("productPriceNew", productPriceNew);
	    data.put("productBarcode", productBarcode);

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