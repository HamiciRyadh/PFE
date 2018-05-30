package usthb.lfbservices.com.pfe.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.SimpleFragmentPagerAdapter;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.fragments.DescProductSalesPointFragment;
import usthb.lfbservices.com.pfe.models.RecyclerViewListener;


public class DescriptiveActivity extends AppCompatActivity implements RecyclerViewListener,
        DescProductFragment.FragmentDescriptionProductActions,
        DescProductSalesPointFragment.FragmentProductSalesPointDescriptionActions{

    private String productBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive);

        productBarcode = getIntent().getStringExtra("usthb.lfbservices.com.pfe.adapters.productBarcode");

        ViewPager viewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager(), productBarcode);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onItemClick() {

    }

    @Override
    public void setToolbarTitleForFragmentDescProduct() {

    }
}