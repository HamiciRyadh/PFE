package usthb.lfbservices.com.pfe.network;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.utils.DisposableManager;

/**
 * Created by ryadh on 29/01/18.
 */

public class PfeRx
{
    private static final String TAG = PfeRx.class.getName();
    private static PfeAPI pfeAPI = new PfeAPI();

    public static void getProducts(final Activity activity)
    {
        pfeAPI.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        DisposableManager.add(d);
                        Log.e(TAG, "getProduits : onSubscribe");
                    }

                    @Override
                    public void onNext(JsonArray jsonArray) {
                        Log.e(TAG, "getProduits : onNext");
                        Log.e(TAG, jsonArray.toString());

                        ArrayList<Product> products = new ArrayList<Product>();
                        Product product;
                        //JsonArray jsonArray = jsonObject.getAsJsonArray("products");
                        for (int i = 0; i < jsonArray.size(); i++)
                        {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();

                            product = new Product();
                            product.setProductId(object.get("productId").getAsInt());
                            product.setProductName(object.get("productName").getAsString());
                            product.setProductType(object.get("productType").getAsString());
                            product.setProductCategory(object.get("productCategory").getAsString());
                            product.setProductTradeMark(object.get("productTradeMark").getAsString());
                            products.add(product);
                        }


                        ProductsAdapter productsAdapter = new ProductsAdapter(activity, products);

                        ListView listView = (ListView) activity.findViewById(R.id.list_view_products);
                        listView.setAdapter(productsAdapter);
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e(TAG, "getProduits : onError " + e.toString());
                    }

                    @Override
                    public void onComplete()
                    {
                        Log.e(TAG, "getProduits : onComplete");
                    }
                });
    }
}
