package usthb.lfbservices.com.pfe.itinerary.autocomplete;

import com.google.gson.annotations.SerializedName;


public class Prediction {

        private String description;

        @SerializedName("place_id")
        private String placeID;

        public String getDescription() {
                return description;
        }

        public String getPlaceID() {
                return placeID;
        }
}
