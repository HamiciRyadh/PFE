package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import java.util.Locale;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.DescSalesPointActivity;
import usthb.lfbservices.com.pfe.models.Notification;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.utils.Utils;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder>
        implements ITouchHelperAdapter {

    private Context context;
    private AppRoomDatabase db;
    private List<Notification> notifications;

    /**
     * Adapter constructor
     *
     * @param notifications
     *         A collection of {@link Notification} which will contain the data that will be used in each ViewHolder
     */

    public NotificationListAdapter(List<Notification> notifications) {
        this.notifications = notifications;
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
    @NonNull
    @Override
    public  NotificationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification , parent, false);
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
    public void onBindViewHolder(@NonNull NotificationListAdapter.ViewHolder holder, int position) {
        db= AppRoomDatabase.getInstance(NotificationListAdapter.this.context);
        notifications = new ArrayList<>(db.notificationDao().getAll());

        holder.notificationId = notifications.get(position).getNotificationId();
        holder.notificationSalesPoint.setText(notifications.get(position).getSalesPointName());
        holder.notificationProduct.setText(notifications.get(position).getProductName());

        long now = System.currentTimeMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                notifications.get(position).getNotificationDate().getTime(),
                now,
                DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL);

        holder.notificationNewQuantity.setText(String.valueOf(notifications.get(position).getNotificationNewQuantity()));
        holder.notificationNewPrice.setText(String.format(Locale.getDefault(),"%.2f DA", notifications.get(position).getNotificationNewPrice()));
        //holder.notificationDate.setText(df.format(notifications.get(position).getNotificationDate()));

        holder.notificationDate.setText(ago);
    }


    /**
     * Gets the size of the collection of items in our list
     *
     * @return An Integer representing the size of the collection that will be displayed
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }


    @Override
    public void onItemDismiss(int position) {
        db = AppRoomDatabase.getInstance(NotificationListAdapter.this.context);
        db.notificationDao().deleteById(notifications.get(position).getNotificationId());
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

        /**
         *  Notify any registered observers that the item reflected at fromPosition has been moved to toPosition.
         */

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notificationSalesPoint;
        TextView notificationProduct;
        TextView notificationDate;
        TextView notificationNewQuantity;
        TextView notificationNewPrice;
        RelativeLayout viewBackground, viewForeground;

        int notificationId;

        /**
         * The ViewHolder that will be used to display the data in each item shown
         * in the RecyclerView
         *
         * @param itemViem The layout view group used to display the data
         */

        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

            notificationSalesPoint = itemView.findViewById(R.id.notification_salespoint);
            notificationProduct = itemView.findViewById(R.id.notification_product);
            notificationDate = itemView.findViewById(R.id.notification_date);
            notificationNewPrice = itemView.findViewById(R.id.notification_price);
            notificationNewQuantity = itemView.findViewById(R.id.notification_qte);

            viewBackground = itemView.findViewById(R.id.n_view_background);
            viewForeground = itemView.findViewById(R.id.n_view_foreground);
        }



        @Override
        public void onClick(View view) {
            if (Utils.isNetworkAvailable(context)) {
                for (Notification notification : notifications) {
                    if (notificationId == notification.getNotificationId()) {
                        Intent intent = new Intent(context, DescSalesPointActivity.class);
                        intent.putExtra("salesPointID", notification.getSalesPointId());
                        intent.putExtra("productQuantity", notification.getNotificationNewQuantity());
                        intent.putExtra("productPrice", notification.getNotificationNewPrice());
                        context.startActivity(intent);
                        break;
                    }
                }
            } else {
                Snackbar.make(viewForeground, context.getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
