package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.ProductFavoriteAdapter;
import usthb.lfbservices.com.pfe.adapters.TouchProductAdapter;

/**
 * Created by ryadh on 06/05/18.
 */

public class FragmentFavorite extends Fragment {

    private static final String TAG = "FragmentFavorite";

    private FavoriteActions implementation;
    private View rootView;
    private FragmentActivity fragmentBelongActivity;

    private AppRoomDatabase db;

    private RecyclerView recyclerView;
    private TextView emptyView;

    private ProductFavoriteAdapter adapter;

    public FragmentFavorite() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        fragmentBelongActivity = getActivity();

        if (rootView != null) {
            initVariables();
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);

        try {
            implementation = (FavoriteActions) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FavoriteActions");
        }
    }

    public void initVariables() {
        recyclerView = rootView.findViewById(R.id.recyclerview_Product);
        recyclerView.addItemDecoration(new DividerItemDecoration(fragmentBelongActivity, DividerItemDecoration.VERTICAL));

        emptyView = rootView.findViewById(R.id.empty_products_list);

        db = AppRoomDatabase.getInstance(fragmentBelongActivity);
        adapter = new ProductFavoriteAdapter(db.productDao().getAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentBelongActivity));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new TouchProductAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        if (adapter == null || adapter.getItemCount() ==0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public interface FavoriteActions {

    }
}
