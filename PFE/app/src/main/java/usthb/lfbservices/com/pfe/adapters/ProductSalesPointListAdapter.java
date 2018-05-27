package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import usthb.lfbservices.com.pfe.activities.DescSalesPointActivity;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;


public class ProductSalesPointListAdapter extends RecyclerView.Adapter<ProductSalesPointListAdapter.ViewHolder> implements ITouchHelperAdapter {

    private static final String TAG = "PSPListAdapter";
    private Context context;
    private AppRoomDatabase db;
    private View rootView;

    private List<ProductSalesPoint> productSalesPoints;

    public ProductSalesPointListAdapter (List<ProductSalesPoint> productSalesPoints) {
        this.productSalesPoints = productSalesPoints;
    }

    @Override
    public  ProductSalesPointListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sales_point_product, parent, false);
        return new ViewHolder(rootView);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductSalesPointListAdapter.ViewHolder holder, int position) {
        db = AppRoomDatabase.getInstance(ProductSalesPointListAdapter.this.context);
        String salespointId = productSalesPoints.get(position).getSalesPointId();
        String salespointName = db.salesPointDao().getSalesPointNameById(salespointId);
        holder.salesPointid.setText(salespointName);
        holder.price.setText(String.format("%.2f DA", productSalesPoints.get(position).getProductPrice()));
        holder.quantity.setText(String.valueOf(productSalesPoints.get(position).getProductQuantity()));
    }

    @Override
    public int getItemCount() {
            return productSalesPoints.size();
            }

    @Override
    public void onItemDismiss(int position) {
        db = AppRoomDatabase.getInstance(ProductSalesPointListAdapter.this.context);
        String salespointID = productSalesPoints.get(position).getSalesPointId();
        String productBarcode = productSalesPoints.get(position).getProductBarcode();
        db.productSalesPointDao().deleteById(productBarcode,salespointID);
        productSalesPoints.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(productSalesPoints, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(productSalesPoints, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView salesPointid;
        public TextView quantity;
        public TextView price;

        private String sSalesPointId;


        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            salesPointid = itemView.findViewById(R.id.sales_point_name_details);
            price = itemView.findViewById(R.id.product_price_marker);
            quantity = itemView.findViewById(R.id.product_qte_marker);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sSalesPointId =  productSalesPoints.get(getLayoutPosition()).getSalesPointId();
            Intent intent = new Intent(context, DescSalesPointActivity.class);
            intent.putExtra("usthb.lfbservices.com.pfe.adapters.productBarcode", sSalesPointId);
            context.startActivity(intent);
        }
    }
}

