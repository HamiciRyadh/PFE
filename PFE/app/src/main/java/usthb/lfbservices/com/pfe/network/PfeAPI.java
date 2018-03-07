package usthb.lfbservices.com.pfe.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ryadh on 29/01/18.
 */

public class PfeAPI
{
    private static final String BASE_URL = "http://192.168.1.6:8080/PFE/api/";

    private Retrofit retrofit = null;
    private PfeService pfeService;

    public PfeAPI()
    {
        retrofit = this.getClient();
        pfeService = retrofit.create(PfeService.class);
    }

    public Retrofit getClient()
    {
        if (retrofit == null)
        {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

    public Observable<JsonArray> getProducts()
    {
        return pfeService.getProducts();
    }

}
