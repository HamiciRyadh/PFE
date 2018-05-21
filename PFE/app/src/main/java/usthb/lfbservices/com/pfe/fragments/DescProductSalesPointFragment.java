package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.ProductSalesPointListAdapter;
import usthb.lfbservices.com.pfe.adapters.TouchSalespointAdapter;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.Utils;

public class DescProductSalesPointFragment extends Fragment {

    private static final String TAG = "FragmentPSPDescription";
    private static final String PRODUCT_BARCODE = "productBarcode";

    private FragmentProductSalesPointDescriptionActions implementation;
    private FragmentActivity fragmentBelongActivity;
    private AppRoomDatabase db;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ProductSalesPointListAdapter adapter;
    private List<ProductSalesPoint> productSalespointsList;
    private String productBarcode;

    public DescProductSalesPointFragment() {

    }

    public static DescProductSalesPointFragment newInstance(final String productBarcode) {
        DescProductSalesPointFragment fragment = new DescProductSalesPointFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_desc_product_salespoint, container, false);
        fragmentBelongActivity = getActivity();
        if (rootView != null) {
            recyclerView = rootView.findViewById(R.id.recyclerview_Salespoint);
            emptyView =rootView.findViewById(R.id.empty_list_productSalespoint);
/*
            if (Utils.isNetworkAvailable(getActivity())) {

            } else {*/
                db = AppRoomDatabase.getInstance(getActivity());
                productSalespointsList= db.productSalesPointDao().getAll(productBarcode);
                Log.e(TAG, "PSP SIZE " + productSalespointsList.size());
                adapter = new ProductSalesPointListAdapter(productSalespointsList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(fragmentBelongActivity, DividerItemDecoration.VERTICAL));
                ItemTouchHelper.Callback callback = new TouchSalespointAdapter(adapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recyclerView);

                if (adapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(R.string.no_salespoint_saved);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
  //          }
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (FragmentProductSalesPointDescriptionActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentDescriptionProductActions");
        }
    }

    public interface FragmentProductSalesPointDescriptionActions {

    }

}