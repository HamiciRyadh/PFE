package usthb.lfbservices.com.pfe.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.SimpleFragmentPagerAdapter;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.fragments.DescProductSalesPointFragment;
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.RecyclerViewListener;


public class DescriptiveActivity extends AppCompatActivity implements RecyclerViewListener,
        DescProductFragment.FragmentDescriptionProductActions,
        DescProductSalesPointFragment.FragmentProductSalesPointDescriptionActions{

    private static final String TAG = "DescActivity";

    private Product product;
    private SimpleFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            product = bundle.getParcelable("product");
        }

        if (product == null) {
            product = new Product();
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager(), product);
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

    @Override
    public void displayProductCharacteristics(Product product, List<KeyValue> productCharacteristics) {
        if (adapter != null) {
            adapter.getDescProductFragment().displayProductCharacteristics(product, productCharacteristics);
        }
    }
}