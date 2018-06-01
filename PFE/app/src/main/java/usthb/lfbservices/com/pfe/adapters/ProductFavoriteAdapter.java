package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.activities.DescriptiveActivity;
import usthb.lfbservices.com.pfe.models.Product;

public class ProductFavoriteAdapter extends RecyclerView.Adapter< ProductFavoriteAdapter.ViewHolder>  implements ITouchHelperAdapter {

    private static final String TAG = ProductFavoriteAdapter.class.getName();
    private Context context;
    private AppRoomDatabase db;
    private List<Product> products;


    public ProductFavoriteAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public  ProductFavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductFavoriteAdapter.ViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getProductName());
        holder.productTradeMark.setText(products.get(position).getProductTradeMark());
    }


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
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView productName;
        public TextView productTradeMark;
        public RelativeLayout viewBackground, viewForeground;
        public String productBarcode;

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
