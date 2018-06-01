package usthb.lfbservices.com.pfe.itinerary.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ryadh on 30/05/18.
 */

public class AddressComponents {

    @SerializedName("types")
    @Expose
    private String[] types;

    @SerializedName("long_name")
    @Expose
    private String longName;

    public String[] getTypes() {
        return types;
    }

    public String getLongName() {
        return longName;
    }
}
