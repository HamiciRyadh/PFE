package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.RoomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.Product;

//TODO: Remove comments
public class ProductListAdapter extends RecyclerView.Adapter< ProductListAdapter.ViewHolder>  implements ITouchHelperAdapter {

    private static final String TAG = ProductListAdapter.class.getName();
    private Context context;
    private AppRoomDatabase db;
    private String productBarcode;
    private List<Product> products;



    public ProductListAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public  ProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListAdapter.ViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getProductName());
        holder.productTradeMark.setText(products.get(position).getProductTradeMark());
    }


    @Override
    public int getItemCount() {
        return products.size();
    }


    @Override
    public void onItemDismiss(int position) {
        db = AppRoomDatabase.getInstance(ProductListAdapter.this.context);
        productBarcode = products.get(position).getProductBarcode();
        products.remove(position);
        db.productSalesPointDao().deleteByproductBarcode(productBarcode);
        db.productDao().deleteById(productBarcode);
        notifyItemRemoved(position);
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

        public String productBarcode;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            productName = itemView.findViewById(R.id.product_name);
            productTradeMark = itemView.findViewById(R.id.product_trademark);
        }

        @Override
        public void onClick(View view) {
            /*
            productBarcode = products.get(getLayoutPosition()).getProductBarcode();
            Intent intent = new Intent(context, DescriptiveActivity.class);
            context.startActivity(intent);
            Singleton.getInstance().setProduct(productBarcode);
            Log.d(TAG, "onClick Product " + productBarcode.toString());
            */
            Log.e("ProductListAdapter", "OnClick");
        }
    }
}
