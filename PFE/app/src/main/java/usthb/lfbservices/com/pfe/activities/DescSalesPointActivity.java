package usthb.lfbservices.com.pfe.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.SalesPoint;
import usthb.lfbservices.com.pfe.models.Singleton;
import usthb.lfbservices.com.pfe.utils.Constants;

public class DescSalesPointActivity extends AppCompatActivity {

        AppRoomDatabase db;
        SalesPoint salesPoint;
        TextView salespointname,salespointaddress,salespointphone,salespointWebSite;
        RatingBar salespointrating;
        ImageView salespointItineraire;
        ImageView salespointimage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_description_sales_point);

            db = AppRoomDatabase.getInstance(this);
            final String salespointID = getIntent().getStringExtra("usthb.lfbservices.com.pfe.adapters.productBarcode");
            salesPoint = db.salesPointDao().getById(salespointID);

            initVariables();

            if (salesPoint != null) {
                salespointname.setText(salesPoint.getSalesPointName());
                salespointphone.setText(salesPoint.getSalesPointPhoneNumber());
                salespointaddress.setText(salesPoint.getSalesPointAddress());
                salespointWebSite.setText(salesPoint.getSalesPointWebSite());
                salespointrating.setRating((float)salesPoint.getSalesPointRating());
                salespointimage.setImageBitmap(BitmapFactory.decodeByteArray(salesPoint.getImage(), 0, salesPoint.getImage().length));

                salespointItineraire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<SalesPoint> listeSP = new ArrayList<>();
                        listeSP.add(salesPoint);
                        Singleton.getInstance().setSalesPointList(listeSP);
                        final Intent intent = new Intent(DescSalesPointActivity.this, ItineraireActivity.class);
                        intent.putExtra(Constants.INTENT_SALES_POINT_ID, salesPoint.getSalesPointId());
                        startActivity(intent);
                    }
                });

                salespointphone.setPaintFlags(salespointphone.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
                salespointphone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = salespointphone.getText().toString();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });

                salespointWebSite.setPaintFlags(salespointWebSite.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
                salespointWebSite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = salespointWebSite.getText().toString();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });
            }
        }

        public void initVariables() {
            salespointname = findViewById(R.id.descsales_point_name_details);
            salespointaddress =findViewById(R.id.descsales_point_address_details);
            salespointrating = findViewById(R.id.descsales_point_rating_details);
            salespointWebSite =findViewById(R.id.descsales_point_website_details);
            salespointphone = findViewById(R.id.descsales_point_phone_number_details);
            salespointItineraire = findViewById(R.id.descsales_point_itineraire);
            salespointimage = findViewById(R.id.descsales_point_image_details);
        }
}
