package usthb.lfbservices.com.pfe.network;

import com.google.gson.JsonArray;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by ryadh on 29/01/18.
 */

public interface PfeService
{

    @GET("Products")
    Observable<JsonArray> getProducts();

}
