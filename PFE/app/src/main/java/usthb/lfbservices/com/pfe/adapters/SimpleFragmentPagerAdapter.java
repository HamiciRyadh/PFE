package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.fragments.DescProductFragment;
import usthb.lfbservices.com.pfe.fragments.DescProductSalesPointFragment;
import usthb.lfbservices.com.pfe.models.Product;


public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private DescProductFragment descProductFragment;
    private DescProductSalesPointFragment descProductSalesPointFragment;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm, @NonNull Product product) {
        super(fm);
        mContext = context;
        descProductFragment = DescProductFragment.newInstance(product);
        descProductSalesPointFragment = DescProductSalesPointFragment.newInstance(product);
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

    public DescProductFragment getDescProductFragment() {
        return descProductFragment;
    }

    public DescProductSalesPointFragment getDescProductSalesPointFragment() {
        return descProductSalesPointFragment;
    }
}