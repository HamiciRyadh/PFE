package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.sql.Date;

import usthb.lfbservices.com.pfe.utils.DateConverter;

@Entity

public class Notification {
    @PrimaryKey(autoGenerate = true)
    private int notificationId;
    private String salespointId;
    private String productId;
    @TypeConverters({DateConverter.class})
    private Date NotificationDate;
    private int notificationNewQuantity;
    private double notificationNewPrice;

    public Notification() {
    }

    public Notification( String salespointId, String productId,
                         int notificationNewQuantity, double notificationNewPrice, Date notificationDate) {
        this.salespointId = salespointId;
        this.productId = productId;
        NotificationDate = notificationDate;
        this.notificationNewQuantity = notificationNewQuantity;
        this.notificationNewPrice = notificationNewPrice;
    }


    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @NonNull
    public String getSalespointId() {
        return salespointId;
    }

    public void setSalespointId(@NonNull String salespointId) {
        this.salespointId = salespointId;
    }

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public Date getNotificationDate() {
        return NotificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        NotificationDate = notificationDate;
    }

    public int getNotificationNewQuantity() {
        return notificationNewQuantity;
    }

    public void setNotificationNewQuantity(int notificationNewQuantity) {
        this.notificationNewQuantity = notificationNewQuantity;
    }

    public double getNotificationNewPrice() {
        return notificationNewPrice;
    }

    public void setNotificationNewPrice(double notificationNewPrice) {
        this.notificationNewPrice = notificationNewPrice;
    }
}
