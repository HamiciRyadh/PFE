package usthb.lfbservices.com.pfe.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Product")
public class Product {


    @NonNull
    @PrimaryKey
    private String productBarcode;
    private String productName;
    private int productType;
    private String productTradeMark;

    @Ignore
    public Product() {
        this("Unknown","Unknown",0,"Unknown");
    }


    public Product(@NonNull String productBarcode, String productName, int productType, String productTradeMark) {
        super();
        this.productBarcode = productBarcode;
        this.productName = productName;
        this.productType = productType;
        this.productTradeMark = productTradeMark;
    }



    @NonNull
    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }


    public String getProductTradeMark() {
        return productTradeMark;
    }

    public void setProductTradeMark(String productTradeMark) {
        this.productTradeMark = productTradeMark;
    }



}