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
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "SearchFromProductBarcode : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.retrieving_data));
                    }

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

                            if (activity instanceof FragmentMap.MapActions) {
                                FragmentMap fragmentMap = ((FragmentMap.MapActions)activity).getActivityFragmentMap();
                                if (fragmentMap != null) {
                                    fragmentMap.resetUserMarker();
                                }
                            }

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


    //TODO: Faire en sorte d'afficher par pages ==> en plus de la requête de base, une autre requête
    //qui ne sera exécutée qu'une seule fois qui calculera le nombre total de résultats possible
    //afin de déterminer le nombre de pages de résultats, faire pareil avec catégories
    public static void searchFromQuery(@NonNull final Activity activity,
                                      @NonNull final String searchString) {

        final ArrayList<Product> products = new ArrayList<>();
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



    public static void getPlaceDetails(@NonNull final Activity activity,
                                       @NonNull final String salesPointId/*,boolean fromRx*/) {

        final String apiKey = activity.getResources().getString(R.string.google_maps_key);
        pfeAPI.getPlaceDetails(apiKey, salesPointId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GooglePlaceDetails>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "GetPlaceDetails : onSubscribe");
                        DisposableManager.add(d);
                    }

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
     *
     * @param activity The activity in which to display the results of the network call, or the
     *                 appropriate message.
     *                 //@param layoutResourceId The layout id used by{@link ProductsAdapter} the Adapter to display the results.
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
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Connect : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.checking_informations));
                    }

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
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "Register : onSubscribe");
                        DisposableManager.add(d);
                        progressDialog.setMessage(activity.getResources().getString(R.string.registering_informations));
                    }

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
                            //TODO:Error message for mail address
                            Toast.makeText(activity, activity.getResources().getString(R.string.mail_address_used), Toast.LENGTH_LONG).show();
                        }
                    }

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

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "Register : onComplete");
                        progressDialog.dismiss();
                    }
                });
    }


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


    public static void addToNotificationsList(@NonNull final String salesPointId,
                                              @NonNull final String productBarcode) {
        pfeAPI.addToNotificationsList(salesPointId, productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "addToNotificationsList : onSubscribe");
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


    public static void removeFromNotificationsList(@NonNull final String salesPointId,
                                                   @NonNull final String productBarcode) {
        pfeAPI.removeFromNotificationsList(salesPointId, productBarcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
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


    public static void getProductDetails(@NonNull final Activity activity,
                                         @NonNull final ProductSalesPoint productSalesPoint) {

        pfeAPI.getProductDetails(productSalesPoint.getProductBarcode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Product>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "GetProductDetails : onSubscribe");
                        DisposableManager.add(d);
                    }

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


    public static void getProductCharacteristics(@NonNull final Activity activity,
                                                 @NonNull final Product product,
                                                 final boolean addToDatabase) {

        Log.e(TAG, "AddToDb : " + addToDatabase);
        Log.e(TAG, "ProductBarcode : " + product.getProductBarcode());
        pfeAPI.getProductCharacteristics(product.getProductBarcode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<KeyValue>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getProductCharacteristics : onSubscribe");
                        DisposableManager.add(d);
                    }

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


    public static void getSearchPropositions(@NonNull final Activity activity,
                                             @NonNull final String query,
                                             @NonNull final CursorAdapter cursorAdapter) {
        pfeAPI.getSearchPropositions(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getSearchPropositions : onSubscribe");
                        Disposable old = Singleton.getInstance().getSearchPropositionDisposable();
                        if (old != null) old.dispose();
                        Singleton.getInstance().setSearchPropositionDisposable(d);
                    }

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



    public static void getNewestProductSalesPointsInformations(@NonNull final Activity activity,
                                                               @NonNull final List<String> salesPointsIds) {
        pfeAPI.getNewestInformations(salesPointsIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductSalesPoint>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "getNewestProductSalesPointsInformations : onSubscribe");
                        DisposableManager.add(d);
                    }

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
