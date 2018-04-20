package usthb.lfbservices.com.pfe.itinerary.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GoogleDirections {

    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }


}