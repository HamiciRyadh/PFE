package usthb.lfbservices.com.pfe.network;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("SalesPoint/Place/details/{sales_point_id}")
    Observable<SalesPoint> getPlaceDetails(@Path("sales_point_id") String salesPointId);

    @GET("Search")
    Observable<List<Product>> searchFromQuery(@Query("value") String value);

    @GET("Search/{product_barcode}")
    Observable<Result> searchFromProductBarcode(@Path("product_barcode") String productBarcode);

    /**
     * Gives a List of Products for a given Category
     * @param category The id of the Category
     * @return A List of Products corresponding to the specified Category
     */
    @GET("Search/Category/{category_id}")
    Observable<List<Product>> searchCategory(@Path("category_id") int category);

    @POST("User/Connect")
    @FormUrlEncoded
    Observable<Boolean> connect(@Field("mailAddress") String mailAddress,
                                @Field("password") String password);

    @POST("User/Register")
    @FormUrlEncoded
    Observable<Boolean> register(@Field("mailAddress") String mailAddress,
                                @Field("password") String password);

    @PUT("User/Device/Add")
    @FormUrlEncoded
    Observable<Boolean> setFirebaseTokenId(@Field("deviceId") String deviceId);

    @POST("User/Device/Update")
    @FormUrlEncoded
    Observable<Boolean> updateFirebaseTokenId(@Field("previousDeviceId") String previousDeviceId,
                                              @Field("newDeviceId") String newDeviceId);

    @DELETE("User/Device/Remove")
    Observable<Boolean> removeFirebaseTokenId(@Query("deviceId") String deviceId);

    @PUT("Notification/AddToNotificationsList")
    @FormUrlEncoded
    Observable<Boolean> addToNotificationsList(@Field("sales_point_id") String salesProductId,
                                              @Field("product_barcode") String productBarcode);

    @DELETE("Notification/RemoveFromNotificationsList")
    @FormUrlEncoded
    Observable<Boolean> removeFromNotificationsList(@Field("sales_point_id") String salesProductId,
                                                   @Field("product_barcode") String productBarcode);
}
