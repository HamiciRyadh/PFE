package usthb.lfbservices.com.pfe.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import usthb.lfbservices.com.pfe.models.Product;

/**
 * Created by ryadh on 29/01/18.
 * An interface that is used by Retrofit to interact with the exposed methods of the Web Service
 */

public interface PfeService
{

    @GET("Products")
    Observable<JsonArray> getProducts();


    @GET("Search")
    Observable<JsonObject> search(@Query("value") String value);

    /**
     * Gives a List of Products for a given Category
     * @param category The id of the Category
     * @return A List of Products corresponding to the specified Category
     */
    @GET("Products/Search/category/{product_Category}")
    Observable<List<Product>> searchCategory(@Path("product_Category") int category);
}
