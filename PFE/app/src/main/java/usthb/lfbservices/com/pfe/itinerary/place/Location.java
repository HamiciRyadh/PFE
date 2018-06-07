package usthb.lfbservices.com.pfe.itinerary.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Location {

    @SerializedName("lat")
    @Expose
    private double Lat;

    @SerializedName("lng")
    @Expose
    private  double lng;

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return lng;
    }
}
