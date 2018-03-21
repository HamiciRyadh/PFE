package webService; 

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder; 
 
import org.json.JSONException;
import org.json.JSONObject;
 
import model.SalesPoint;
 


public class GoogleAPI {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_DETAILS = "/details"; 

    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyBMzBd0cHQW0SxrUeOQWxhuimse0OPI8TI";


    
    public static SalesPoint details(String salespointId) throws Exception {
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

        try {
            
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
             
            
            JSONObject geometry = jsonObj.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            double lng = location.getDouble("lng");
            double lat = location.getDouble("lat");
            String name =jsonObj.getString("name");
            String formatted_address =jsonObj.getString("formatted_address");
          
                 
          return ( new SalesPoint(salespointId,lat,lng,name,formatted_address));
 			            
            
        } catch (JSONException e) {
           
        }
		return null;

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

        try {
            
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
             
           
            String phoneNumber;
           if (jsonObj.has("formatted_phone_number")) {
                 phoneNumber = jsonObj.getString("formatted_phone_number"); }
                    else   phoneNumber ="Non Disponible";
               
            String webSite;
             if (jsonObj.has("website")) {
                   webSite = jsonObj.getString("webSite"); }
                        else webSite = "Non Disponible";   
             
             float rating ;
             if (jsonObj.has("rating")) {
                 rating = BigDecimal.valueOf(jsonObj.getDouble("rating")).floatValue(); }
                     // a Revoir 
                      else  rating =  0;
             
            
             
          return ( new SalesPoint(salespointId,phoneNumber,webSite,rating));
 			
           
            
        } catch (JSONException e) {
           
        }
		return null;

  }

}
    
 