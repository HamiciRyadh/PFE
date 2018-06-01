package usthb.lfbservices.com.pfe.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {@ForeignKey
                        (entity = Product.class,
                        parentColumns = "productBarcode",
                        childColumns = "productBarcode", onDelete = CASCADE
                        ),

                      @ForeignKey
                       (entity = TypeCharacteristic.class,
                       parentColumns = "typeCharacteristicId",
                       childColumns = "typeCharacteristicId", onDelete = CASCADE
                       )},

        primaryKeys = { "typeCharacteristicId", "productBarcode" })

public class ProductCharacteristic {


    private int typeCharacteristicId;
    @NonNull
    private String productBarcode = "";
    @NonNull
    private String productCharacteristicValue = "";


    public ProductCharacteristic() {
    }

    public ProductCharacteristic(int typeCharacteristicId, @NonNull String productBarcode, @NonNull String productCharacteristicValue) {
        this.typeCharacteristicId = typeCharacteristicId;
        this.productBarcode = productBarcode;
        this.productCharacteristicValue = productCharacteristicValue;
    }

    public int getTypeCharacteristicId() {
        return typeCharacteristicId;
    }

    public void setTypeCharacteristicId(int typeCharacteristicId) {
        this.typeCharacteristicId = typeCharacteristicId;
    }

    @NonNull
    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    @NonNull
    public String getProductCharacteristicValue() {
        return productCharacteristicValue;
    }

    public void setProductCharacteristicValue(@NonNull String productCharacteristicValue) {
        this.productCharacteristicValue = productCharacteristicValue;
    }

    @Override
    public String toString() {
        return "ProductCharacteristic{" +
                "typeCharacteristicId=" + typeCharacteristicId +
                ", productBarcode='" + productBarcode + '\'' +
                ", productCharacteristicValue='" + productCharacteristicValue + '\'' +
                '}';
    }
}

