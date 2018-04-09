package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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


    private Button btn_Marque;
    private ArrayList<String> listCheckedTradeMarks = new ArrayList<String>();
    private ArrayList<String> listItemsMarque = new ArrayList<>();
    private boolean[] checkedItemsMarque;

    private Button btn_Type;
    private ArrayList<String> listItemsType = new ArrayList<>();
    private boolean[] checkedItemsType;
    private ArrayList<Integer> mUserItemsType = new ArrayList<>();


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
                    Product product = (Product)parent.getAdapter().getItem(position);
                    implementation.onProductSelected(product.getProductId());
                }
            });
        }

        initVariables();
        initMarquesButton();
        initTypeButton();

        return rootView;
    }

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

    @Override
    public void onResume() {
        Log.e(TAG, "RESUME");
        if (savedProductsAdapter != null) {
            listView.setAdapter(savedProductsAdapter);
            if (progressBar != null) progressBar.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "PAUSE");
        savedProductsAdapter = (ProductsAdapter)listView.getAdapter();
        savedListProducts = new ArrayList<Product>();
        ProductsAdapter productsAdapter = (ProductsAdapter)listView.getAdapter();
        if (productsAdapter != null) {
            for (int i = 0; i < productsAdapter.getCount(); i++) {
                savedListProducts.add(productsAdapter.getItem(i));
            }
        }

        super.onPause();
    }


    public void initVariables() {
        progressBar = rootView.findViewById(R.id.products_progress_bar);
        btn_Marque = rootView.findViewById(R.id.btn_Marque);
        btn_Type = rootView.findViewById(R.id.btn_Type);
    }



    public void initMarquesButton() {
        btn_Marque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<Product> productsList = Singleton.getInstance().getProductList();
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

                        List<Product> temporaryProductsList;
                        ((ProductsAdapter) listView.getAdapter()).clear();

                        if (listCheckedTradeMarks.size() == 0 || listCheckedTradeMarks.size() == listItemsMarque.size()) {
                            temporaryProductsList = Singleton.getInstance().getProductList();
                        }

                        else {
                            temporaryProductsList = new ArrayList<Product>();
                            for (Product product : productsList) {
                                for (String tradeMark : listCheckedTradeMarks) {
                                    if (product.getProductTradeMark().equals(tradeMark)) {
                                        temporaryProductsList.add(product);
                                    }
                                }
                            }
                        }

                        ((ProductsAdapter) listView.getAdapter()).addAll(temporaryProductsList);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        listCheckedTradeMarks.clear();
                        ((ProductsAdapter) listView.getAdapter()).clear();
                        ((ProductsAdapter) listView.getAdapter()).addAll(Singleton.getInstance().getProductList());
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }


    //TODO: Tout refaire comme avec les marques
    public void initTypeButton() {
        btn_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i< listView.getAdapter().getCount(); i++ ){

                    int Type =  ((Product)listView.getAdapter().getItem(i)).getProductType();

                    //Web Service
                    // type en string !!

                    if (! listItemsType.contains(""+Type))
                        listItemsType.add(""+Type);
                }

                final String[] listType = listItemsType.toArray(new String[listItemsType.size()]);
                checkedItemsType = new boolean[listType.length];

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Type);


                mBuilder.setMultiChoiceItems(listType, checkedItemsType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsType.add(position);
                        } else {
                            mUserItemsType.remove((Integer.valueOf(position)));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String item = "";
                        for (int i = 0; i < mUserItemsType.size(); i++) {
                            item = item + listType[mUserItemsType.get(i)];
                            if (i != mUserItemsType.size() - 1) {
                                item = item + ", ";
                            }
                        }

                        //ICII APPEL
                        //  mItemSelected.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItemsType.length; i++) {
                            checkedItemsType[i] = false;
                            mUserItemsType.clear();

                            //CHANGEMENT
                            //  mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }




    public interface ProductsFragmentActions {

        void onProductSelected(int productId);
    }
}
