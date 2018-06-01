package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.Utils;

public class DescSalesPointActivity extends AppCompatActivity {

    private static final String TAG = "DescSalesPoint";

    private AppRoomDatabase db;
    private TextView productQuantity, productPrice, salesPointName,salesPointAddress,salesPointPhone,salesPointWebsite, salesPointRatingValue;
    private RatingBar salesPointRating;
    private ImageView salesPointItinerary;
    private ImageView salesPointImage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            Log.e(TAG, "onCreate");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_description_sales_point);

            db = AppRoomDatabase.getInstance(this);
            final String salesPointID = getIntent().getStringExtra("salesPointID");
            final int productQuantity = getIntent().getIntExtra("productQuantity", -1);
            final double productPrice = getIntent().getDoubleExtra("productPrice", -1);

            initVariables();

            if (salesPointID != null && productPrice != -1 && productQuantity != -1) {
                this.productQuantity.setText(String.format(Locale.getDefault(),"%d", productQuantity));
                this.productPrice.setText(String.format(Locale.getDefault(),"%.2f DA", productPrice));
            } else {
                Log.e(TAG, "Missing Intent Arguments.");
                return;
            }

            if (Utils.isNetworkAvailable(this)) {
                PfeRx.getPlaceDetails(this, salesPointID);
            } else {
                final SalesPoint salesPoint = db.salesPointDao().getById(salesPointID);
                if (salesPoint != null) {
                    initViews(salesPoint, false);
                } else {
                    initViews(new SalesPoint(), false);
                }
            }
        }

        public void initViews(final SalesPoint salesPoint, final boolean insertIntoDatabase) {
            Log.e(TAG, "initViews");
            salesPointName.setText(salesPoint.getSalesPointName());
            if (salesPoint.getSalesPointAddress() == null || salesPoint.getSalesPointAddress().trim().equals("")) {
                salesPoint.setSalesPointAddress(getResources().getString(R.string.not_available));
            }
            salesPointAddress.setText(salesPoint.getSalesPointAddress());
            salesPointRating.setRating((float)salesPoint.getSalesPointRating());
            salesPointRatingValue.setText(String.format(Locale.getDefault(),"%.2f", salesPoint.getSalesPointRating()));

            salesPointItinerary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.isNetworkAvailable(DescSalesPointActivity.this)) {
                        Toast.makeText(DescSalesPointActivity.this, getResources().getString(R.string.no_internet) ,Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<SalesPoint> listSP = new ArrayList<>();
                    listSP.add(salesPoint);
                    Singleton.getInstance().setSalesPointList(listSP);
                    final Intent intent = new Intent(DescSalesPointActivity.this, ItineraryActivity.class);
                    intent.putExtra(Constants.INTENT_SALES_POINT_ID, salesPoint.getSalesPointId());
                    startActivity(intent);
                }
            });

            if (salesPoint.getSalesPointPhoneNumber() == null || salesPoint.getSalesPointPhoneNumber().trim().equals("")) {
                salesPoint.setSalesPointPhoneNumber(getResources().getString(R.string.not_available));
            }
            salesPointPhone.setText(salesPoint.getSalesPointPhoneNumber());
            salesPointPhone.setPaintFlags(salesPointPhone.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
            salesPointPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = salesPointPhone.getText().toString();
                    if (!phone.equalsIgnoreCase(getResources().getString(R.string.not_available))) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                }
            });

            if (salesPoint.getSalesPointWebSite() == null || salesPoint.getSalesPointWebSite().trim().equals("")) {
                salesPoint.setSalesPointWebSite(getResources().getString(R.string.not_available));
            }
            salesPointWebsite.setText(salesPoint.getSalesPointWebSite());
            salesPointWebsite.setPaintFlags(salesPointWebsite.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
            salesPointWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = salesPointWebsite.getText().toString();
                    if (!url.equalsIgnoreCase(getResources().getString(R.string.not_available))) {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://" + url;
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                }
            });

            if (insertIntoDatabase) {
                if (!db.salesPointDao().salesPointExists(salesPoint.getSalesPointId())) {
                    db.salesPointDao().insert(salesPoint);
                } else {
                    db.salesPointDao().update(salesPoint);
                }
            }
            if (salesPoint.getImage() != null) {
                salesPointImage.setImageBitmap(BitmapFactory.decodeByteArray(salesPoint.getImage(), 0, salesPoint.getImage().length));
            } else {
                Picasso.get()
                        .load(Utils.buildGooglePictureUri(DescSalesPointActivity.this,salesPoint.getSalesPointPhotoReference()))
                        .error(R.drawable.not_avaialble2)
                        .into(salesPointImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "Picasso Success");
                                if (insertIntoDatabase) {
                                    Utils.addPhoto(DescSalesPointActivity.this, salesPoint.getSalesPointId(), salesPoint.getSalesPointPhotoReference());
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "Picasso Failure");
                            }
                        });
            }
        }

        public void initVariables() {
            productQuantity = findViewById(R.id.desc_product_quantity);
            productPrice = findViewById(R.id.desc_product_price);
            salesPointName = findViewById(R.id.descsales_point_name_details);
            salesPointAddress =findViewById(R.id.descsales_point_address_details);
            salesPointRating = findViewById(R.id.descsales_point_rating_details);
            salesPointRatingValue = findViewById(R.id.rating);
            salesPointWebsite =findViewById(R.id.descsales_point_website_details);
            salesPointPhone = findViewById(R.id.descsales_point_phone_number_details);
            salesPointItinerary = findViewById(R.id.descsales_point_itineraire);
            salesPointImage = findViewById(R.id.descsales_point_image_details);
        }
}
