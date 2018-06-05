package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.ProductCharacteristicAdapter;
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.utils.Utils;

public class DescProductFragment extends Fragment {

    private static final String TAG = "ProductDescription";
    private static final String PRODUCT = "product";

    private FragmentDescriptionProductActions implementation;
    private FragmentActivity fragmentBelongActivity;
    private AppRoomDatabase db;
    private Product product;
    private View rootView;
    private TextView productName, productType,productCategory,productMark, emptyTextView;
    private ListView listProductCharacteristics;
    private ProductCharacteristicAdapter productCharacteristicAdapter;

    public DescProductFragment() {
    }

    public static DescProductFragment newInstance(final Product product) {
        DescProductFragment fragment = new DescProductFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            product = getArguments().getParcelable(PRODUCT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_desc_product, container, false);
        fragmentBelongActivity = getActivity();

        if (rootView != null) {
            initVariables();
            implementation.setToolbarTitleForFragmentDescProduct();

            setProductInformations(product);
            setCharacteristics();
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (FragmentDescriptionProductActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentDescriptionProductActions");
        }
    }

    public void initVariables() {
        productName = rootView.findViewById(R.id.descProduct_Name);
        productType = rootView.findViewById(R.id.descProduct_Type);
        productCategory = rootView.findViewById(R.id.descProduct_Category);
        productMark = rootView.findViewById(R.id.descProduct_Mark);
        emptyTextView = rootView.findViewById(R.id.empty_characteristics);
        listProductCharacteristics = rootView.findViewById(R.id.list_product_caracteristics);
        listProductCharacteristics.setEmptyView(emptyTextView);
        productCharacteristicAdapter = new ProductCharacteristicAdapter(getActivity(), R.layout.list_item_product_caracteristic , new ArrayList<KeyValue>());
        if (listProductCharacteristics != null) listProductCharacteristics.setAdapter(productCharacteristicAdapter);
        db = AppRoomDatabase.getInstance(getActivity());
    }

    private void setProductInformations(@NonNull final Product product) {
        if (productName != null) productName.setText(product.getProductName());
        if (productCategory != null) productCategory.setText(db.typeDao().getCategoryName(product.getProductType()));
        if (productType != null) productType.setText(db.typeDao().getName(product.getProductType()));
        if (productMark != null) productMark.setText(product.getProductTradeMark());
    }

    private void setCharacteristics() {
        if (listProductCharacteristics != null) {
            List<KeyValue> characteristics = db.productCharacteristicDao().getCharacteristics(product.getProductBarcode());
            if (characteristics != null && characteristics.size() > 0) {
                productCharacteristicAdapter.addAll(characteristics);
            } else {
                if (Utils.isNetworkAvailable(fragmentBelongActivity)) {
                    PfeRx.getProductCharacteristics(fragmentBelongActivity, product, false);
                } else {
                    Toast.makeText(fragmentBelongActivity, fragmentBelongActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setCharacteristicsFromNetwork(final List<KeyValue> productCharacteristics) {
        if (listProductCharacteristics != null) {
            productCharacteristicAdapter.clear();
            productCharacteristicAdapter.addAll(productCharacteristics);
        }
    }

    public void displayProductCharacteristics(final Product product, final List<KeyValue> productCharacteristics) {
        Log.e(TAG, "displayProductCharacteristics");
        setProductInformations(product);
        setCharacteristicsFromNetwork(productCharacteristics);
    }

    public interface FragmentDescriptionProductActions {
        void setToolbarTitleForFragmentDescProduct();
        void displayProductCharacteristics(final Product product, final List<KeyValue> productCharacteristics);
    }

}
