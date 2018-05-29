package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.Notification;


public class NotificationListAdapter extends RecyclerView.Adapter< NotificationListAdapter.ViewHolder>  implements ITouchHelperAdapter {

    private static final String TAG = NotificationListAdapter.class.getName();
    private Context context;
    private AppRoomDatabase db;
    private int notificationId;
    private List<Notification> notifications;



    public NotificationListAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public  NotificationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationListAdapter.ViewHolder holder, int position) {

        db= AppRoomDatabase.getInstance(NotificationListAdapter.this.context);
        notifications = new ArrayList<>(db.notificationDao().getAll());
         String salespointName =notifications.get(position).getSalespointId();
         String productName =notifications.get(position).getProductId();

         holder.notificationSalespoint.setText(salespointName);

         holder.notificationProduct.setText(productName);

        //DateFormat df = new SimpleDateFormat("dd/MM/YYYY - hh:mm");


        long now = System.currentTimeMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                notifications.get(position).getNotificationDate().getTime(),
                now,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL);

       holder.notificationNewQuantity.setText(String.valueOf(notifications.get(position).getNotificationNewQuantity()));
       holder.notificationNewPrice.setText(String.format("%.2f DA", notifications.get(position).getNotificationNewPrice()));
       //holder.notificationDate.setText(df.format(notifications.get(position).getNotificationDate()));

        holder.notificationDate.setText(ago);

    }



    @Override
    public int getItemCount() {
        return notifications.size();
    }


    @Override
    public void onItemDismiss(int position) {
        db = AppRoomDatabase.getInstance(NotificationListAdapter.this.context);
        notificationId = notifications.get(position).getNotificationId();
        db.notificationDao().deleteById(notificationId);
        notifications.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDismiss(int position, int direction) {

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(notifications, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(notifications, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView notificationSalespoint;
        public TextView notificationProduct;
        public TextView notificationDate;
        public TextView notificationNewQuantity;
        public TextView notificationNewPrice;
        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

            notificationSalespoint = itemView.findViewById(R.id.notification_salespoint);
            notificationProduct = itemView.findViewById(R.id.notification_product);
            notificationDate = itemView.findViewById(R.id.notification_date);
            notificationNewPrice = itemView.findViewById(R.id.notification_price);
            notificationNewQuantity = itemView.findViewById(R.id.notification_qte);

            viewBackground = itemView.findViewById(R.id.n_view_background);
            viewForeground = itemView.findViewById(R.id.n_view_foreground);
        }



        @Override
        public void onClick(View view) {

        }



    }
}
