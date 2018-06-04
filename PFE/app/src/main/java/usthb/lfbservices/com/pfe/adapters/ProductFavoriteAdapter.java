package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.DescriptiveActivity;
import usthb.lfbservices.com.pfe.models.Product;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;

public class ProductFavoriteAdapter extends RecyclerView.Adapter< ProductFavoriteAdapter.ViewHolder>  implements ITouchHelperAdapter {

    private static final String TAG = ProductFavoriteAdapter.class.getName();
    private Context context;
    private AppRoomDatabase db;
    private List<Product> products;


    /**
     * Adapter constructor
     *
     * @param products
     *         A collection of {@link Product } which will contain the data that will be used in each ViewHolder
     */

    public ProductFavoriteAdapter(List<Product> products) {
        this.products = products;
    }


    /**
     * This is where the ViewHolder(s) are created. Since the framework handles the initialization and recycling
     * we only need to use the viewtype passed in here to inflate our View
     *
     * @param parent
     *         The ViewGroup into which the new View will be added after it is bound to
     *         an adapter position.
     * @param viewType
     *         The view type of the new View.
     *
     * @return
     */
    @Override
    public  ProductFavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products_favorite, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This is where the data is bound to each ViewHolder. This method is called at least once and will be
     * called each time the adapter is notified that the data set has changed
     *
     * @param holder
     *         The ViewHolder
     * @param position
     *         The position in our collection of data
     */
    @Override
    public void onBindViewHolder(ProductFavoriteAdapter.ViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getProductName());
        holder.productTradeMark.setText(products.get(position).getProductTradeMark());
    }


    /**
     * Gets the size of the collection of items in our list
     *
     * @return An Integer representing the size of the collection that will be displayed
     */
    @Override
    public int getItemCount() {
        return products.size();
    }


    @Override
    public void onItemDismiss(int position) {
        db = AppRoomDatabase.getInstance(ProductFavoriteAdapter.this.context);
        String productBarcode = products.get(position).getProductBarcode();
        products.remove(position);
        db.productDao().deleteById(productBarcode);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDismiss(int position, int direction) {

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(products, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(products, i, i - 1);
            }
        }

        /**
         *  Notify any registered observers that the item reflected at fromPosition has been moved to toPosition.
         */

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView productName;
        public TextView productTradeMark;
        public RelativeLayout viewBackground, viewForeground;
        public String productBarcode;

        /**
         * The ViewHolder that will be used to display the data in each item shown
         * in the RecyclerView
         *
         * @param itemView
         *         The layout view group used to display the data
         */

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            productName = itemView.findViewById(R.id.product_name);
            productTradeMark = itemView.findViewById(R.id.product_trademark);
            viewBackground = itemView.findViewById(R.id.p_view_background);
            viewForeground = itemView.findViewById(R.id.p_view_foreground);
        }

        @Override
        public void onClick(View view) {
            Product product = products.get(getLayoutPosition());
            Intent intent = new Intent(context, DescriptiveActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        }
    }
}
