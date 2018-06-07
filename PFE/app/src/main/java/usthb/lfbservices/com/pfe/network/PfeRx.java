package usthb.lfbservices.com.pfe.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.DescSalesPointActivity;
import usthb.lfbservices.com.pfe.adapters.SalesPointsProductAdapter;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.itinerary.place.AddressComponents;
import usthb.lfbservices.com.pfe.itinerary.place.GooglePlaceDetails;
import usthb.lfbservices.com.pfe.itinerary.place.ResultDetails;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.activities.MainActivity;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.fragments.FragmentMap;
import usthb.lfbservices.com.pfe.models.BottomSheetDataSetter;
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.ProductCharacteristic;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.Result;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

/**
 * A class exposing static methods to interact with Retrofit that handles the network calls
 * and the associated handling.
 */

public class PfeRx {
    private static final String TAG = PfeRx.class.getName();

    /**
     * The object used for the network calls.
     */
    private static PfeAPI pfeAPI = PfeAPI.getInstance();

    /**
     * Operates a network call to get the {@link SalesPoint} and {@link ProductSalesPoint} corresponding
     * to the selected product.
     * @param activity The current {@link Activity}.
     * @param productBarcode A {@link Product#productBarcode}.
     */
    public static void searchFromProductBarcode(@NonNull final Activity activity,
                                                @NonNull final String productBarcode) {
        final ProgressDialog progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(activity.getResources().getString(R.string.server_connexion));
        progressDialog.show();

        pfeAPI.searchFromProductBarcode(productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SearchFromProductBarcode : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.retrieving_data));
                    }

                    /**
                     * Adds to the map markers representing the sales points of the selected product
                     * and initializes the marker's on click listener and the showButton's on click
                     * listener.
                     */
                    @Override
                    public void onNext(Result result) {
                        Log.e(TAG, "SearchFromproductBarcode : onNext");
                        Log.e(TAG, "ProductSalesPoints : " + result.getProductSalesPoints());
                        Log.e(TAG, "SalesPoints : " + result.getSalesPoints());

                        final List<SalesPoint> salesPoints = result.getSalesPoints();
                        final List<ProductSalesPoint> productSalesPoints = result.getProductSalesPoints();
                        final Button showList = activity.findViewById(R.id.show_list_button);
                        final Map<LatLng, ProductSalesPoint> hashMap = new HashMap<>();

                        Collections.sort(salesPoints, new Comparator<SalesPoint>() {
                            public int compare(SalesPoint one, SalesPoint other) {
                                return one.getSalesPointName().compareTo(other.getSalesPointName());
                            }
                        });

                        for (SalesPoint salesPoint : salesPoints) PfeRx.getPlaceDetails(activity, salesPoint.getSalesPointId());

                        Singleton.getInstance().setSalesPointList(salesPoints);
                        Singleton.getInstance().setProductSalesPointList(productSalesPoints);

                        final View geolocation = activity.findViewById(R.id.geolocalisation);
                        if (geolocation != null) geolocation.setVisibility(View.VISIBLE);

                        final SalesPointsProductAdapter salesPointsAdapter1 = new SalesPointsProductAdapter(activity, R.layout.list_item_salespoint_product, (ArrayList<ProductSalesPoint>) productSalesPoints);
                        final ListView listSalesPoints = activity.findViewById(R.id.list_view_salespoint_product);
                        if (listSalesPoints != null) listSalesPoints.setAdapter(salesPointsAdapter1);

                        final GoogleMap map = Singleton.getInstance().getMap();

                        if (map != null) {
                            /**
                             * Clears the map and extracts from the {@link SharedPreferences}, if
                             * possible, the last known position of the user.
                             */
                            map.clear();
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            SharedPreferences preferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES_POSITION, Context.MODE_PRIVATE);
                            String sUserLatitude = preferences.getString(Constants.SHARED_PREFERENCES_POSITION_LATITUDE, null);
                            String sUserLongitude = preferences.getString(Constants.SHARED_PREFERENCES_POSITION_LONGITUDE, null);
                            LatLng userPosition = null;

                            if (sUserLatitude != null && sUserLongitude != null) {
                                try {
                                    userPosition = new LatLng(Double.parseDouble(sUserLatitude), Double.parseDouble(sUserLongitude));
                                } catch (Exception e) {
                                    Log.e(TAG, "Exception creating user position : " + e);
                                }
                            }

                            /**
                             * Adds a marker for the user's position.
                             */
                            if (activity instanceof FragmentMap.MapActions) {
                                FragmentMap fragmentMap = ((FragmentMap.MapActions)activity).getActivityFragmentMap();
                                if (fragmentMap != null) {
                                    fragmentMap.resetUserMarker();
                                }
                            }

                            /**
                             * Adds the resulting {@link SalesPoint} to the map.
                              */
                            ProductSalesPoint productSalesPointTemps = new ProductSalesPoint();
                            int nbMarkers = 0;

                            for (SalesPoint salesPoint : salesPoints) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(salesPoint.getSalesPointLat(), salesPoint.getSalesPointLong()))
                                        .title(salesPoint.getSalesPointName())
                                        .snippet(salesPoint.getSalesPointAddress());

                                for (ProductSalesPoint productSalesPoint : productSalesPoints) {
                                    if (productSalesPoint.getSalesPointId().equals(salesPoint.getSalesPointId())) {
                                        productSalesPointTemps = productSalesPoint;
                                        if (productSalesPoint.getProductQuantity() > 0) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
                                        } else {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));
                                        }
                                    }
                                }
                                Marker marker = map.addMarker(markerOptions);
                                hashMap.put(marker.getPosition(), productSalesPointTemps);
                                nbMarkers++;
                            }
                            /**
                             * Determines the appropriate level of zoom.
                             */
                            if (nbMarkers > 0) {
                                if (nbMarkers == 1) {
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom((LatLng)hashMap.keySet().toArray()[0], FragmentMap.ZOOM_LEVEL);
                                    map.animateCamera(cu);
                                } else {
                                    Iterator<LatLng> positions = hashMap.keySet().iterator();

                                    List<LatLng> latLngList = Utils.getClosestPositions(positions, userPosition ,3);

                                    for (LatLng latLng : latLngList) {
                                        builder.include(latLng);
                                    }

                                    LatLngBounds bounds = builder.build();
                                    // offset from the edges of the map in pixels
                                    int padding = 200;
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    map.animateCamera(cu);
                                }
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.no_corresponding_sales_point), Toast.LENGTH_LONG).show();
                            }

                            /**
                             * Initializes the map so that when a marker is clicked, if it represents
                             * a {@link SalesPoint}, the camera will move to center it and a bottom
                             * sheet with the {@link ProductSalesPoint} informations will appear,
                             * it will also offer the user the possibility to add this result to
                             * the favorite or to subscribe to its notifications feed.
                             */
                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                    marker.showInfoWindow();
                                    if (activity instanceof BottomSheetDataSetter) {
                                        for (SalesPoint salesPoint : salesPoints) {
                                            if (salesPoint.getSalesPointLatLng().equals(marker.getPosition())) {
                                                for (ProductSalesPoint productSalesPoint : productSalesPoints) {
                                                    if (productSalesPoint.getSalesPointId().equals(salesPoint.getSalesPointId())) {
                                                        BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                                                        bottomSheetDataSetter.setBottomSheetData(salesPoint, productSalesPoint);
                                                        bottomSheetDataSetter.setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                                                        PfeRx.getPlaceDetails(activity, salesPoint.getSalesPointId());
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    return true;
                                }
                            });
                        }

                        /**
                         * Initializes the showList button so that when clicked, a list of {@link ProductSalesPoint}
                         * will be showed to the user, and when an element of that list is selected,
                         * the list will disappear and the user will be gided to the corresponding
                         * sales point.
                         */
                        if (showList != null) {
                            showList.setVisibility(View.VISIBLE);
                            showList.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    final ListView listView = activity.findViewById(R.id.list_view_salespoint_product);
                                    if (listView != null) {
                                        if (listView.getVisibility() == View.VISIBLE) {
                                            showList.setText(activity.getString(R.string.sales_points_show_list));
                                            listView.setVisibility(View.GONE);
                                        }
                                        else {
                                            showList.setText(activity.getString(R.string.reduce));
                                            final SalesPointsProductAdapter productSalesPointListAdapter = new SalesPointsProductAdapter (activity, R.layout.list_item_salespoint_product, (ArrayList<ProductSalesPoint>)productSalesPoints);
                                            listView.setAdapter(productSalesPointListAdapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                    if (activity instanceof BottomSheetDataSetter) {
                                                        listView.setVisibility(View.GONE);
                                                        showList.setText(activity.getString(R.string.sales_points_show_list));
                                                        ProductSalesPoint productSalesPointPosition = ((ProductSalesPoint)(adapterView.getItemAtPosition(position)));

                                                        for( SalesPoint salesPointSingleton : Singleton.getInstance().getSalesPointList())
                                                        {
                                                            if (salesPointSingleton.getSalesPointId().equals(productSalesPointPosition.getSalesPointId())) {
                                                                BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter) activity;
                                                                bottomSheetDataSetter.setBottomSheetData(salesPointSingleton, productSalesPointPosition);
                                                                bottomSheetDataSetter.setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                                                                PfeRx.getPlaceDetails(activity, salesPointSingleton.getSalesPointId());
                                                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(salesPointSingleton.getSalesPointLatLng(), FragmentMap.ZOOM_LEVEL);
                                                                if (map != null)
                                                                    map.animateCamera(cu);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            listView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            });
                        }
                    }

                    /**
                     * Dismisses the progress dialog, logs the error and shows the user a {@link Toast}
                     * message to tell him that an error occurred.
                     */
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "SearchFromProductBarcode : onError " + e.toString());
                        Toast.makeText(activity, activity.getResources().getString(R.string.an_error_occured), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "SearchFromProductBarcode : onComplete");
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Operates a network call to get the products corresponding to the search query.
     * @param activity The current {@link Activity}.
     * @param searchString A search query.
     */
    public static void searchFromQuery(@NonNull final Activity activity,
                                       @NonNull final String searchString) {

        final ArrayList<Product> products = new ArrayList<>();
        final ProductsAdapter productsAdapter = new ProductsAdapter(activity, R.layout.list_item_products, products);

        pfeAPI.searchFromQuery(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Product>>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SearchFromQuery : onSubscribe");
                        DisposableManager.add(d);
                    }

                    /**
                     * Adds the {@link List} of {@link Product} resulting from this network call
                     * to the {@link Singleton} and displays it in the {@link usthb.lfbservices.com.pfe.fragments.ProductsFragment}.
                     */
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
                        if (listView != null) listView.setAdapter(productsAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "SearchFromQuery : onError " + e.toString());

                        ListView listView = activity.findViewById(R.id.list_view_products);
                        View progressBar = activity.findViewById(R.id.products_progress_bar);
                        TextView emptyTextView = activity.findViewById(R.id.empty_list_products);

                        if (listView != null) {
                            listView.setAdapter(productsAdapter);
                            listView.setEmptyView(emptyTextView);
                        }

                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null)
                            emptyTextView.setText(activity.getString(R.string.an_error_occured));
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

    /**
     * Operates a network call to get the detailed informations of a {@link SalesPoint}.
     * @param activity The current {@link Activity}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     */
    public static void getPlaceDetails(@NonNull final Activity activity,
                                       @NonNull final String salesPointId) {

        final String apiKey = activity.getResources().getString(R.string.google_maps_key);
        pfeAPI.getPlaceDetails(apiKey, salesPointId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GooglePlaceDetails>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "GetPlaceDetails : onSubscribe");
                        DisposableManager.add(d);
                    }

                    /**
                     * Extracts the useful sales point's informations and adds them to the correct
                     * instance of {@link SalesPoint} of the {@link Singleton} and if the calling
                     * {@link Activity} is an instance of {@link BottomSheetDataSetter} then calls
                     * the {@link BottomSheetDataSetter#setBottomSheetDataDetails(SalesPoint)} method
                     * and if it is an instance of {@link DescSalesPointActivity} calls the
                     * {@link DescSalesPointActivity#initViews(SalesPoint, boolean)} method.
                     */
                    @Override
                    public void onNext(GooglePlaceDetails googlePlaceDetails) {
                        Log.e(TAG, "GetPlaceDetails : onNext");

                        final List<SalesPoint> salesPoints = Singleton.getInstance().getSalesPointList();
                        final ResultDetails resultDetails = googlePlaceDetails.getResult();
                        final SalesPoint result = new SalesPoint();

                        if (resultDetails == null) {
                            Log.e(TAG, "Result null");
                            return;
                        }
                        if (resultDetails.getPlaceId() == null || resultDetails.getPlaceId().trim().equals("")) {
                            if (activity instanceof BottomSheetDataSetter) {
                                BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                                bottomSheetDataSetter.setBottomSheetDataDetails(result);
                            } else if (activity instanceof DescSalesPointActivity) {
                                ((DescSalesPointActivity) activity).initViews(result, false);
                            }
                        }

                        result.setSalesPointId(resultDetails.getPlaceId());
                        result.setSalesPointName(resultDetails.getName());
                        result.setSalesPointAddress(resultDetails.getFormattedAddress());
                        result.setSalesPointLat(resultDetails.getGeometry().getLocation().getLat());
                        result.setSalesPointLong(resultDetails.getGeometry().getLocation().getLng());
                        String phoneNumber = resultDetails.getInternationalPhoneNumber();
                        if (phoneNumber == null || phoneNumber.trim().equalsIgnoreCase("")) {
                            phoneNumber = resultDetails.getInternationalPhoneNumber();
                            if (phoneNumber == null || phoneNumber.trim().equalsIgnoreCase("")) {
                                phoneNumber = activity.getResources().getString(R.string.not_available);
                            }
                        }
                        result.setSalesPointPhoneNumber(phoneNumber);
                        result.setSalesPointPhotoReference(resultDetails.getPhotos()[0].getPhotoReference());
                        result.setSalesPointRating(resultDetails.getRating());
                        String website = resultDetails.getWebsite();
                        if (website == null || website.trim().equalsIgnoreCase(""))
                            website = activity.getResources().getString(R.string.not_available);
                        result.setSalesPointWebSite(website);

                        Log.e(TAG, "BEFORE CITY");
                        for (AddressComponents addressComponents : resultDetails.getAddressComponents()) {
                            for (String type : addressComponents.getTypes()) {
                                if (type.contains("locality")) {
                                    Log.e(TAG, "CITY : " + addressComponents.getLongName());
                                    AppRoomDatabase db = AppRoomDatabase.getInstance(activity);
                                    result.setSalesPointCity(db.cityDao().getCityIdByName(addressComponents.getLongName()));
                                    Log.e(TAG, "CITY ID : " + db.cityDao().getCityIdByName(addressComponents.getLongName()));
                                }
                            }
                        }
                        Log.e(TAG, "After city");

                        final int size = salesPoints.size();
                        int correspondingSalesPoint = -1;
                        for (int i = 0; i < size; i++) {
                            final SalesPoint salesPoint = salesPoints.get(i);
                            if (salesPoint.getSalesPointId().equals(result.getSalesPointId())) {
                                correspondingSalesPoint = i;
                                salesPoints.remove(i);
                                salesPoints.add(i, result);
                                break;
                            }
                        }

                        if ((correspondingSalesPoint != -1) && (activity instanceof BottomSheetDataSetter)) {
                            BottomSheetDataSetter bottomSheetDataSetter = (BottomSheetDataSetter)activity;
                            bottomSheetDataSetter.setBottomSheetDataDetails(result);
                        } else if (activity instanceof DescSalesPointActivity) {
                            ((DescSalesPointActivity) activity).initViews(result, true);
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
     * @param activity The activity in which to display the results of the network call, or the
     *                 appropriate message.
     * @param category The Category id used for the network call to filter the Products.
     */

    public static void searchCategory(@NonNull final Activity activity, final int category) {

        final ArrayList<Product> products = new ArrayList<>();
        final ProductsAdapter productsAdapter = new ProductsAdapter(activity, R.layout.list_item_products, products);

        pfeAPI.searchCategory(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Product>>() {
                    /**
                     * This method is called before we start the network call.
                     * Activates the PorgressBar to show the user that there is a background
                     * activity and adds the {@link Disposable} of this network call to the {@link DisposableManager}.
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

                        if (listView != null) {
                            listView.setAdapter(productsAdapter);
                            listView.setEmptyView(emptyTextView);
                        }
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
                        if (emptyTextView != null)
                            emptyTextView.setText(activity.getString(R.string.an_error_occured));
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

    /**
     * Operates a network call to check if the user is registered and shows an indeterminate progress
     * bar.
     * @param activity The current {@link Activity}.
     * @param mailAddress The user's mail address.
     * @param password The user's password.
     */
    public static void connect(@NonNull final Activity activity,
                               @NonNull final String mailAddress,
                               @NonNull final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(activity.getResources().getString(R.string.server_connexion));
        progressDialog.show();

        pfeAPI.connect(mailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Connect : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.checking_informations));
                    }

                    /**
                     * If the user exists adds him mail address and password to the {@link SharedPreferences}
                     * and redirect him to the {@link MainActivity}.
                     */
                    @Override
                    public void onNext(Boolean exists) {
                        Log.e(TAG, "Connect : onNext");

                        if (exists) {
                            SharedPreferences.Editor editor =
                                    activity.getSharedPreferences(Constants.SHARED_PREFERENCES_USER,  Context.MODE_PRIVATE).edit();
                            editor.putString(Constants.SHARED_PREFERENCES_USER_EMAIL, mailAddress);
                            editor.putString(Constants.SHARED_PREFERENCES_USER_PASSWORD, password);
                            editor.apply();

                            pfeAPI.setAuthorization(mailAddress, password);
                            final String deviceId = Utils.getStoredFirebaseTokenId(activity);
                            PfeRx.setFirebaseTokenId(deviceId);

                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.invalid_mail_password), Toast.LENGTH_LONG).show();
                        }
                    }

                    /**
                     * Dismisses the progress bar, logs the error and shows a {@link Snackbar} to
                     * tell the user that an error occurred and give him the possibility to retry.
                     */
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Connect : onError " + e.toString());
                        progressDialog.dismiss();
                        Snackbar.make(activity.findViewById(R.id.login_layout),activity.getResources().getString(R.string.an_error_occured), Snackbar.LENGTH_LONG)
                                .setAction(activity.getResources().getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PfeRx.connect(activity, mailAddress, password);
                                    }
                                })
                                .setActionTextColor(activity.getResources().getColor(R.color.colorPrimary))
                                .show();
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Connect : onComplete");
                        progressDialog.dismiss();
                    }
                });
    }


    /**
     * Operates a network call to register the user and show an indeterminate progress bar.
     * @param activity The current {@link Activity}.
     * @param mailAddress The user's mail address
     * @param password The user's password.
     */
    public static void register(@NonNull final Activity activity,
                                @NonNull final String mailAddress,
                                @NonNull final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(activity.getResources().getString(R.string.server_connexion));
        progressDialog.show();

        pfeAPI.register(mailAddress, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Register : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.registering_informations));
                    }

                    /**
                     * If the user was successfully registered, adds his mail address and passwords
                     * to the {@link SharedPreferences} and redirects him to the {@link MainActivity}.
                     */
                    @Override
                    public void onNext(Boolean registered) {
                        Log.e(TAG, "Register : onNext");

                        if (registered) {
                            SharedPreferences.Editor editor =
                                    activity.getSharedPreferences(Constants.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE).edit();
                            editor.putString(Constants.SHARED_PREFERENCES_USER_EMAIL, mailAddress);
                            editor.putString(Constants.SHARED_PREFERENCES_USER_PASSWORD, password);
                            editor.apply();

                            pfeAPI.setAuthorization(mailAddress, password);
                            final String deviceId = Utils.getStoredFirebaseTokenId(activity);
                            PfeRx.setFirebaseTokenId(deviceId);

                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.mail_address_used), Toast.LENGTH_LONG).show();
                        }
                    }

                    /**
                     * Logs the error message, dismisses the progress bar and shows a {@link Snackbar}
                     * to tell the user that the network call failed and offers him to retry.
                     */
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Register : onError " + e.toString());
                        progressDialog.dismiss();
                        Snackbar.make(activity.findViewById(R.id.login_layout),activity.getResources().getString(R.string.an_error_occured), Snackbar.LENGTH_LONG)
                                .setAction(activity.getResources().getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PfeRx.register(activity, mailAddress, password);
                                    }
                                })
                                .setActionTextColor(activity.getResources().getColor(R.color.colorPrimary))
                                .show();
                    }

                    /**
                     * Dismisses the progress bar.
                     */
                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Register : onComplete");
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Operates a network call to add this device's Firebase token id in the system's database to
     * the devices associated with the currently logged user.
     * @param deviceId The device's Firebase token id.
     */
    private static void setFirebaseTokenId(@NonNull final String deviceId) {
        pfeAPI.setFirebaseTokenId(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SetFirebaseTokenId : onSubscribe");
                    }

                    @Override
                    public void onNext(Boolean added) {
                        Log.e(TAG, "SetFirebaseTokenId : onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "SetFirebaseTokenId : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "SetFirebaseTokenId : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to update the value of this device's Firebase token id in the
     * system's database.
     * @param previousDeviceId The previous value of this device's Firebase token id.
     * @param newDeviceId The new value of this device's Firebase token id.
     */
    public static void updateFirebaseTokenId(@NonNull final String previousDeviceId,
                                             @NonNull final String newDeviceId) {
        pfeAPI.updateFirebaseTokenId(previousDeviceId, newDeviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "updateFirebaseTokenId : onSubscribe");
                    }

                    @Override
                    public void onNext(Boolean updated) {
                        Log.e(TAG, "updateFirebaseTokenId : onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "updateFirebaseTokenId : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "updateFirebaseTokenId : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to remove this device's Firebase token id from the system's database.
     * @param deviceId The device's Firebase token id.
     */
    public static void removeFirebaseTokenId(@NonNull final String deviceId) {
        pfeAPI.removeFirebaseTokenId(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "removeFirebaseTokenId : onSubscribe");
                    }

                    @Override
                    public void onNext(Boolean removed) {
                        Log.e(TAG, "removeFirebaseTokenId : onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "removeFirebaseTokenId : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "removeFirebaseTokenId : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to add this user to the notifications feed of a given {@link Product} and {@link SalesPoint}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     * @param productBarcode A {@link Product#productBarcode}.
     */
    public static void addToNotificationsList(@NonNull final String salesPointId,
                                              @NonNull final String productBarcode) {
        pfeAPI.addToNotificationsList(salesPointId, productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "addToNotificationsList : onSubscribe");
                        DisposableManager.add(d);
                    }

                    @Override
                    public void onNext(Boolean added) {
                        Log.e(TAG, "addToNotificationsList : onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "addToNotificationsList : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "addToNotificationsList : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to remove this user from the notifications feed of a given {@link Product} and {@link SalesPoint}.
     * @param salesPointId A {@link SalesPoint#salesPointId}.
     * @param productBarcode A {@link Product#productBarcode}.
     */
    public static void removeFromNotificationsList(@NonNull final String salesPointId,
                                                   @NonNull final String productBarcode) {
        pfeAPI.removeFromNotificationsList(salesPointId, productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "removeFromNotificationsList : onSubscribe");
                    }

                    @Override
                    public void onNext(Boolean added) {
                        Log.e(TAG, "removeFromNotificationsList : onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "removeFromNotificationsList : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "removeFromNotificationsList : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to get the detailed informations of a {@link Product}.
     * @param activity The current {@link Activity}.
     * @param productSalesPoint A {@link ProductSalesPoint}.
     */
    public static void getProductDetails(@NonNull final Activity activity,
                                         @NonNull final ProductSalesPoint productSalesPoint) {

        pfeAPI.getProductDetails(productSalesPoint.getProductBarcode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Product>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "GetProductDetails : onSubscribe");
                        DisposableManager.add(d);
                    }

                    /**
                     * Checks whether or not the {@link Product} resulting from this network call
                     * exists in the database, if it doesn't, it will insert it. After that it will
                     * make a second network call to get this product's characteristics.
                     */
                    @Override
                    public void onNext(Product product) {
                        Log.e(TAG, "GetProductDetails : onNext : " + product);

                        AppRoomDatabase db = AppRoomDatabase.getInstance(activity);
                        if (!db.productDao().productExists(product.getProductBarcode())) db.productDao().insert(product);
                        db.productSalesPointDao().insertAll(productSalesPoint);
                        PfeRx.getProductCharacteristics(activity, product, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "GetProductDetails : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "GetProductDetails : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to get the characteristics of a {@link Product}.
     * @param activity The current {@link Activity}.
     * @param product A{@link Product}.
     * @param addToDatabase A boolean to know whether or not it will insert the results in the database.
     */
    public static void getProductCharacteristics(@NonNull final Activity activity,
                                                 @NonNull final Product product,
                                                 final boolean addToDatabase) {

        pfeAPI.getProductCharacteristics(product.getProductBarcode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<KeyValue>>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getProductCharacteristics : onSubscribe");
                        DisposableManager.add(d);
                    }

                    /**
                     * Creates a {@link List} of {@link ProductCharacteristic} from the {@link List}
                     * of {@link KeyValue} resulting from this network call and tests the value of the
                     * boolean addToDatabase passed in parameter to this method, if it is set to true
                     * then it will add the list of characteristics to the database, and if it is false
                     * then if the calling activity is an instance of {@link DescProductFragment.FragmentDescriptionProductActions}
                     * then it will call the {@link DescProductFragment.FragmentDescriptionProductActions#displayProductCharacteristics(Product, List)}
                     * method to display the results.
                     */
                    @Override
                    public void onNext(List<KeyValue> listProductsCharacteristics) {
                        Log.e(TAG, "getProductCharacteristics : onNext");

                        final AppRoomDatabase db = AppRoomDatabase.getInstance(activity);
                        final List<ProductCharacteristic> productCharacteristics = new ArrayList<>();
                        String productCharacteristicValue;
                        int typeCharacteristicId;

                        for (KeyValue keyValue : listProductsCharacteristics) {
                            productCharacteristicValue = keyValue.getProductCharacteristicValue();
                            typeCharacteristicId = keyValue.getTypeCharacteristicId();

                            Log.e("TypeChar : ", "" + typeCharacteristicId);
                            Log.e("Characteristic : ", productCharacteristicValue);
                            productCharacteristics.add(new ProductCharacteristic(typeCharacteristicId, product.getProductBarcode(), productCharacteristicValue));
                        }
                        if (addToDatabase) {
                            Log.e(TAG, "Add to database");
                            db.productCharacteristicDao().insertAll(productCharacteristics);
                        } else {
                            Log.e(TAG, "Testing activity");
                            if (activity instanceof DescProductFragment.FragmentDescriptionProductActions) {
                                ((DescProductFragment.FragmentDescriptionProductActions)activity).displayProductCharacteristics(product, listProductsCharacteristics);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getProductCharacteristics : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "getProductCharacteristics : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to get the appropriate search propositions.
     * @param activity The current {@link Activity}.
     * @param query The current search query.
     * @param cursorAdapter The {@link CursorAdapter} of the current {@link android.support.v7.widget.SearchView}.
     */
    public static void getSearchPropositions(@NonNull final Activity activity,
                                             @NonNull final String query,
                                             @NonNull final CursorAdapter cursorAdapter) {
        pfeAPI.getSearchPropositions(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {

                    /**
                     * Disposes of the previous instance of this network call if it exists and
                     * adds the {@link Disposable} of this network call to the {@link Singleton}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getSearchPropositions : onSubscribe");
                        Disposable old = Singleton.getInstance().getSearchPropositionDisposable();
                        if (old != null) old.dispose();
                        Singleton.getInstance().setSearchPropositionDisposable(d);
                    }

                    /**
                     * Create a {@link MatrixCursor} from the {@link List} of {@link String} resulting
                     * from this network call and representing the autocomplete propositions and updates
                     * the adapter of the {@link android.support.v7.widget.SearchView} with the new cursor.
                     * @param propositions A {@link List} of {@link String} representing the autocomplete
                     *                     propositions.
                     */
                    @Override
                    public void onNext(List<String> propositions) {
                        Log.e(TAG, "getSearchPropositions : onNext");
                        String[] columns = new String[] {"_id", "proposition"};

                        MatrixCursor matrixCursor= new MatrixCursor(columns);
                        activity.startManagingCursor(matrixCursor);
                        for (int i = 0; i < propositions.size(); i++) {
                            matrixCursor.addRow(new Object[] {i, propositions.get(i)});
                        }
                        cursorAdapter.changeCursor(matrixCursor);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getSearchPropositions : onError " + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "getSearchPropositions : onComplete");
                    }
                });
    }

    /**
     * Operates a network call to update the informations of the {@link ProductSalesPoint} of a {@link Product}
     * stored in the database.
     * @param activity The current {@link Activity}.
     * @param salesPointsIds A {@link List} of {@link SalesPoint#salesPointId}.
     * @param productBarcode A {@link Product#productBarcode}.
     */
    public static void getNewestProductSalesPointsInformations(@NonNull final Activity activity,
                                                               @NonNull final List<String> salesPointsIds,
                                                               @NonNull final String productBarcode) {
        pfeAPI.getNewestInformations(salesPointsIds, productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductSalesPoint>>() {

                    /**
                     * Adds the {@link Disposable} of this network call to the {@link DisposableManager}.
                     * @param d The {@link Disposable} corresponding to this network call.
                     */
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getNewestProductSalesPointsInformations : onSubscribe");
                        DisposableManager.add(d);
                    }

                    /**
                     * Updates the database with the {@link List} of {@link ProductSalesPoint}
                     * resulting from this network call to the database.
                     * @param productSalesPoints A {@link List} of {@link ProductSalesPoint}.
                     */
                    @Override
                    public void onNext(List<ProductSalesPoint> productSalesPoints) {
                        Log.e(TAG, "getNewestProductSalesPointsInformations : onNext");

                        final AppRoomDatabase db = AppRoomDatabase.getInstance(activity);

                        for (ProductSalesPoint productSalesPoint : productSalesPoints) {
                            db.productSalesPointDao().update(productSalesPoint);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getNewestProductSalesPointsInformations : onError : " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "getNewestProductSalesPointsInformations : onComplete");
                    }
                });
    }
}