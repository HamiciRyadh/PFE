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
 *
 */

public class ProductsFragment extends Fragment {

    private static final String TAG = ProductsFragment.class.getName();

    /**
     * An instance of {@link ProductsFragmentActions} interface used to update the UI when actions
     * are performed.
     */
    private ProductsFragmentActions implementation;


    private View rootView;

    /**
     * An instance of the Activity using the SearchFragment.
     */
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

    /**
     * This method is executed when we attach the Fragment to an Activity, that Activity will be
     * stored in a {@link ProductsFragmentActions} reference and thus must implement the interface
     * so that it will update its UI according to the actions performed.
     * @param context The Context of the Activity using the Fragment.
     */
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

    /**
     * This interface contains the methods that the activity using this fragment has to implement
     * so that it will be able to act accordingly and updates its UI in response to the actions
     * performed in the {@link ProductsFragment}.
     */
    public interface ProductsFragmentActions {

        void onProductSelected(int productId);
    }
}
