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
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.CategoryAdapter;
import usthb.lfbservices.com.pfe.adapters.HistoryAdapter;
import usthb.lfbservices.com.pfe.models.Category;
import usthb.lfbservices.com.pfe.utils.Constants;
import usthb.lfbservices.com.pfe.utils.Utils;

/**
 * A {@link Fragment} that displays the categories and the history searches.
 */

public class SearchFragment extends Fragment {

    private static String TAG = "SearchFragment";

    private SearchFragmentActions implementation;

    private View rootView;
    private FragmentActivity fragmentBelongActivity;
    private AppRoomDatabase db;

    private ListView listView;
    private ArrayList<Category> listCategories;
    private ArrayList<String> listHistorySearches;
    private GridView gridView;
    private CategoryAdapter categoryAdapter;
    private HistoryAdapter historyAdapter;
    private TextView emptyTextViewHistory;
    private String[] categories;
    private ArrayList<Bitmap> icon;
    private int numberOfCategoriesToDisplay;

    public SearchFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        implementation.setToolbarTitleForSearchFragment();
        return rootView;
    }

    /**
     * Checks if the attached {@link Context} implements the {@link SearchFragmentActions} interface,
     * if it is not the case, throws a {@link ClassNotFoundException}.
     * @param context The Context of the current Activity.
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
     * Removes the reference to the implementation.
     */
    @Override
    public void onDetach() {
        Log.e(TAG, "OnDetach");
        super.onDetach();
        implementation = null;
    }

    /**
     * Initializes the variables.
     */
    public void initVariables() {
        db = AppRoomDatabase.getInstance(fragmentBelongActivity);
        emptyTextViewHistory = rootView.findViewById(R.id.empty_history);
        listView = rootView.findViewById(R.id.list_view_history);
        ArrayList<String> listCategoryNames = new ArrayList<>();
        for (Category category : Category.Data()) listCategoryNames.add(category.getCategoryName());
        categories = listCategoryNames.toArray(new String[]{});
        icon = new ArrayList<>();
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.computer));
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.telephone));
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.montre));
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.voiture));
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.materiel));
        icon.add(BitmapFactory.decodeResource(getResources(), R.drawable.camera));

        numberOfCategoriesToDisplay = calculateNumberOfCategoriesToDisplay();
        listCategories = getMinimumCategoriesToDisplay();
        listHistorySearches = getHistorySearches();
        gridView = rootView.findViewById(R.id.category_grid_view);
        categoryAdapter = new CategoryAdapter(fragmentBelongActivity, R.layout.category_grid, listCategories);
        historyAdapter = new HistoryAdapter(fragmentBelongActivity, R.layout.list_item_history, listHistorySearches);
    }

    /**
     * Initializes the category {@link GridView} with the {@link CategoryAdapter} and displays the
     * minimal number of categories.
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
     * Initializes the history {@link ListView} with the {@link HistoryAdapter} and the associated
     * empty {@link TextView}.
     */
    public void initHistory() {
        listView.setAdapter(historyAdapter);
        listView.setEmptyView(emptyTextViewHistory);
        if (historyAdapter.getCount() == 0) {
            emptyTextViewHistory.setText(fragmentBelongActivity.getResources().getString(R.string.empty_history));
        } else {
            emptyTextViewHistory.setText("");
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "History Network Call");
                implementation.searchQuery(historyAdapter.getItem(position));
            }
        });
    }

    /**
     * Extracts the history searches from the history file.
     * @return An {@link ArrayList} of {@link String} representing the history searches.
     */
    public ArrayList<String> getHistorySearches() {
        Log.e(TAG, "READING FILE : " + Constants.HISTORY_FILE_NAME);
        ArrayList<String> list = new ArrayList<String>();

        try {
            File file = new File(fragmentBelongActivity.getFilesDir(), Constants.HISTORY_FILE_NAME);
            if (file.exists()) {
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
     * Adds a search string to the file containing the history searches.
     * @param history The history search to add in the file.
     */
    public void addToHistorySearches(final String history) {
        Log.e(TAG, "WRITING TO FILE : " + Constants.HISTORY_FILE_NAME);
        if (listHistorySearches.contains(history)) listHistorySearches.remove(history);
        listHistorySearches.add(0, history);
        try {
            File file = new File(fragmentBelongActivity.getFilesDir(), Constants.HISTORY_FILE_NAME);
            FileOutputStream fos;

            fos = new FileOutputStream(file);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(listHistorySearches);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the history list view to display the latest searches.
     */
    public void refreshHistory(){
        listHistorySearches.clear();
        listHistorySearches = getHistorySearches();
        historyAdapter.clear();
        historyAdapter.addAll(listHistorySearches);
    }

    /**
     * Gets the minimal number of {@link Category} to display, they will be displayed on one line in
     * addition to the "more" option.
     * @return An {@link ArrayList} of {@link Category}.
     */
    public ArrayList<Category> getMinimumCategoriesToDisplay() {
        ArrayList<Category> list = new ArrayList<>();

        Log.e(TAG, "Number of categories to display : " + numberOfCategoriesToDisplay);
        Log.e(TAG, "Categories.length : " + categories.length);
        Log.e(TAG, "Categories data size : " + Category.Data().size());

        for (int i = 1; i < numberOfCategoriesToDisplay && i <= categories.length; i++) {
            list.add(new Category(icon.get(i-1), categories[i-1], i));
        }

        if (numberOfCategoriesToDisplay < categories.length) {
            list.add(new Category(BitmapFactory.decodeResource(getResources(), R.drawable.plus), getString(R.string.more), numberOfCategoriesToDisplay));
        }
        else {
            if (numberOfCategoriesToDisplay == categories.length) {
                list.add(new Category(icon.get(categories.length-1), categories[categories.length-1], numberOfCategoriesToDisplay));
            }
        }

        return list;
    }

    /**
     * Gets all the categories to display in addition to the "less" option.
     * @return An {@link ArrayList} of {@link Category}.
     */
    public ArrayList<Category> getAllCategoriesToDisplay() {
        ArrayList<Category> list = new ArrayList<Category>();

        for (int i = 1; i <= categories.length; i++) {
            list.add(new Category(icon.get(i-1), categories[i-1], i));
        }

        if (numberOfCategoriesToDisplay < categories.length) {
            list.add(new Category(BitmapFactory.decodeResource(getResources(), R.drawable.moins), getString(R.string.less), categories.length+1));
        }

        return list;
    }

    /**
     * Calculates the maximal number of categories to display on one line of the screen.
     * @return The maximal number of categories to display on one line of the screen.
     */
    public int calculateNumberOfCategoriesToDisplay() {
        int pxWidth;
        pxWidth = Utils.getDeviceWidthInPixel(fragmentBelongActivity);
        int result = (int)((pxWidth + 2*getResources().getDimension(R.dimen.categories_grid_layout_margin))
                /getResources().getDimension(R.dimen.categories_grid_column_width));

        Log.e(TAG, "" + result);
        return result;
    }

    /**
     * The interface that needs to be implemented by the attached Activity.
     */
    public interface SearchFragmentActions {

        void onCategorySelected(int category);
        void searchQuery(String query);
        void setToolbarTitleForSearchFragment();
    }
}