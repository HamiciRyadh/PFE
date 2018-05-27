package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.models.Singleton;

/**
 *
 */

public class ProductsFragment extends Fragment {

    private static final String TAG = ProductsFragment.class.getName();

    private ProductsFragmentActions implementation;

    private View rootView;
    private FragmentActivity fragmentBelongActivity;
    private ListView listView;
    private ProgressBar progressBar;
    private List<Product> savedListProducts = null;
    private ProductsAdapter savedProductsAdapter = null;
    private AppRoomDatabase db;


    private Button btn_Marque;
    private ArrayList<String> listCheckedTradeMarks = new ArrayList<String>();
    private ArrayList<String> listItemsMarque = new ArrayList<>();
    private boolean[] checkedItemsMarque;

    private Button btn_Type;
    private ArrayList<String> listItemsType = new ArrayList<>();
    private ArrayList<Integer> mUserItemsType = new ArrayList<>();
    private String[] types;
    private boolean[] checkedItemsType;


    public ProductsFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_list_products, container, false);
        fragmentBelongActivity = getActivity();

        if (rootView != null) {
            listView = rootView.findViewById(R.id.list_view_products);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Product product = (Product)parent.getAdapter().getItem(position);
                    implementation.onProductSelected(product.getProductBarcode());
                }
            });
            initVariables();
            initMarquesButton();
            initTypeButton();
        }

        implementation.setToolbarTitleForProductFragment();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        listCheckedTradeMarks = new ArrayList<>();
        mUserItemsType = new ArrayList<>();
        try {
            implementation = (ProductsFragmentActions)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ProductsFragmentActions");
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "RESUME");

        if (savedProductsAdapter != null) {
            listView.setAdapter(savedProductsAdapter);
            if (progressBar != null) progressBar.setVisibility(View.GONE);
        } else {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "PAUSE");
        savedProductsAdapter = (ProductsAdapter)listView.getAdapter();
        savedListProducts = new ArrayList<>();
        ProductsAdapter productsAdapter = (ProductsAdapter)listView.getAdapter();
        if (productsAdapter != null) {
            for (int i = 0; i < productsAdapter.getCount(); i++) {
                savedListProducts.add(productsAdapter.getItem(i));
            }
        }

        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "OnDetach");
        super.onDetach();
        implementation = null;
    }


    public void initVariables() {
        db = AppRoomDatabase.getInstance(fragmentBelongActivity);
        progressBar = rootView.findViewById(R.id.products_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        btn_Marque = rootView.findViewById(R.id.btn_Marque);
        btn_Type = rootView.findViewById(R.id.btn_Type);
        types = db.typeDao().getAll();
    }



    public void initMarquesButton() {
        btn_Marque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<Product> productsList = Singleton.getInstance().getProductList();

                listItemsMarque = new ArrayList<>();
                for (int i = 0; i < productsList.size(); i++) {
                    String marque =  productsList.get(i).getProductTradeMark();
                    if (!listItemsMarque.contains(marque)) listItemsMarque.add(marque);
                }

                final String[] listMarque = listItemsMarque.toArray(new String[listItemsMarque.size()]);

                checkedItemsMarque = new boolean[listMarque.length];

                //Préserver les cases cochées
                for (int i = 0; i < checkedItemsMarque.length; i++) {
                    if (listCheckedTradeMarks.contains(listMarque[i])) {
                        checkedItemsMarque[i] = true;
                    }
                }

                final ArrayList<String> listCheckedTradeMarksSave = new ArrayList<String>();
                listCheckedTradeMarksSave.addAll(listCheckedTradeMarks);

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Marque);


                mBuilder.setMultiChoiceItems(listMarque, checkedItemsMarque, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            listCheckedTradeMarks.add(listItemsMarque.get(position));
                        } else {
                            listCheckedTradeMarks.remove(listItemsMarque.get(position));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        List<Product> temporaryProductsList = sortByType();
                        temporaryProductsList = sortByTradeMark(temporaryProductsList);

                        ((ProductsAdapter) listView.getAdapter()).addAll(temporaryProductsList);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listCheckedTradeMarks.clear();
                        listCheckedTradeMarks.addAll(listCheckedTradeMarksSave);
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        listCheckedTradeMarks.clear();
                        ((ProductsAdapter) listView.getAdapter()).clear();
                        ((ProductsAdapter) listView.getAdapter()).addAll(sortByType());
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }


    public void initTypeButton() {
        btn_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Product> products = Singleton.getInstance().getProductList();
                final ArrayList<Integer> mUserItemsTypeSave = new ArrayList<Integer>();
                mUserItemsTypeSave.addAll(mUserItemsType);

                Log.e(TAG, ""+products.size());

                listItemsType = new ArrayList<>();
                for (int i=0; i< products.size(); i++ ){
                    int type =  products.get(i).getProductType();
                    if (!listItemsType.contains(types[type])) listItemsType.add(types[type]);
                }

                final String[] listType = listItemsType.toArray(new String[listItemsType.size()]);
                checkedItemsType = new boolean[listType.length];

                for (Integer integer : mUserItemsType) {
                    checkedItemsType[integer.intValue()] = true;
                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Type);
                mBuilder.setMultiChoiceItems(listType, checkedItemsType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsType.add(new Integer(position));
                        } else {
                            mUserItemsType.remove(new Integer(position));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        List<Product> temporaryProductsList = sortByType();
                        temporaryProductsList = sortByTradeMark(temporaryProductsList);

                        ((ProductsAdapter) listView.getAdapter()).clear();
                        ((ProductsAdapter) listView.getAdapter()).addAll(temporaryProductsList);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserItemsType.clear();
                        mUserItemsType.addAll(mUserItemsTypeSave);
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsType.length; i++) {
                            checkedItemsType[i] = false;
                        }
                        mUserItemsType.clear();
                        listItemsType.clear();
                        ((ProductsAdapter) listView.getAdapter()).clear();
                        ((ProductsAdapter) listView.getAdapter())
                                .addAll(sortByTradeMark(Singleton.getInstance().getProductList()));
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    public List<Product> sortByType() {
        List<Product> products = Singleton.getInstance().getProductList();
        List<Product> temporaryProductsList;
        if (mUserItemsType.size() == 0 || mUserItemsType.size() == products.size()) {
            temporaryProductsList = products;
        } else {
            temporaryProductsList = new ArrayList<Product>();

            for (Integer integer : mUserItemsType) {
                int correspondingType = integer.intValue();

                for (Product product : products) {
                    if (product.getProductType() == correspondingType) {
                        temporaryProductsList.add(product);
                    }
                }
            }
        }
        return temporaryProductsList;
    }

    public List<Product> sortByTradeMark(List<Product> productsSortedByType) {
        List<Product> temporaryProductsList;
        ((ProductsAdapter) listView.getAdapter()).clear();

        if (listCheckedTradeMarks.size() == 0 || listCheckedTradeMarks.size() == listItemsMarque.size()) {
            temporaryProductsList = productsSortedByType;
        }

        else {
            temporaryProductsList = new ArrayList<>();
            for (Product product : productsSortedByType) {
                for (String tradeMark : listCheckedTradeMarks) {
                    if (product.getProductTradeMark().equals(tradeMark)) {
                        temporaryProductsList.add(product);
                    }
                }
            }
        }
        return temporaryProductsList;
    }

    public void clearProductsFragment() {
        if (savedProductsAdapter != null ) {
            savedProductsAdapter.clear();
            savedProductsAdapter = null;
        }
    }


    public interface ProductsFragmentActions {

        void onProductSelected(final String productBarcode);
        void setToolbarTitleForProductFragment();
    }
}
