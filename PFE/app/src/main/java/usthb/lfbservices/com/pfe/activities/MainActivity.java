package usthb.lfbservices.com.pfe.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.CategoryAdapter;
import usthb.lfbservices.com.pfe.models.Category;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private ArrayList<Category> listCategories = new ArrayList<Category>();
    private GridView gridView;
    private CategoryAdapter categoryAdapter;
    private String[] categories;
    private Bitmap icon;
    private int numberOfCategoriesToDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = getResources().getStringArray(R.array.categories_array);
        icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.common_full_open_on_phone);
        numberOfCategoriesToDisplay = calculateNumberOfCategoriesToDisplay();
        listCategories = getMinimumCategoriesToDisplay();

        gridView = (GridView) findViewById(R.id.category_grid_view);
        categoryAdapter = new CategoryAdapter(this, R.layout.category_grid, listCategories);
        gridView.setAdapter(categoryAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == numberOfCategoriesToDisplay-1 && numberOfCategoriesToDisplay < categories.length)
                {
                    listCategories.clear();
                    listCategories = getAllCategoriesToDisplay();
                    categoryAdapter.clear();
                    categoryAdapter.addAll(listCategories);
                }
                else if (position == categories.length)
                {
                    listCategories.clear();
                    listCategories = getMinimumCategoriesToDisplay();
                    categoryAdapter.clear();
                    categoryAdapter.addAll(listCategories);
                }
                else
                {
                    //Network call
                }
            }
        });

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
            list.add(new Category(icon, categories[categories.length-1], numberOfCategoriesToDisplay));
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
        int pxWidth = getResources().getDisplayMetrics().widthPixels;

        int result = (int)((pxWidth + 2*getResources().getDimension(R.dimen.categories_grid_layout_margin))
                /getResources().getDimension(R.dimen.categories_grid_column_width));

        return result;
    }
}
