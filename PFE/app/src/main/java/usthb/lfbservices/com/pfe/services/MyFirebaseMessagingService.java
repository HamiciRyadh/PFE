package usthb.lfbservices.com.pfe.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.activities.DescSalesPointActivity;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.models.Notification;
import usthb.lfbservices.com.pfe.utils.Constants;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private String productName;
    private Notification notification;

    /**
     * Called when a message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_USER_PREFERENCES, MODE_PRIVATE);
        final String notifications = sharedPreferences.getString(Constants.NOTIFICATIONS, null);
        if (notifications != null && notifications.equalsIgnoreCase(Constants.NOTIFICATIONS_OFF)) return;
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            AppRoomDatabase db = AppRoomDatabase.getInstance(MyFirebaseMessagingService.this);

            List<String> list = new ArrayList<>(remoteMessage.getData().values());

            for (String string : list) {
                Log.e(TAG, string);
            }

            Date currentTime = Calendar.getInstance().getTime();
            java.sql.Date sDate = new java.sql.Date(currentTime.getTime());

            productName = list.get(0);
            final String productBarcode = list.get(3);
            final String salesPointId = list.get(6);
            final String salesPointName = list.get(7);
            final int productQuantityNew = Integer.parseInt(list.get(4));
            final double productPriceNew = Double.parseDouble(list.get(1));
            final int productQuantityOld = Integer.parseInt(list.get(5));
            //final double productPriceOld = Double.parseDouble(list.get(2));
            notification = new Notification(salesPointId, productBarcode, sDate, productQuantityNew, productPriceNew, productName, salesPointName);
            db.notificationDao().insertAll(notification);
            if (db.productSalesPointDao().exists(productBarcode, salesPointId)) {
                db.productSalesPointDao().update(new ProductSalesPoint(salesPointId, productBarcode, productQuantityNew, productPriceNew));
            }
            String messageBody;
            if (productQuantityNew == 0 && productQuantityOld != 0) {
                messageBody = "Il n'est plus disponible chez "+ salesPointName+".";
            } else if (productQuantityOld == 0 && productQuantityNew != 0) {
                messageBody = "Il est de nouveau disponible chez "+ salesPointName+".";
            } else {
                messageBody = "Quantité : " + productQuantityNew + "   -   "+ "Prix : " + String.format(Locale.getDefault(),"%.2f DA", productPriceNew);
            }
            sendNotification(messageBody);
        }
    }

    /**
     * Create and show a notification containing the received FCM message.
     * @param messageBody FCM message body received.
     */
    private void sendNotification(final String messageBody) {
        Intent intent = new Intent(this, DescSalesPointActivity.class);
        intent.putExtra("salesPointID", notification.getSalesPointId());
        intent.putExtra("productQuantity", notification.getNotificationNewQuantity());
        intent.putExtra("productPrice", notification.getNotificationNewPrice());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.map)
                        .setContentTitle("Du nouveau ! " + productName)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }

        if (notificationManager != null) notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
