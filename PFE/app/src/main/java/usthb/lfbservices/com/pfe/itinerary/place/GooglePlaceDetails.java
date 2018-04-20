package usthb.lfbservices.com.pfe.itinerary.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by imene on 16/04/18.
 */

public class GooglePlaceDetails {
    @SerializedName("result")
    @Expose
    private ResultDetails result;

    public ResultDetails getResult() {
        return result;
    }
}
