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
import android.widget.Spinner;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.adapters.ProductsAdapter;
import usthb.lfbservices.com.pfe.models.CustomOnItemSelectedListener;
import usthb.lfbservices.com.pfe.models.Product;

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

    private Button btn_wilaya;
    private Button btn_ville;
    private Button btn_marque;
    private String[] listItemsWilaya;
    private ArrayList<String> listItemsVille = new ArrayList<>();
    private ArrayList<String> listItemsMarque = new ArrayList<>();
    private boolean[] checkedItemsWilaya;
    private boolean[] checkedItemsVille;
    private boolean[] checkedItemsMarque;
    private ArrayList<Integer> mUserItemsWilaya = new ArrayList<>();
    private ArrayList<Integer> mUserItemsVille = new ArrayList<>();
    private ArrayList<Integer> mUserItemsMarque = new ArrayList<>();
    private Spinner searchPerimeter;

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
        initWilayaButton();
        initVillesButton();
        initMarquesButton();
        addListenerOnSpinnerItemSelection();

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
        btn_wilaya = rootView.findViewById(R.id.btn_wilaya);
        btn_ville = rootView.findViewById(R.id.btn_ville);
        btn_marque = rootView.findViewById(R.id.btn_marque);
        progressBar = rootView.findViewById(R.id.products_progress_bar);
        listItemsWilaya = getResources().getStringArray(R.array.wilaya_item);
        checkedItemsWilaya = new boolean[listItemsWilaya.length];
    }


    public void addListenerOnSpinnerItemSelection() {
        searchPerimeter = rootView.findViewById(R.id.spinner_rayon);
        searchPerimeter.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void initMarquesButton() {
        btn_marque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=1; i<= listView.getAdapter().getCount(); i++ ){

                    String marque =  ((Product)listView.getAdapter().getItem(i)).getProductTradeMark();

                    if (! listItemsMarque.contains("marque"))
                        listItemsMarque.add(marque);
                }

                final String[] listMarque = listItemsMarque.toArray(new String[listItemsMarque.size()]);
                checkedItemsMarque = new boolean[listMarque.length];

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Marque);


                mBuilder.setMultiChoiceItems(listMarque, checkedItemsMarque, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsMarque.add(position);
                        } else {
                            mUserItemsMarque.remove((Integer.valueOf(position)));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String item = "";
                        for (int i = 0; i < mUserItemsMarque.size(); i++) {
                            item = item + listMarque[mUserItemsMarque.get(i)];
                            if (i != mUserItemsMarque.size() - 1) {
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
                        for (int i = 0; i < checkedItemsVille.length; i++) {
                            checkedItemsMarque[i] = false;
                            mUserItemsMarque.clear();

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

    public void initVillesButton() {
        btn_ville.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Ville);

                final String[] listVille = listItemsVille.toArray(new String[listItemsVille.size()]);
                checkedItemsVille = new boolean[listVille.length];


                mBuilder.setMultiChoiceItems(listVille, checkedItemsVille, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItemsVille.add(new Integer(position));
                        } else {
                            mUserItemsVille.remove(new Integer(position));
                        }
                    }

                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String item = "";
                        for (int i = 0; i < mUserItemsVille.size(); i++) {
                            item = item + listVille[mUserItemsVille.get(i)];
                            if (i != mUserItemsVille.size() - 1) {
                                item = item + ", ";
                            }
                        }

                        //ICII APPEL
                        //mItemSelected.setText(item);
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
                        for (int i = 0; i < checkedItemsVille.length; i++) {
                            checkedItemsVille[i] = false;
                            mUserItemsVille.clear();

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

    public void initWilayaButton() {
        btn_wilaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(fragmentBelongActivity);
                mBuilder.setTitle(R.string.dialog_title_Wilaya);
                mBuilder.setMultiChoiceItems(listItemsWilaya, checkedItemsWilaya, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.e("", "Position : " + position);
                        Log.e("", "IsChecked : " + isChecked);
                        if (isChecked) {
                            mUserItemsWilaya.add(new Integer(position));
                        } else {
                            mUserItemsWilaya.remove(new Integer(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        listItemsVille.clear();

                        for (int i = 0; i < mUserItemsWilaya.size(); i++) {

                            String element = listItemsWilaya[mUserItemsWilaya.get(i).intValue()];

                            if (element.equals("Alger")) {

                                listItemsVille.addAll(Arrays.asList(getResources().getStringArray(R.array.Algiers_item)));
                            }

                            if (element.equals("Annaba")) {
                                listItemsVille.addAll(Arrays.asList(getResources().getStringArray(R.array.Annaba_item)));
                            }


                        }

                        //ICI APPEL ! avec element
                        if (mUserItemsWilaya.size() != 0) btn_ville.setVisibility(View.VISIBLE);

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
                        for (int i = 0; i < checkedItemsWilaya.length; i++) {
                            checkedItemsWilaya[i] = false;
                            mUserItemsWilaya.clear();

                            btn_ville.setVisibility(View.GONE);
                            listItemsVille.clear();

                          //  appel ancienne initial
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
