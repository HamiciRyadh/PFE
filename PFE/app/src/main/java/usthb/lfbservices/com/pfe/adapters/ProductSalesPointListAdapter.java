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
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;


public class ProductSalesPointListAdapter extends RecyclerView.Adapter< ProductSalesPointListAdapter.ViewHolder> implements ITouchHelperAdapter {

    private static final String TAG = ProductSalesPointListAdapter.class.getName();
    private Context context;
    private AppRoomDatabase db;


    private List<ProductSalesPoint> productSalesPoints;

    public ProductSalesPointListAdapter (List<ProductSalesPoint> productSalesPoints) {
        this.productSalesPoints = productSalesPoints;
    }

    @Override
    public  ProductSalesPointListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_salespoint_product, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ProductSalesPointListAdapter.ViewHolder holder, int position) {
        db= AppRoomDatabase.getInstance(ProductSalesPointListAdapter.this.context);
        String salespointId =productSalesPoints.get(position).getSalesPointId();
        String salespointName = db.salesPointDao().getSalesPointNameById(salespointId);
        holder.salespointid.setText(salespointName);
        holder.price.setText(String.valueOf(productSalesPoints.get(position).getProductPrice()));
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

        public TextView salespointid;
        public TextView quantity;
        public TextView price;

        private String salesPointId;


        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            salespointid = itemView.findViewById(R.id.sales_point_name_details);
            price = itemView.findViewById(R.id.product_price_marker);
            quantity =itemView.findViewById(R.id.product_qte_marker);
        }

        @Override
        public void onClick(View view) {
            /*
            salesPointId =  productSalesPoints.get(getLayoutPosition()).getSalesPointId();
            Intent intent = new Intent(context,DescSalesPointActivity.class);
            context.startActivity(intent);
            Singleton.getInstance().setSalesPoint(salesPointId);
            */
            Log.d(TAG, "onClick SalesPoint " + salesPointId.toString());
        }



    }
}

