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
 * Fragment that displays the search panel which is composed.
 */

public class SearchFragment extends Fragment {

    private static String TAG = SearchFragment.class.getName();

    /**
     * An instance of {@link SearchFragmentActions} interface used to update the UI when actions
     * are performed.
     */
    private SearchFragmentActions implementation;

    private View rootView;

    /**
     * An instance of the Activity using the SearchFragment.
     */
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

    /**
     * A constructor.
     */
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

    /**
     * This method is executed when we attach the Fragment to an Activity, that Activity will be
     * stored in a {@link SearchFragmentActions} reference and thus must implement the interface
     * so that it will update its UI according to the actions performed.
     * @param context The Context of the Activity using the Fragment.
     */
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

    /**
     * This method initializes the variables.
     */
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

    /**
     * This method sets a {@link CategoryAdapter} to the categories GridView and a
     * {@link android.widget.AdapterView.OnItemClickListener} so that when the user selects the
     * "more" option, all the categories will be displayed, and when the user selects the "less"
     * option, only the strict minimum number of categories is displayed, and if a category is
     * selected, the appropriate actions are executed by calling the onCategorySelected method of
     * the {@link SearchFragmentActions} implementation.
     */
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
                    Log.e(TAG, "Category Network Call");
                    implementation.onCategorySelected(position);
                }
            }
        });
    }

    /**
     * This method sets a {@link HistoryAdapter} to the history ListView and a
     * {@link android.widget.AdapterView.OnItemClickListener} so that when the user selects an
     * element of the history, the appropriate actions are executed by calling the onHistorySelected
     * method of the {@link SearchFragmentActions} implementation.
     */
    public void initHistory() {
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Make it a method in the SearchFragmentActions as onHistorySelected()
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

    /**
     * This method reads an {@literal ArrayList<String>} from a file which name is stored in the
     * HISTORY_FILE_NAME constant of the {@link Utils} class and returns it.
     * @return An ArrayList<String> containing all the previously searched strings.
     */
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

    /**
     * This method adds the String passed in parameter to the {@literal ArrayList<String>}
     * representing the history searches and the write the whole list into a file which name is stored in the
     * HISTORY_FILE_NAME constant of the {@link Utils}.
     * @param history The search string to add to the history.
     */
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

    /**
     * This method clears the {@literal ArrayList<String>} representing the history searches using
     * the clear() method, gets the new history searches using the {@link #getHistorySearchs()},
     * clears the {@link HistoryAdapter} and then adds all the history searches to it.
     *
     */
    public void refreshHistory(){
        listHistorySearchs.clear();
        listHistorySearchs = getHistorySearchs();
        historyAdapter.clear();
        historyAdapter.addAll(listHistorySearchs);
    }

    /**
     * This method searches for the categories to display and decides whether or not there is a need
     * for the "more" option that would display the unshown categories.
     * @return An {@literal ArrayList<Category>} to display.
     */
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

    /**
     * This method searches for the categories to display and decides whether or not there is a need
     * for the "less" option that would display only the minimum number of categories.
     * @return An {@literal ArrayList<Category>} to display.
     */
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

    /**
     * This method calculates the maximum number of category elements (elements of the GridView
     * representing the categories) that can be displayed in one row according to the dimensions
     * of the screen.
     * @return The maximum number of categories elements that can be displayed in one row of the
     * GridView.
     */
    public int calculateNumberOfCategoriesToDisplay() {
        int pxWidth = fragmentBelongActivity.getResources().getDisplayMetrics().widthPixels;
        int result = (int)((pxWidth + 2*getResources().getDimension(R.dimen.categories_grid_layout_margin))
                /getResources().getDimension(R.dimen.categories_grid_column_width));

        return result;
    }

    /**
     * This interface contains the methods that the activity using this fragment has to implement
     * so that it will be able to act accordingly and updates its UI in response to the actions
     * performed in the {@link SearchFragment}.
     */
    public interface SearchFragmentActions {

        void onCategorySelected(int category);
    }
}
