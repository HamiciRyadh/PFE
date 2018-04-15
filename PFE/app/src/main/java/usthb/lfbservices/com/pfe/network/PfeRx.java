package usthb.lfbservices.com.pfe.network;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.LoginActivity;
import usthb.lfbservices.com.pfe.activities.MapsActivity;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.adapters.SalesPointsAdapter;
import usthb.lfbservices.com.pfe.adapters.CustomInfoWindowGoogleMap;
import usthb.lfbservices.com.pfe.models.BottomSheetDataSetter;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.Result;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

/**
 * A class exposing static methods to interact with Retrofit that handles the network calls
 * and the associated handling.
 */

public class PfeRx extends FragmentActivity {
    private static final String TAG = PfeRx.class.getName();

    /**
     * The object used for the network calls.
     */
    private static PfeAPI pfeAPI = PfeAPI.getInstance();

    public static void searchFromProductId(@NonNull final Activity activity,
                                           @NonNull final int productId) {

        pfeAPI.searchFromProductId(productId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SearchFromProductId : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(Result result) {
                        Log.e(TAG, "SearchFromProductId : onNext");
                        Log.e(TAG, "ProductSalesPoints : " + result.getProductSalesPoints());
                        Log.e(TAG, "SalesPoints : " + result.getSalesPoints());

                        final List<SalesPoint> salesPoints = result.getSalesPoints();
                        final List<ProductSalesPoint> productSalesPoints = result.getProductSalesPoints();
                        final Button showList = activity.findViewById(R.id.show_list_button);
                        final Map<LatLng, ProductSalesPoint> hashMap = new HashMap<LatLng, ProductSalesPoint>();

                        Collections.sort(salesPoints, new Comparator<SalesPoint>() {
                            public int compare(SalesPoint one, SalesPoint other) {
                                return one.getSalesPointName().compareTo(other.getSalesPointName());
                            }
                        });

                        Singleton.getInstance().setSalesPointList(salesPoints);
                        Singleton.getInstance().setProductSalesPointList(productSalesPoints);

                        final SalesPointsAdapter salesPointsAdapter1 = new SalesPointsAdapter(activity, R.layout.list_item_salespoint_product, (ArrayList) salesPoints);

                        final ListView listSalesPoints = activity.findViewById(R.id.list_view_sales_points);
                        listSalesPoints.setAdapter(salesPointsAdapter1);

                        final GoogleMap map = Singleton.getInstance().getMap();
                        if (map != null) {
                            map.clear();

                            LatLngBounds.Builder builder = null;
                            ProductSalesPoint productSalesPointTemps = new ProductSalesPoint();

                            for (SalesPoint salesPoint : salesPoints) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(salesPoint.getSalesPointLat(), salesPoint.getSalesPointLong()))
                                        .title(salesPoint.getSalesPointName())
                                        .snippet(salesPoint.getSalesPointAddress());

                                for (ProductSalesPoint productSalesPoint : productSalesPoints) {
                                    if (productSalesPoint.getSalespointId().equals(salesPoint.getSalesPointId())) {
                                        productSalesPointTemps = productSalesPoint;
                                        if (productSalesPoint.getProductQuantity() > 0) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        } else {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        }
                                    }
                                }
                                Marker marker = map.addMarker(markerOptions);
                                hashMap.put(marker.getPosition(), productSalesPointTemps);
                                if (builder == null) builder = new LatLngBounds.Builder();
                                builder.include(marker.getPosition());
                            }
                            if (builder != null) {
                                LatLngBounds bounds = builder.build();
                                // offset from the edges of the map in pixels
                                int padding = 200;
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                map.animateCamera(cu);
                            } else {
                                Toast.makeText(activity, "Aucun point de vente pour le produit sélectionné", Toast.LENGTH_LONG).show();
                            }
                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(activity, hashMap);
                            map.setInfoWindowAdapter(customInfoWindow);

                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    marker.showInfoWindow();
                                    return false;
                                }
                            });

                            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                    if (activity instanceof BottomSheetDataSetter) {
                                        for (SalesPoint salesPoint : salesPoints) {
                                            if (salesPoint.getSalesPointLatLng().equals(marker.getPosition())) {
                                                BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                                                bottomSheetDataSetter.setBottomSheetData(salesPoint);
                                                PfeRx.getPlaceDetails(activity, salesPoint.getSalesPointId());
                                                break;
                                            }
                                        }

                                    }
                                }
                            });
                        }

                        showList.setVisibility(View.VISIBLE);
                        showList.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                final ListView listView = activity.findViewById(R.id.list_view_sales_points);
                                if (listView.getVisibility() == View.VISIBLE) {
                                    showList.setText(activity.getString(R.string.sales_points_show_list));
                                    listView.setVisibility(View.GONE);
                                }
                                else {
                                    showList.setText(activity.getString(R.string.reduce));
                                    final SalesPointsAdapter salesPointsAdapter = new SalesPointsAdapter(activity, R.layout.sales_point_list, (ArrayList) salesPoints);
                                    listView.setAdapter(salesPointsAdapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                            if (activity instanceof BottomSheetDataSetter) {
                                                SalesPoint salesPoint = ((SalesPoint)(adapterView.getItemAtPosition(position)));
                                                BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                                                bottomSheetDataSetter.setBottomSheetData(salesPoint);
                                                PfeRx.getPlaceDetails(activity, salesPoint.getSalesPointId());

                                                //Zoomer sur le marqueur correspondant
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(salesPoint.getSalesPointLatLng(), 12f);
                                                map.animateCamera(cu);
                                            }
                                        }
                                    });
                                    listView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "SearchFromProductId : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "SearchFromProductId : onComplete");
                    }
                });
    }


   public static void searchFromQuery(@NonNull final Activity activity,
                                      @NonNull final String searchString) {

        final ArrayList<Product> products = new ArrayList<Product>();
        final ProductsAdapter productsAdapter = new ProductsAdapter(activity, R.layout.list_item_products, products);

        pfeAPI.searchFromQuery(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Product>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SearchFromQuery : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(List<Product> products) {
                        Log.e(TAG, "SearchFromQuery : onNext");

                        Collections.sort(products, new Comparator<Product>() {
                            public int compare(Product one, Product other) {
                                return one.getProductName().compareTo(other.getProductName());
                            }
                        });

                        Singleton.getInstance().setProductList(products);
                        productsAdapter.addAll(products);
                        ListView listView = activity.findViewById(R.id.list_view_products);
                        listView.setAdapter(productsAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "SearchFromQuery : onError " + e.toString());

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);

                        listView.setAdapter(productsAdapter);
                        listView.setEmptyView(emptyTextView);

                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null)
                            emptyTextView.setText(activity.getString(R.string.error_occured));
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "SearchFromQuery : onComplete");

                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null) {
                            if (products.size() == 0)
                                emptyTextView.setText(activity.getString(R.string.no_corresponding_product));
                            else emptyTextView.setText("");
                        }
                    }
                });
    }



    public static void getPlaceDetails(@NonNull final Activity activity,
                                       @NonNull final String salesPointId) {

        pfeAPI.getPlaceDetails(salesPointId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SalesPoint>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "GetPlaceDetails : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(SalesPoint salesPoint) {
                        Log.e(TAG, "GetPlaceDetails : onNext");
                        Log.e(TAG, "Content : " + salesPoint.getSalesPointPhotoReference());

                        if (activity instanceof BottomSheetDataSetter) {
                            BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                            bottomSheetDataSetter.setBottomSheetDataDetails(salesPoint);
                            bottomSheetDataSetter.setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "GetPlaceDetails : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "GetPlaceDetails : onComplete");
                    }
                });
    }



    /**
     * Searches for the Products corresponding to the specified Category, displays the results
     * if there are any, or an appropriate message in the case where there are no corresponding
     * results or an error occurred.
     *
     * @param activity The activity in which to display the results of the network call, or the
     *                 appropriate message.
     *                 //@param layoutResourceId The layout id used by{@link ProductsAdapter} the Adapter to display the results.
     * @param category The Category id used for the network call to filter the Products.
     */

    public static void searchCategory(@NonNull final Activity activity,
                                      final int category) {

        final ArrayList<Product> products = new ArrayList<Product>();
        final ProductsAdapter productsAdapter = new ProductsAdapter(activity, R.layout.list_item_products, products);

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
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Search Category : onSubscribe");
                        DisposableManager.add(d);

                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        if (progressBar != null)
                            progressBar.setVisibility(View.VISIBLE);
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

                        Collections.sort(list, new Comparator<Product>() {
                            public int compare(Product one, Product other) {
                                return one.getProductName().compareTo(other.getProductName());
                            }
                        });

                        productsAdapter.addAll(list);
                        Singleton.getInstance().setProductList(list);

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        if (listView != null) listView.setAdapter(productsAdapter);
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

                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null)
                            emptyTextView.setText(activity.getString(R.string.error_occured));
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
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null) {
                            if (products.size() == 0)
                                emptyTextView.setText(activity.getString(R.string.no_corresponding_product));
                            else emptyTextView.setText("");
                        }
                    }
                });
    }

    public static void connect(@NonNull final Activity activity,
                               @NonNull final String mailAddress,
                               @NonNull final String password) {

        pfeAPI.connect(mailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Connect : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(Boolean exists) {
                        Log.e(TAG, "Connect : onNext");

                        if (exists) {
                            SharedPreferences.Editor editor =
                                    activity.getSharedPreferences(Utils.SHARED_PREFERENCES_USER,MODE_PRIVATE).edit();
                            editor.putString("email", mailAddress);
                            editor.putString("password", password);
                            editor.apply();

                            Intent intent = new Intent(activity, MapsActivity.class);
                            activity.startActivity(intent);

                            PfeAPI.getInstance().setCredentials(mailAddress, password);
                        } else {
                            Toast.makeText(activity, "WRONG", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Connect : onError " + e.toString());
                        Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Connect : onComplete");
                    }
                });
    }


    public static void register(@NonNull final Activity activity,
                                @NonNull final String mailAddress,
                                @NonNull final String password) {

        pfeAPI.register(mailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Register : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(Boolean registered) {
                        Log.e(TAG, "Register : onNext");

                        if (registered) {
                            SharedPreferences.Editor editor =
                                    activity.getSharedPreferences(Utils.SHARED_PREFERENCES_USER,MODE_PRIVATE).edit();
                            editor.putString("email", mailAddress);
                            editor.putString("password", password);
                            editor.apply();

                            Intent intent = new Intent(activity, MapsActivity.class);
                            activity.startActivity(intent);
                        } else {
                            //TODO:Error message for mail address
                            Toast.makeText(activity, "WRONG", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO:Snackbar for retry
                        Log.e(TAG, "Register : onError " + e.toString());
                        Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Register : onComplete");
                    }
                });
    }
}
