package usthb.lfbservices.com.pfe.network;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.Result;
import usthb.lfbservices.com.pfe.models.SalesPoint;

/**
 * Created by ryadh on 29/01/18.
 * An interface that is used by Retrofit to interact with the exposed methods of the Web Service
 */

public interface PfeService
{

    @GET("Products/Place/details/{salesPointId}")
    Observable<SalesPoint> getPlaceDetails(@Path("salesPointId") String salesPointId);

    @GET("Products/Search")
    Observable<List<Product>> searchFromQuery(@Query("value") String value);

    @GET("Products/Search/{productId}")
    Observable<Result> searchFromProductId(@Path("productId") int productId);

    /**
     * Gives a List of Products for a given Category
     * @param category The id of the Category
     * @return A List of Products corresponding to the specified Category
     */
    @GET("Products/Category/{categoryId}")
    Observable<List<Product>> searchCategory(@Path("categoryId") int category);

    @POST("User/Connect")
    @FormUrlEncoded
    Observable<Boolean> connect(@Field("mailAddress") String mailAddress,
                                @Field("password") String password);

    @POST("User/Register")
    @FormUrlEncoded
    Observable<Boolean> register(@Field("mailAddress") String mailAddress,
                                @Field("password") String password);
}
