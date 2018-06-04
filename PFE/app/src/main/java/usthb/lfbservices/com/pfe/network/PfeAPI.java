package usthb.lfbservices.com.pfe.network;

import android.util.Base64;
import android.util.Log;

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
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.Result;
import usthb.lfbservices.com.pfe.models.SalesPoint;

/**
 * A class used by Retrofit to execute network calls.
 */

public class PfeAPI {
    /**
     * The url of the Web Service.
     */
    private static final String WEB_SERVICE_BASE_URL = "http://192.168.1.6:8080/PFE-EE/api/";
    /**
     * The url of the Google Maps API.
     */
    private static final String GOOGLE_MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api/";

    /**
     * A Retrofit object to instantiate the interface representing the exposed methods of the
     * Web Service.
     */
    private Retrofit retrofit = null;

    /**
     * A {@link PfeService} object containing the exposed methods of the Web Service to instantiate
     * with {@link Retrofit}.
     */
    private PfeService pfeService;
    /**
     * An {@link ItineraryService} object containing the needed exposed methods of the Google Maps API
     * to instantiate with {@link Retrofit}.
     */
    private ItineraryService itineraryService;

    /**
     * A static initialization of the {@link PfeAPI} class.
     */
    private static final PfeAPI INSTANCE = new PfeAPI();

    /**
     * A static method used to get an instance of the {@link PfeAPI} class and avoid the creation
     * of new instances of it.
     * @return An instance of the {@link PfeAPI} class.
     */
    public static PfeAPI getInstance() {
        return INSTANCE;
    }

    /**
     * The authorization string that will be added to the Authorization header parameter in every
     * request.
     */
    private String authorization = "";

    /**
     * A private constructor to avoid initialization without the use of the {@link PfeAPI#getInstance()} method.
     */
    private PfeAPI() {
        retrofit = this.getClient(WEB_SERVICE_BASE_URL);
        pfeService = retrofit.create(PfeService.class);
        retrofit = this.getClient(GOOGLE_MAPS_API_BASE_URL);
        itineraryService = retrofit.create(ItineraryService.class);
    }

    /**
     * Instantiates the {@link Retrofit} object if it is null, and setting {@link Gson} to handle
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

    /**
     * Builds an {@link OkHttpClient} with logging option and and {@link Interceptor} to automatically
     * add an Authorization header parameter to the requests and sets the connect, read and write
     * timeouts.
     * @return An instance of the {@link OkHttpClient} class.
     */
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

    /**
     * Initializes the authorization string by encoding the given mail address and password in {@link Base64}.
     * @param mailAddress The mail address of the authenticated user.
     * @param password The password of the authenticated user.
     */
    public void setAuthorization(final String mailAddress, final String password) {
        authorization = android.util.Base64.encodeToString((mailAddress + ":" + password).getBytes(), Base64.NO_WRAP);
    }


    public Observable<GooglePlaceDetails> getPlaceDetails(final String key, final String salesPointId) {
        return itineraryService.getPlaceDetails(key, salesPointId);
    }

    /**
     * Operates a network call to retrieve a {@link Result} corresponding to the given {@link Product#productBarcode}.
     * @param productBarcode The {@link Product#productBarcode}.
     * @return An {@link Observable} of {@link Result}.
     */
    public Observable<Result> searchFromProductBarcode(final String productBarcode) {
        return pfeService.searchFromProductBarcode(productBarcode);
    }

    /**
     * Operates a network call to retrieve a {@link List} of {@link Product} resulting from the user's search string.
     * @param value The user's search string.
     * @return An {@link Observable} of a {@link List} of {@link Product}.
     */
    public Observable<List<Product>> searchFromQuery(final String value) {
        return pfeService.searchFromQuery(value);
    }

    /**
     * Operates a network call to the Web Server to obtain a List of {@link Product} corresponding
     * to the specified {@link usthb.lfbservices.com.pfe.models.Category}
     * @param categoryId The Category id used for the network call to filter the Products.
     * @return An {@link Observable} of a {@link List} of {@link Product}.
     */
    public Observable<List<Product>> searchCategory(final int categoryId) {
        return pfeService.searchCategory(categoryId);
    }

    /**
     * Operates a network call to verify if the user exists.
     * @param mailAddress The user's mail address.
     * @param password The user's password.
     * @return An {@link Observable} of boolean : true if the user exists, false otherwise.
     */
    public Observable<Boolean> connect(final String mailAddress, final String password) {
        Log.e("Header Authorization : ", "Basic " + android.util.Base64.encodeToString((mailAddress + ":" + password).getBytes(), Base64.NO_WRAP));
        return pfeService.connect(mailAddress, password);
    }

    /**
     * Operates a network call to register the user.
     * @param mailAddress The user's mail address.
     * @param password The user's password.
     * @return An {@link Observable} of a boolean : true if the user was successfully added, false otherwise.
     */
    public Observable<Boolean> register(final String mailAddress, final String password) {
        return pfeService.register(mailAddress, password);
    }

    /**
     * Operates a network call to add the device's Firebase token id.
     * @param deviceId The device's Firebase token id.
     * @return An {@link Observable} of a boolean : true if it was successfully added, false otherwise.
     */
    public Observable<Boolean> setFirebaseTokenId(final String deviceId) {
        return pfeService.setFirebaseTokenId(deviceId);
    }

    /**
     * Operates a network call to update the device's Firebase token id.
     * @param previousDeviceId The previous Firebase token id.
     * @param newDeviceId The new Firebase token id.
     * @return An {@link Observable} of a boolean : true if the update was successful, false otherwise.
     */
    public Observable<Boolean> updateFirebaseTokenId(final String previousDeviceId, final String newDeviceId) {
        return pfeService.updateFirebaseTokenId(previousDeviceId, newDeviceId);
    }

    /**
     * Operates a network call to remove the device's Firebase token id.
     * @param deviceId The device's current Firebase token id.
     * @return An {@link Observable} of a boolean : true if the deletion was successful, false otherwise.
     */
    public Observable<Boolean> removeFirebaseTokenId(final String deviceId) {
        return pfeService.removeFirebaseTokenId(deviceId);
    }

    /**
     * Operates a network call to add a the authenticated user in the notifications list of a {@link Product}
     * identified by its {@link Product#productBarcode} in a {@link SalesPoint} identified by its
     * {@link SalesPoint#salesPointId}.
     * @param salesPointId The {@link SalesPoint#salesPointId}.
     * @param productBarcode The {@link Product#productBarcode}.
     * @return An {@link Observable} of a boolean : true if he was successfully added, false otherwise.
     */
    public Observable<Boolean> addToNotificationsList(final String salesPointId, final String productBarcode) {
        return pfeService.addToNotificationsList(salesPointId, productBarcode);
    }

    /**
     * Operates a network call to remove the authenticated user from the notifications list of a {@link Product}
     * identified by its {@link Product#productBarcode} in a {@link SalesPoint} identified by its
     * {@link SalesPoint#salesPointId}.
     * @param salesPointId The {@link SalesPoint#salesPointId}.
     * @param productBarcode The {@link Product#productBarcode}.
     * @return An {@link Observable} of a boolean : true if the deletion was successful, false otherwise.
     */
    public Observable<Boolean> removeFromNotificationsList(final String salesPointId, final String productBarcode) {
        return pfeService.removeFromNotificationsList(salesPointId, productBarcode);
    }

    /**
     * Operates a network call to get the newest informations about a group of {@link Product} and
     * their associated {@link SalesPoint}.
     * @param salesPointsIds A {@link List} of {@link String}.
     * @return An {@link Observable} of a {@link List} of {@link ProductSalesPoint} representing the
     * newest informations about the products and sales points sent.
     */
    public Observable<List<ProductSalesPoint>> getNewestInformations(final List<String> salesPointsIds) {
        return pfeService.getNewestInformations(salesPointsIds);
    }


    public Call<GoogleDirections> getDistanceDuration(final String apiKey, final String units, final String origin,
                                                      final String destination, final String mode) {
        return itineraryService.getDistanceDuration(apiKey, units, origin, destination, mode);
    }

    public Call<GoogleAutocompleteResponse> getAutoCompleteSearchResults(final String apiKey, final String searchTerm,
                                                                         final String location, final long radius) {
        return itineraryService.getAutoCompleteSearchResults(apiKey, searchTerm, location, radius);
    }

    public Call<GooglePlaceDetails> getLatLng(final String apiKey, final String placeid) {
        return itineraryService.getLatLng(apiKey, placeid);
    }

    /**
     * Operates a network call to retrieve the informations of the {@link Product} associated with the given
     * @param productBarcode The {@link Product#productBarcode} of the {@link Product}.
     * @return An {@link Observable} of the {@link Product} associated with the given {@link Product#productBarcode}.
     */
    public Observable<Product> getProductDetails(String productBarcode) {
        return pfeService.getProductDetails(productBarcode);
    }

    /**
     * Operates a network call to retrieve the characteristics of the {@link Product} associated with the
     * given {@link Product#productBarcode}.
     * @param productBarcode The {@link Product#productBarcode} of the {@link Product}.
     * @return An {@link Observable} of a {@link List} of {@link KeyValue} representing the
     * characteristics of the {@link Product} associated with the given {@link Product#productBarcode}.
     */
    public Observable<List<KeyValue>>  getProductCharacteristics(String productBarcode) {
        return pfeService.getProductCharacteristics(productBarcode);
    }

    /**
     * Operates a network call to retrieve the search propositions corresponding to the given search
     * query.
     * @param query The search query for which the propositions are made.
     * @return An {@link Observable} of a {@link List} of {@link String} representing the search
     * propositions relative to the given search query.
     */
    public Observable<List<String>> getSearchPropositions(final String query) {
        return pfeService.getSearchPropositions(query);
    }


    //TODO : NEW mais à modifier ! envoyer aussi la liste des points de vente actuelle
    public Observable<List<ProductSalesPoint>> getProductSalesPoint(final String productBarcode) {
        return pfeService.getProductSalesPoint(productBarcode);
    }
}