package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

@Entity(foreignKeys = {@ForeignKey
                        (entity = Product.class,
                        parentColumns = "productBarcode",
                        childColumns = "productBarcode"
                        ),

                      @ForeignKey
                       (entity = TypeCaracteristic.class,
                       parentColumns = "typeCaracteristicId",
                       childColumns = "typeCaracteristicId"
                       )},

        primaryKeys = { "typeCaracteristicId", "productBarcode" })

public class ProductCaracteristic {


    private int typeCaracteristicId;
    @NonNull
    private String productBarcode = "";
    @NonNull
    private String productCaracteristicValue = "";


    public ProductCaracteristic() {
    }

    public ProductCaracteristic(int typeCaracteristicId,@NonNull String productBarcode,@NonNull String productCaracteristicValue) {
        this.typeCaracteristicId = typeCaracteristicId;
        this.productBarcode = productBarcode;
        this.productCaracteristicValue = productCaracteristicValue;
    }

    public int getTypeCaracteristicId() {
        return typeCaracteristicId;
    }

    public void setTypeCaracteristicId(int typeCaracteristicId) {
        this.typeCaracteristicId = typeCaracteristicId;
    }

    @NonNull
    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NonNull
    public String getProductCaracteristicValue() {
        return productCaracteristicValue;
    }

    public void setProductCaracteristicValue(@NonNull String productCaracteristicValue) {
        this.productCaracteristicValue = productCaracteristicValue;
    }

    @Override
    public String toString() {
        return "ProductCaracteristic{" +
                "typeCaracteristicId=" + typeCaracteristicId +
                ", productBarcode='" + productBarcode + '\'' +
                ", productCaracteristicValue='" + productCaracteristicValue + '\'' +
                '}';
    }
}

