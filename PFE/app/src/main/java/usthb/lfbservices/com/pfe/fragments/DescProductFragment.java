package usthb.lfbservices.com.pfe.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.ProductCaracteristicAdapter;
import usthb.lfbservices.com.pfe.models.KeyValue;
import usthb.lfbservices.com.pfe.models.Product;

public class DescProductFragment extends Fragment {

    private static final String TAG = "ProductDescription";
    private static final String PRODUCT_BARCODE = "productBarcode";

    private FragmentDescriptionProductActions implementation;
    private AppRoomDatabase db;
    private Product product;
    private View rootView;
    private TextView productName, productType,productCategory,productMark;
    private ListView listProductCaracteristics;
    private String productBarcode;

    public DescProductFragment() {

    }

    public static DescProductFragment newInstance(final String productBarcode) {
        DescProductFragment fragment = new DescProductFragment();
        Bundle args = new Bundle();
        args.putString(PRODUCT_BARCODE, productBarcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productBarcode = getArguments().getString(PRODUCT_BARCODE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_desc_product, container, false);

        if (rootView != null) {
            initVariables();
            implementation.setToolbarTitleForFragmentDescProduct();

            if (product != null){
                if (productName != null) productName.setText(product.getProductName());
                if (productCategory != null) productCategory.setText(db.typeDao().getCategoryName(product.getProductType()));
                if (productType != null) productType.setText(db.typeDao().getName(product.getProductType()));
                if (productMark != null) productMark.setText(product.getProductTradeMark());

                if (listProductCaracteristics != null) {
                    ProductCaracteristicAdapter productCaracteristicAdapter = new ProductCaracteristicAdapter(getActivity(), R.layout.list_item_product_caracteristic , new ArrayList<KeyValue>());
                    listProductCaracteristics.setAdapter(productCaracteristicAdapter);
                    productCaracteristicAdapter.addAll(db.productCaracteristicDao().getCaracteristics(productBarcode));
                }
            }
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
        listProductCaracteristics = rootView.findViewById(R.id.list_product_caracteristics);

        db = AppRoomDatabase.getInstance(getActivity());
        product = db.productDao().getByBarcode(productBarcode);
    }

    public interface FragmentDescriptionProductActions {
        void setToolbarTitleForFragmentDescProduct();
    }

}
