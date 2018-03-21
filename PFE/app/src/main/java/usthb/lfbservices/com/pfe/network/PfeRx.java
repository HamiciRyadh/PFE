package usthb.lfbservices.com.pfe.network;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.utils.DisposableManager;

/**
 * A class exposing static methods to interact with Retrofit that handles the network calls
 * and the associated handling.
 */

public class PfeRx
{
    private static final String TAG = PfeRx.class.getName();

    /**
     * The object used for the network calls.
     */
    private static PfeAPI pfeAPI = new PfeAPI();

    public static void getProducts(final Activity activity, final int layoutResourceId) {
        pfeAPI.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getProduits : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(JsonArray jsonArray) {
                        Log.e(TAG, "getProduits : onNext");
                        Log.e(TAG, jsonArray.toString());

                        ArrayList<Product> products = new ArrayList<Product>();
                        Product product;
                        //JsonArray jsonArray = jsonObject.getAsJsonArray("products");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject object = jsonArray.get(i).getAsJsonObject();

                            product = new Product();
                            product.setProductId(object.get("productId").getAsInt());
                            product.setProductName(object.get("productName").getAsString());
                            product.setProductType(object.get("productType").getAsInt());
                            product.setProductCategory(object.get("productCategory").getAsInt());
                            product.setProductTradeMark(object.get("productTradeMark").getAsString());
                            products.add(product);
                        }


                        ProductsAdapter productsAdapter = new ProductsAdapter(activity, layoutResourceId, products);

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        listView.setAdapter(productsAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getProduits : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "getProduits : onComplete");
                    }
                });
    }

    public static void search(final Activity activity, final int layoutResourceId, final String searchString)
    {
        pfeAPI.search(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        Log.e(TAG, "Search : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        Log.e(TAG, "Search : onNext");
                        Log.e(TAG, jsonObject.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Search : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Search : onComplete");
                    }
                });
    }

    /**
     * Searches for the Products corresponding to the specified Category, displays the results
     * if there are any, or an appropriate message in the case where there are no corresponding
     * results or an error occurred.
     * @param activity The activity in which to display the results of the network call, or the
     *                 appropriate message.
     * @param layoutResourceId The layout id used by{@link ProductsAdapter} the Adapter to display the results.
     * @param category The Category id used for the network call to filter the Products.
     */
    public static void searchCategory(final Activity activity, final int layoutResourceId, final int category) {
        final ArrayList<Product> products = new ArrayList<Product>();
        final ProductsAdapter productsAdapter = new ProductsAdapter(activity, layoutResourceId, products);

        pfeAPI.searchCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Product>>() {
                    /**
                     * This method is called before we start the network call.
                     * Activates the PorgressBar to show the user that there is a background
                     * activity.
                     */
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        Log.e(TAG, "Search Category : onSubscribe");
                        DisposableManager.add(d);

                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    }

                    /**
                     * This method is called when the results of the network call are received,
                     * Adds all the results to the {@link ProductsAdapter} before setting it to the
                     * ListView in addition to an EmptyView to display a content for the case of
                     * 0 corresponding results or the occurrence of an error.
                     * @param list The List of Products received from the network call.
                     */
                    @Override
                    public void onNext(List<Product> list) {
                        Log.e(TAG, "Search Category : onNext");
                        Log.e(TAG, list.toString());

                        productsAdapter.addAll(list);

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);
                        listView.setAdapter(productsAdapter);
                        listView.setEmptyView(emptyTextView);
                    }

                    /**
                     * This method is called if an error occurred during the network call.
                     * Disables the ProgressBar, sets the {@link ProductsAdapter} to the
                     * ListView and and EmptyView to display an error message.
                     * @param e A Throwable corresponding to the error.
                     */
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Search Category : onError " + e.toString());

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);

                        listView.setAdapter(productsAdapter);
                        listView.setEmptyView(emptyTextView);

                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null) emptyTextView.setText(activity.getString(R.string.error_occured));
                    }

                    /**
                     * This method is called when the network call ended successfully.
                     * Disables the ProgressBar and in the case of 0 corresponding results (empty
                     * ListView) sets a corresponding message in the EmptyView.
                     */
                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Search Category : onComplete");

                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null) {
                            if (products.size() == 0) emptyTextView.setText(activity.getString(R.string.no_corresponding_product));
                            else emptyTextView.setText("");
                        }
                    }
                });
    }
}
