package usthb.lfbservices.com.pfe.network;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.GoogleAutocompleteResponse;
import usthb.lfbservices.com.pfe.itinerary.direction.GoogleDirections;
import usthb.lfbservices.com.pfe.itinerary.place.GooglePlaceDetails;

/**
 * An interface that is used by Retrofit to interact with some exposed methods of Google Maps API.
 */

public interface ItineraryService {
    @GET("directions/json")
    Call<GoogleDirections> getDistanceDuration(@Query("key") String apiKey,
                                               @Query("units") String units,
                                               @Query("origin") String origin,
                                               @Query("destination") String destination,
                                               @Query("mode") String mode);

    @GET("place/autocomplete/json")
    Call<GoogleAutocompleteResponse> getAutoCompleteSearchResults(@Query("key") String apiKey,
                                                                  @Query("input") String searchTerm,
                                                                  @Query("location") String location,
                                                                  @Query("radius") long radius);

    @GET("place/details/json")
    Call<GooglePlaceDetails> getLatLng(@Query("key") String apiKey,
                                       @Query("placeid") String placeId);


    @GET("place/details/json?sensor=false")
    Observable<GooglePlaceDetails> getPlaceDetails(@Query("key") String apiKey,
                                                   @Query("placeid") String placeId);
}
