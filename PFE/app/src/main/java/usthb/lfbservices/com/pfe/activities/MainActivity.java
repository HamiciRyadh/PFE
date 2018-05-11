package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import usthb.lfbservices.com.pfe.R;
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
import usthb.lfbservices.com.pfe.utils.Constantes;
import usthb.lfbservices.com.pfe.utils.DisposableManager;
import usthb.lfbservices.com.pfe.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentMap.MapActions, FragmentFavorite.FavoriteActions,
        FragmentNotifications.NotificationsActions, FragmentParameters.ParametersActions,
        SearchFragment.SearchFragmentActions, ProductsFragment.ProductsFragmentActions,
        BottomSheetDataSetter {

    private static final String TAG = MainActivity.class.getName();

    private FragmentMap fragmentMap;
    private FragmentFavorite fragmentFavorite;
    private FragmentNotifications fragmentNotifications;
    private FragmentParameters fragmentParameters;
    private SearchFragment searchFragment;
    private ProductsFragment productsFragment;
    private Fragment currentFragment;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private int lastCheckedMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, fragmentMap, Constantes.FRAGMENT_MAP)
                .commit();
        currentFragment = fragmentMap;
        lastCheckedMenuItem = 0;
        initVariables();

        SharedPreferences preferences = getSharedPreferences(Constantes.SHARED_PREFERENCES_USER,MODE_PRIVATE);
        String mailAddress = preferences.getString(Constantes.SHARED_PREFERENCES_USER_EMAIL, null);
        String password = preferences.getString(Constantes.SHARED_PREFERENCES_USER_PASSWORD, null);
        if (mailAddress != null && password != null) {
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
        } else {
            if (getSupportFragmentManager().findFragmentByTag(Constantes.FRAGMENT_MAP) != null) {
                Log.e(TAG, "MapFragment");
                if (searchFragment.isVisible()) {
                    Log.e(TAG, "SearchFragment");
                    fragmentMap.hideSearchFragment();
                } else if (productsFragment.isVisible()) {
                    Log.e(TAG, "ProductFragment");
                    fragmentMap.popSearchFragment();
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
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home : {
                if (getSupportFragmentManager().findFragmentByTag(Constantes.FRAGMENT_MAP) == null) {
                    lastCheckedMenuItem = 0;
                    DisposableManager.dispose();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .add(R.id.frame_layout, fragmentMap, Constantes.FRAGMENT_MAP)
                            .commit();
                    currentFragment = fragmentMap;
                }
                break;
            }
            case R.id.nav_favorite : {
                if (getSupportFragmentManager().findFragmentByTag(Constantes.FRAGMENT_FAVORITE) == null) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        navigationView.getMenu().getItem(lastCheckedMenuItem).setChecked(true);
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                        lastCheckedMenuItem = 1;
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentFavorite, Constantes.FRAGMENT_FAVORITE)
                                .commit();
                        currentFragment = fragmentFavorite;
                    }
                }
                break;
            }
            case R.id.nav_notifications : {
                if (getSupportFragmentManager().findFragmentByTag(Constantes.FRAGMENT_NOTIFICATIONS) == null) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        navigationView.getMenu().getItem(lastCheckedMenuItem).setChecked(true);
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                        lastCheckedMenuItem = 2;
                        DisposableManager.dispose();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .add(R.id.frame_layout, fragmentNotifications, Constantes.FRAGMENT_NOTIFICATIONS)
                                .commit();
                        currentFragment = fragmentNotifications;
                    }
                }
                break;
            }
            case R.id.nav_parameters : {
                if (getSupportFragmentManager().findFragmentByTag(Constantes.FRAGMENT_PARAMETERS) == null) {
                    if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                    lastCheckedMenuItem = 3;
                    DisposableManager.dispose();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .add(R.id.frame_layout, fragmentParameters, Constantes.FRAGMENT_PARAMETERS)
                            .commit();
                    currentFragment = fragmentParameters;
                }
                break;
            }
            case R.id.nav_connexion : {
                if (currentFragment == fragmentMap) fragmentMap.removeFragments();
                if (Utils.isUserConnected(MainActivity.this)) {
                    SharedPreferences.Editor preferencesEditor = getSharedPreferences(Constantes.SHARED_PREFERENCES_USER,MODE_PRIVATE).edit();
                    preferencesEditor.remove(Constantes.SHARED_PREFERENCES_USER_EMAIL)
                            .remove(Constantes.SHARED_PREFERENCES_USER_PASSWORD)
                            .apply();
                    navigationView.getMenu().getItem(4).setTitle(R.string.connect);
                    navigationView.getMenu().getItem(0).setChecked(true);
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
        fragmentMap.onCategorySelected(category);
    }

    @Override
    public void onProductSelected(final String productBarcode) {
        fragmentMap.onProductSelected(productBarcode);
    }

    @Override
    public void setBottomSheetData(@NonNull final SalesPoint salesPoint, @NonNull final ProductSalesPoint productSalesPoint) {
        final TextView nameTextView = findViewById(R.id.sales_point_name_details);
        final TextView quantityTextView = findViewById(R.id.product_qte_marker);
        final TextView priceTextView = findViewById(R.id.product_price_marker);
        final ImageView notifyMe = findViewById(R.id.notify_me);
        final ImageView addToFavorite = findViewById(R.id.add_to_favorite);

        if (nameTextView != null) nameTextView.setText(salesPoint.getSalesPointName());
        if (quantityTextView != null) quantityTextView.setText(""+productSalesPoint.getProductQuantity());
        if (priceTextView != null) priceTextView.setText(""+productSalesPoint.getProductPrice());

        if (notifyMe != null) {
            notifyMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isUserConnected(MainActivity.this)) {
                        Utils.showConnectDialog(MainActivity.this);
                    } else {
                        PfeRx.addToNotificationsList(salesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());
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
                        //TODO: Insert code here
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
                intent.putExtra(Constantes.INTENT_SALES_POINT_ID, salesPoint.getSalesPointId());
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
        fragmentMap.searchQuery(query);
    }

    public void initFragments() {
        fragmentMap = new FragmentMap();
        fragmentFavorite = new FragmentFavorite();
        fragmentNotifications = new FragmentNotifications();
        fragmentParameters = new FragmentParameters();
        searchFragment = new SearchFragment();
        productsFragment = new ProductsFragment();
    }

    public void initVariables() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer =  findViewById(R.id.draweer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public ProductsFragment getActivityProductsFragment() {
        return productsFragment;
    }

    @Override
    public SearchFragment getActivitySearchFragment() {
        return searchFragment;
    }

}
