package usthb.lfbservices.com.pfe.itinerary.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import usthb.lfbservices.com.pfe.models.SalesPoint;

/**
 * Created by imene on 16/04/18.
 */

public class ResultDetails {

    @SerializedName("place_id")
    @Expose
    private String placeId;


    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;

    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    @SerializedName("photos")
    @Expose
    private Photo[] photos;

    @SerializedName("address_components")
    @Expose
    private AddressComponents[] addressComponents;

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getWebsite() {
        return website;
    }

    public float getRating() {
        return rating;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public Photo[] getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public AddressComponents[] getAddressComponents() {
        return addressComponents;
    }
}
