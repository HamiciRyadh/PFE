package usthb.lfbservices.com.pfe.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
    private static final String BASE_URL = "http://192.168.1.2:8080/PFE-EE/api/";

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

    /**
     * A constructor.
     */
    public PfeAPI() {
        retrofit = this.getClient();
        pfeService = retrofit.create(PfeService.class);
    }

    /**
     * Instantiate the {@link Retrofit} object if it is null, and setting {@link Gson} to handle
     * automatic conversion of JSON responses and {@link RxJava2CallAdapterFactory} to handle
     * RxAndroid's Observable type.
     */
    public Retrofit getClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
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

}
