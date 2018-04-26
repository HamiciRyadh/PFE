package usthb.lfbservices.com.pfe.network;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import usthb.lfbservices.com.pfe.itinerary.autocomplete.GoogleAutocompleteResponse;
import usthb.lfbservices.com.pfe.itinerary.direction.GoogleDirections;
import usthb.lfbservices.com.pfe.itinerary.place.GooglePlaceDetails;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.Result;
import usthb.lfbservices.com.pfe.models.SalesPoint;

/**
 * A class used by Retrofit to execute network calls.
 */

public class PfeAPI {
    /**
     * The url of the Web Service to use.
     */
    private static final String WEB_SERVICE_BASE_URL = "http://192.168.1.6:8080/PFE-EE/api/";
    private static final String GOOGLE_MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api/";

    /**
     * A Retrofit object to instantiate the interface representing the exposed methods of the
     * Web Service.
     */
    private Retrofit retrofit = null;
    /**
     * A {@link PfeService} object containing the exposed methods of the Web Service to
     * instantiate with Retrofit.
     */
    private PfeService pfeService;
    private ItineraireService itineraireService;

    private static final PfeAPI WEB_SERVICE_INSTANCE = new PfeAPI();

    public static PfeAPI getInstance() {
        return WEB_SERVICE_INSTANCE;
    }

    private String authorization = "";

    /**
     * A constructor.
     */
    private PfeAPI() {
        retrofit = this.getClient(WEB_SERVICE_BASE_URL);
        pfeService = retrofit.create(PfeService.class);
        retrofit = this.getClient(GOOGLE_MAPS_API_BASE_URL);
        itineraireService = retrofit.create(ItineraireService.class);
    }

    /**
     * Instantiate the {@link Retrofit} object if it is null, and setting {@link Gson} to handle
     * automatic conversion of JSON responses and {@link RxJava2CallAdapterFactory} to handle
     * RxAndroid's Observable type.
     */
    private Retrofit getClient(final String retrofitBaseUrl) {
        OkHttpClient okHttpClient = buildOkHttpClient();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(retrofitBaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    private OkHttpClient buildOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        "Basic " + authorization);
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .build();

        return okHttpClient;
    }

    public Observable<SalesPoint> getPlaceDetails(String salesPointId) {
        return pfeService.getPlaceDetails(salesPointId);
    }

    public Observable<Result> searchFromProductId(final int productId) {
        return pfeService.searchFromProductId(productId);
    }

    public Observable<List<Product>> searchFromQuery(final String value) {
        return pfeService.searchFromQuery(value);
    }

    /**
     * Operates a network call to the Web Server to obtain a List of {@link Product} corresponding
     * to the specified {@link usthb.lfbservices.com.pfe.models.Category}
     * @param category The Category id used for the network call to filter the Products.
     * @return An {@link Observable} List of {@link Product} to use with AndroidRx
     */
    public Observable<List<Product>> searchCategory(final int category) {
        return pfeService.searchCategory(category);
    }

    public Observable<Boolean> connect(final String mailAddress, final String password) {
        return pfeService.connect(mailAddress, password);
    }

    public Observable<Boolean> register(final String mailAddress, final String password) {
        return pfeService.register(mailAddress, password);
    }


    public void setAuthorization(final String mailAddress, final String password) {
        authorization = android.util.Base64.encodeToString((mailAddress + ":" + password).getBytes(), Base64.NO_WRAP);
    }

    public Call<GoogleDirections> getDistanceDuration(final String apiKey, final String units, final String origin,
                                                      final String destination, final String mode) {
        return itineraireService.getDistanceDuration(apiKey, units, origin, destination, mode);
    }

    public Call<GoogleAutocompleteResponse> getAutoCompleteSearchResults(final String apiKey, final String searchTerm,
                                                                         final String location, final long radius) {
        return itineraireService.getAutoCompleteSearchResults(apiKey, searchTerm, location, radius);
    }

    public Call<GooglePlaceDetails> getLatLng(final String apiKey, final String placeid) {
        return itineraireService.getLatLng(apiKey, placeid);
    }
}
