package usthb.lfbservices.com.pfe.itinerary.autocomplete;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleAutocompleteResponse {

        String status;

        @SerializedName("predictions")
        private  List<Prediction> predictionList;

        public String getStatus() {
                return status;
        }

        public List<Prediction> getPredictionList() {
                return predictionList;
        }
}
