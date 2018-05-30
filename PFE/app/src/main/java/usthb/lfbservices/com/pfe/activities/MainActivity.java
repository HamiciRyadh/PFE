package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.SuggestinAdapter;
import usthb.lfbservices.com.pfe.fragments.FragmentBarcodeScanner;
import usthb.lfbservices.com.pfe.fragments.FragmentFavorite;
import usthb.lfbservices.com.pfe.fragments.FragmentMap;
import usthb.lfbservices.com.pfe.fragments.FragmentNotifications;
import usthb.lfbservices.com.pfe.fragments.FragmentParameters;
import usthb.lfbservices.com.pfe.fragments.ProductsFragment;
import usthb.lfbservices.com.pfe.fragments.SearchFragment;
import usthb.lfbservices.com.pfe.models.BottomSheetDataSetter;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentMap.MapActions, FragmentFavorite.FavoriteActions,
        FragmentNotifications.NotificationsActions, FragmentParameters.ParametersActions,
        SearchFragment.SearchFragmentActions, ProductsFragment.ProductsFragmentActions,
        FragmentBarcodeScanner.BarcodeScannerActions, DescProductFragment.FragmentDescriptionProductActions,
        BottomSheetDataSetter {

    private static final String TAG = "MainActivity";

    private FragmentMap fragmentMap;
    private FragmentFavorite fragmentFavorite;
    private FragmentNotifications fragmentNotifications;
    private FragmentParameters fragmentParameters;
    private SearchFragment searchFragment;
    private ProductsFragment productsFragment;
    private FragmentBarcodeScanner fragmentBarcodeScanner;
    private DescProductFragment descProductFragment;
    private Fragment currentFragment;
    private AppRoomDatabase db;
    private SearchView searchView;
    private MenuItem myActionMenuItem;

    private Toolbar toolbar;
    private NavigationView navigationView;

    private boolean goToSearchFragment = false;
    private boolean searchProductBarcode = false;
    private String productBarcodeTemps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, fragmentMap, Constants.FRAGMENT_MAP)
                .commit();
        currentFragment = fragmentMap;
        initVariables();
        navigationView.setItemIconTintList(null);

        if (Utils.isUserConnected(MainActivity.this)) {
            navigationView.getMenu().getItem(4).setTitle(R.string.disconnect);
        }
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        DisposableManager.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.draweer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if ((getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_BARCODE_SCANNER) != null) ||
                (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_FAVORITE) != null) ||
                (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_NOTIFICATIONS) != null) ||
                (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_PARAMETERS) != null)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .add(R.id.frame_layout, fragmentMap, Constants.FRAGMENT_MAP)
                    .commit();
            currentFragment = fragmentMap;
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) supportActionBar.show();
        } else if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_MAP) != null) {
            Log.e(TAG, "MapFragment");
            if (searchFragment.isVisible()) {
                Log.e(TAG, "SearchFragment");
                fragmentMap.hideSearchFragment();
            } else if (productsFragment.isVisible()) {
                Log.e(TAG, "ProductFragment");
                fragmentMap.popSearchFragment();
            } else if (descProductFragment != null && descProductFragment.isVisible()) {
                Log.e(TAG, "DescProductFragment");
                descProductFragment = null;
                fragmentMap.popProductsFragment();
            } else if (fragmentMap.hasData()) {
                Log.e(TAG, "HasData");
                fragmentMap.popProductsFragment();
            } else {
                Log.e(TAG, "No SearchFragment, backpressed");
                fragmentMap.onBackPressed();
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);

        myActionMenuItem = menu.findItem(R.id.action_search);
        final MenuItem barcodeScanner = menu.findItem(R.id.action_barcode);
        barcodeScanner.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e(TAG, "Redirecting to barcode scanner");
                if (!Utils.checkCameraPermission(MainActivity.this)) {
                    Utils.requestCameraPermission(MainActivity.this);
                } else {
                    if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_BARCODE_SCANNER) == null) {
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentBarcodeScanner, Constants.FRAGMENT_BARCODE_SCANNER)
                                .commit();
                        currentFragment = fragmentBarcodeScanner;
                        ActionBar supportActionBar = getSupportActionBar();
                        if (supportActionBar != null) supportActionBar.hide();
                    }
                }
                return false;
            }
        });

        searchView = (SearchView) myActionMenuItem.getActionView();
        String[] columns = new String[] {"_id", "prop"};

        MatrixCursor matrixCursor= new MatrixCursor(columns);
        startManagingCursor(matrixCursor);

        matrixCursor.addRow(new Object[] {1, "probook 450"});

        SuggestinAdapter suggestinAdapter = new SuggestinAdapter(this, matrixCursor, searchView);
        searchView.setSuggestionsAdapter(suggestinAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Log.e(TAG, "OnSuggestionSelect");
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.e(TAG, "OnSuggestionClick");
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e(TAG, "OnClose");
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "Focus : " + hasFocus);
                if (hasFocus) {
                    if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_MAP) == null) {
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentMap, Constants.FRAGMENT_MAP)
                                .commit();
                        currentFragment = fragmentMap;
                        goToSearchFragment = true;
                    } else {
                        fragmentMap.onSearchViewFocus();
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "QueryTextSubmit");
                if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                if (fragmentMap != null) fragmentMap.submitSearchQuery(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.e(TAG, "QueryTextChange");
                return false;
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        item.setChecked(false);

        switch (id) {
            case R.id.nav_home : {
                if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_MAP) == null) {
                    DisposableManager.dispose();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .add(R.id.frame_layout, fragmentMap, Constants.FRAGMENT_MAP)
                            .commit();
                    currentFragment = fragmentMap;
                }
                break;
            }
            case R.id.nav_favorite : {
                if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_FAVORITE) == null) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentFavorite, Constants.FRAGMENT_FAVORITE)
                                .commit();
                        currentFragment = fragmentFavorite;
                    }
                }
                break;
            }
            case R.id.nav_notifications : {
                if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_NOTIFICATIONS) == null) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentNotifications, Constants.FRAGMENT_NOTIFICATIONS)
                                .commit();
                        currentFragment = fragmentNotifications;
                    }
                }
                break;
            }
            case R.id.nav_parameters : {
                if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_PARAMETERS) == null) {
                    if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                    DisposableManager.dispose();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .add(R.id.frame_layout, fragmentParameters, Constants.FRAGMENT_PARAMETERS)
                            .commit();
                    currentFragment = fragmentParameters;
                }
                break;
            }
            case R.id.nav_connexion : {
                if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                if (Utils.isUserConnected(MainActivity.this)) {
                    SharedPreferences.Editor preferencesEditor = getSharedPreferences(Constants.SHARED_PREFERENCES_USER,MODE_PRIVATE).edit();
                    preferencesEditor.remove(Constants.SHARED_PREFERENCES_USER_EMAIL)
                            .remove(Constants.SHARED_PREFERENCES_USER_PASSWORD)
                            .apply();
                    navigationView.getMenu().getItem(4).setTitle(R.string.connect);
                    final String deviceId = Utils.getStoredFirebaseTokenId(MainActivity.this);
                    PfeRx.removeFirebaseTokenId(deviceId);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.draweer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCategorySelected(int category) {
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            fragmentMap.onCategorySelected(category);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProductSelected(final String productBarcode) {
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            setToolbarTitleForFragmentMap();
            fragmentMap.onProductSelected(productBarcode);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setBottomSheetData(@NonNull final SalesPoint salesPoint, @NonNull final ProductSalesPoint productSalesPoint) {
        final TextView nameTextView = findViewById(R.id.sales_point_name_details);
        final TextView quantityTextView = findViewById(R.id.product_qte_marker);
        final TextView priceTextView = findViewById(R.id.product_price_marker);
        final ImageButton notifyMe = findViewById(R.id.notify_me);
        final ImageButton addToFavorite = findViewById(R.id.add_to_favorite);

        if (nameTextView != null) nameTextView.setText(salesPoint.getSalesPointName());
        if (quantityTextView != null) quantityTextView.setText(String.valueOf(productSalesPoint.getProductQuantity()));
        if (priceTextView != null) priceTextView.setText(String.format(Locale.getDefault(),"%.2f DA",productSalesPoint.getProductPrice()));

        if (notifyMe != null) {
            notifyMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        PfeRx.addToNotificationsList(salesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());
                        Snackbar.make(findViewById(R.id.map_views_layout),getResources().getString(R.string.notification_added), Snackbar.LENGTH_LONG)
                                .setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        PfeRx.removeFromNotificationsList(salesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                        addToFavorite(productSalesPoint, salesPoint);
                    }
                }
            });
        }

        if (addToFavorite != null) {
            addToFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        addToFavorite(productSalesPoint, salesPoint);
                        Snackbar.make(findViewById(R.id.map_views_layout), getResources().getString(R.string.saving_informations),Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void setBottomSheetDataDetails(@NonNull final SalesPoint salesPoint) {
        final TextView addressTextView = findViewById(R.id.sales_point_address_details);
        final ImageView salesPointPhoto = findViewById(R.id.sales_point_image_details);
        final TextView ratingMark = findViewById(R.id.rating);
        final RatingBar salesPointRating = findViewById(R.id.sales_point_rating_details);
        final TextView salesPointPhoneNumber = findViewById(R.id.sales_point_phone_number_details);
        final TextView salesPointWebSite = findViewById(R.id.sales_point_website_details);
        final ImageView salesPointItineraire = findViewById(R.id.sales_point_itineraire);

        if (addressTextView != null) addressTextView.setText(salesPoint.getSalesPointAddress());

        if (salesPointItineraire != null) salesPointItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, ItineraireActivity.class);
                intent.putExtra(Constants.INTENT_SALES_POINT_ID, salesPoint.getSalesPointId());
                startActivity(intent);

            }
        });

        if (salesPoint.getSalesPointPhotoReference() != null) {
            Picasso.get()
                    .load(Utils.buildGooglePictureUri(MainActivity.this,salesPoint.getSalesPointPhotoReference()))
                    .error(R.drawable.not_avaialble2)
                    .into(salesPointPhoto);
        }

        final DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if (ratingMark != null) ratingMark.setText(decimalFormat.format(salesPoint.getSalesPointRating()));
        if (salesPointRating != null) salesPointRating.setRating((float) salesPoint.getSalesPointRating());

        if (salesPointPhoneNumber != null) {
            salesPointPhoneNumber.setText(salesPoint.getSalesPointPhoneNumber());
            salesPointPhoneNumber.setPaintFlags(salesPointPhoneNumber.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
            salesPointPhoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = salesPointPhoneNumber.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });
        }

        if (salesPointWebSite != null) {
            salesPointWebSite.setText(salesPoint.getSalesPointWebSite());
            salesPointWebSite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = salesPointWebSite.getText().toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }
    }

    @Override
    public void setBottomSheetState(int state) {
        if (state == BottomSheetBehavior.PEEK_HEIGHT_AUTO || (state >= BottomSheetBehavior.STATE_DRAGGING
                && state <= BottomSheetBehavior.STATE_HIDDEN)) {
            fragmentMap.changeSheetBehaviorState(state);
        }
    }

    @Override
    public void searchQuery(@NonNull String query) {
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            fragmentMap.searchQuery(query);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    public void setSearchViewQuery(final String query) {
        if (searchView != null) {
            if(searchView.isIconified()) {
                searchView.setIconified(false);
            }
            myActionMenuItem.expandActionView();
            searchView.setQuery(query, false);
        }
    }

    @Override
    public void iconifySearchView() {
        if(!searchView.isIconified()) {
            searchView.setIconified(true);
        }
        myActionMenuItem.collapseActionView();
    }

    @Override
    public void openSearchView() {
        if(searchView.isIconified()) {
            searchView.setIconified(false);
        }
    }

    @Override
    public void onBarcodeScanFinished(final SparseArray<Barcode> barcodes) {
        Log.e(TAG, "BarcodeScanFinished");
        //TODO: Add rx call here
        productBarcodeTemps = barcodes.valueAt(0).rawValue;
        Log.e(TAG, "Value " + productBarcodeTemps);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_MAP) == null) {
                    DisposableManager.dispose();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .add(R.id.frame_layout, fragmentMap, Constants.FRAGMENT_MAP)
                            .commit();
                    currentFragment = fragmentMap;
                    searchProductBarcode = true;
                } else {
                    fragmentMap.onProductSelected(productBarcodeTemps);
                }
            }
        });
    }

    @Override
    public void checkSearchProductBarcode() {
        if (searchProductBarcode) {
            searchProductBarcode = false;
            fragmentMap.onProductSelected(productBarcodeTemps);
        }
    }

    public void initFragments() {
        fragmentMap = new FragmentMap();
        fragmentFavorite = new FragmentFavorite();
        fragmentNotifications = new FragmentNotifications();
        fragmentParameters = new FragmentParameters();
        searchFragment = new SearchFragment();
        productsFragment = new ProductsFragment();
        fragmentBarcodeScanner = FragmentBarcodeScanner.newInstance();
    }

    public void initVariables() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer =  findViewById(R.id.draweer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void checkGoToSearchFragment() {
        if (goToSearchFragment) {
            goToSearchFragment = false;
            fragmentMap.onSearchViewFocus();
        }
    }

    public void setToolbarTitle(final String title) {
        if (toolbar != null) toolbar.setTitle(title);
    }

    public void addPhoto( String photoReference)
    {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(Utils.buildGooglePictureUri(MainActivity.this, photoReference).toString());
                Bitmap bitmap= BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                db = AppRoomDatabase.getInstance(MainActivity.this);
                db.salesPointDao().update(byteArray);

            } catch(IOException e) {
                Log.e(TAG, "image " +e );
            }
        }
    }

    private void addToFavorite(final ProductSalesPoint productSalesPoint, final SalesPoint salesPoint) {
        db = AppRoomDatabase.getInstance(MainActivity.this);
        PfeRx.getProductDetails(MainActivity.this, productSalesPoint);
        if (!db.salesPointDao().salesPointExists(salesPoint.getSalesPointId())) {
            db.salesPointDao().insert(salesPoint);
            addPhoto(salesPoint.getSalesPointPhotoReference());
        }
    }

    @Override
    public void setDescProductFragment(DescProductFragment descProductFragment) {
        this.descProductFragment = descProductFragment;
    }

    @Override
    public void onMoreDetailsSelected(final String productBarcode) {
        fragmentMap.onProductMoreDetails(productBarcode);
    }

    @Override
    public ProductsFragment getActivityProductsFragment() {
        return productsFragment;
    }

    @Override
    public SearchFragment getActivitySearchFragment() {
        return searchFragment;
    }

    @Override
    public FragmentMap getActivityFragmentMap() {
        return fragmentMap;
    }

    @Override
    public void setToolbarTitleForFragmentFavorite() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_favorites));
    }

    @Override
    public void setToolbarTitleForFragmentNotifications() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_notifications));
    }

    @Override
    public void setToolbarTitleForFragmentBarcodeScanner() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_barcode_scanner));
    }

    @Override
    public void setToolbarTitleForSearchFragment() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_search));
    }

    @Override
    public void setToolbarTitleForProductFragment() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_products));
    }

    @Override
    public void setToolbarTitleForFragmentParameters() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_parameters));
    }

    @Override
    public void setToolbarTitleForFragmentMap() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_map));
    }

    @Override
    public void setToolbarTitleForFragmentDescProduct() {
        setToolbarTitle(getResources().getString(R.string.title_fragment_desc_product));
    }
}
