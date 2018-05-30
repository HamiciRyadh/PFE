package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.fragments.DescProductSalesPointFragment;


public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private DescProductFragment descProductFragment;
    private DescProductSalesPointFragment descProductSalesPointFragment;
    private String productBarcode;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm, @NonNull String productBarcode) {
        super(fm);
        mContext = context;
        this.productBarcode = productBarcode;
        descProductFragment = DescProductFragment.newInstance(productBarcode);
        descProductSalesPointFragment = DescProductSalesPointFragment.newInstance(productBarcode);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return descProductFragment;
        } else
            return descProductSalesPointFragment;
    }


    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0: {
                return mContext.getString(R.string.title_fragment_desc_product);
            }
            case 1: {
                return mContext.getString(R.string.title_fragment_list_sales_point);
            }
            default: {
                return null;
            }
        }
    }
}

