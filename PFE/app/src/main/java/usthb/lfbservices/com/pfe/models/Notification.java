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
    private String salesPointId;
    private String productBarcode;
    @TypeConverters({DateConverter.class})
    private Date NotificationDate;
    private int notificationNewQuantity;
    private double notificationNewPrice;
    private String productName;
    private String salesPointName;

    public Notification() {
    }

    public Notification(String salesPointId, String productBarcode, Date notificationDate, int notificationNewQuantity, double notificationNewPrice, String productName, String salesPointName) {
        this.salesPointId = salesPointId;
        this.productBarcode = productBarcode;
        NotificationDate = notificationDate;
        this.notificationNewQuantity = notificationNewQuantity;
        this.notificationNewPrice = notificationNewPrice;
        this.productName = productName;
        this.salesPointName = salesPointName;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    @NonNull
    public String getSalesPointId() {
        return salesPointId;
    }

    public void setSalesPointId(@NonNull String salesPointId) {
        this.salesPointId = salesPointId;
    }

    @NonNull
    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(@NonNull String productBarcode) {
        this.productBarcode = productBarcode;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSalesPointName() {
        return salesPointName;
    }

    public void setSalesPointName(String salesPointName) {
        this.salesPointName = salesPointName;
    }
}
