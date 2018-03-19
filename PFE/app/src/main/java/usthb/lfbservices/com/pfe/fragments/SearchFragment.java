package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.CategoryAdapter;
import usthb.lfbservices.com.pfe.adapters.HistoryAdapter;
import usthb.lfbservices.com.pfe.models.Category;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.utils.Utils;

/**
 * Created by root on 11/03/18.
 */

public class SearchFragment extends Fragment {

    private static String TAG = SearchFragment.class.getName();

    private SearchFragmentActions implementation;

    private View rootView;
    private FragmentActivity fragmentBelongActivity;

    private ListView listView;
    private ArrayList<Category> listCategories;
    private ArrayList<String> listHistorySearchs;
    private GridView gridView;
    private CategoryAdapter categoryAdapter;
    private HistoryAdapter historyAdapter;
    private String[] categories;
    private Bitmap icon;
    private int numberOfCategoriesToDisplay;

    public SearchFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        fragmentBelongActivity = getActivity();

        if(rootView != null) {
            initVariables();
            initCategories();
            initHistory();
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            implementation = (SearchFragmentActions)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SearchFragmentActions");
        }
    }

    public void initVariables() {
        listView = rootView.findViewById(R.id.list_view_history);
        categories = getResources().getStringArray(R.array.categories_array);
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.common_full_open_on_phone);
        numberOfCategoriesToDisplay = calculateNumberOfCategoriesToDisplay();
        listCategories = getMinimumCategoriesToDisplay();
        listHistorySearchs = getHistorySearchs();
        gridView = rootView.findViewById(R.id.category_grid_view);
        categoryAdapter = new CategoryAdapter(fragmentBelongActivity, R.layout.category_grid, listCategories);
        historyAdapter = new HistoryAdapter(fragmentBelongActivity, R.layout.list_item_history, listHistorySearchs);
    }

    public void initCategories() {
        gridView.setAdapter(categoryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == numberOfCategoriesToDisplay-1 && categoryAdapter.getCount() < categories.length) {
                    listCategories.clear();
                    listCategories = getAllCategoriesToDisplay();
                    categoryAdapter.clear();
                    categoryAdapter.addAll(listCategories);
                }
                else if (position == categories.length) {
                    listCategories.clear();
                    listCategories = getMinimumCategoriesToDisplay();
                    categoryAdapter.clear();
                    categoryAdapter.addAll(listCategories);
                }
                else {
                    //Network call
                    Log.e(TAG, "Category Network Call");
                    implementation.onCategorySelected(position);
                }
            }
        });
    }

    public void initHistory() {
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Network call
                Log.e(TAG, "History Network Call");
                if (Utils.isNetworkAvailable(fragmentBelongActivity)) {
                    PfeRx.search(fragmentBelongActivity, R.layout.list_item_products, historyAdapter.getItem(position));
                }
                else {
                    Toast.makeText(fragmentBelongActivity, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public ArrayList<String> getHistorySearchs() {
        Log.e(TAG, "READING FILE : " + Utils.HISTORY_FILE_NAME);
        ArrayList<String> list = new ArrayList<String>();

        try {
            File file = new File(fragmentBelongActivity.getFilesDir(), Utils.HISTORY_FILE_NAME);
            if (!file.exists()) {
                list.add(getString(R.string.empty_history));
            }
            else{
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (ArrayList<String>) ois.readObject();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void addToHistorySearchs(String history) {
        Log.e(TAG, "WRITING TO FILE : " + Utils.HISTORY_FILE_NAME);
        listHistorySearchs.add(0, history);
        try {
            File file = new File(fragmentBelongActivity.getFilesDir(), Utils.HISTORY_FILE_NAME);
            FileOutputStream fos;

            fos = new FileOutputStream(file);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listHistorySearchs);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshHistory(){
        listHistorySearchs.clear();
        listHistorySearchs = getHistorySearchs();
        historyAdapter.clear();
        historyAdapter.addAll(listHistorySearchs);
    }

    public ArrayList<Category> getMinimumCategoriesToDisplay() {
        ArrayList<Category> list = new ArrayList<Category>();

        for (int i = 1; i < numberOfCategoriesToDisplay; i++) {
            list.add(new Category(icon, categories[i-1], i));
        }

        if (numberOfCategoriesToDisplay < categories.length) {
            list.add(new Category(icon, getString(R.string.more), numberOfCategoriesToDisplay));
        }
        else {
            if (numberOfCategoriesToDisplay == categories.length) {
                list.add(new Category(icon, categories[categories.length-1], numberOfCategoriesToDisplay));
            }
        }

        return list;
    }

    public ArrayList<Category> getAllCategoriesToDisplay() {
        ArrayList<Category> list = new ArrayList<Category>();

        for (int i = 1; i <= categories.length; i++) {
            list.add(new Category(icon, categories[i-1], i));
        }

        if (numberOfCategoriesToDisplay < categories.length) {
            list.add(new Category(icon, getString(R.string.less), categories.length+1));
        }

        return list;
    }

    public int calculateNumberOfCategoriesToDisplay() {
        int pxWidth = fragmentBelongActivity.getResources().getDisplayMetrics().widthPixels;
        int result = (int)((pxWidth + 2*getResources().getDimension(R.dimen.categories_grid_layout_margin))
                /getResources().getDimension(R.dimen.categories_grid_column_width));

        return result;
    }

    public interface SearchFragmentActions {

        void onCategorySelected(int category);
    }
}
