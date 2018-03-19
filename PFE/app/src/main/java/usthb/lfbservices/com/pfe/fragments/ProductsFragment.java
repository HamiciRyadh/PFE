package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.network.PfeRx;

/**
 * Created by root on 12/03/18.
 */

public class ProductsFragment extends Fragment {

    private static final String TAG = ProductsFragment.class.getName();

    private ProductsFragmentActions implementation;


    private View rootView;
    private FragmentActivity fragmentBelongActivity;

    private ListView listView;

    public ProductsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_list_products, container, false);
        fragmentBelongActivity = getActivity();

        if (rootView != null) {
            listView = rootView.findViewById(R.id.list_view_products);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e(TAG, "onItemClick");
                    Product product = (Product)parent.getSelectedItem();
                    implementation.onProductSelected(product.getProductId());
                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (ProductsFragmentActions)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ProductsFragmentActions");
        }
    }

    public interface ProductsFragmentActions {

        void onProductSelected(int productId);
    }
}
