package usthb.lfbservices.com.pfe.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "Product")
public class Product implements Parcelable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    public Product(Parcel in) {
        this.productBarcode = in.readString();
        this.productName = in.readString();
        this.productType = in.readInt();
        this.productTradeMark = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productBarcode);
        dest.writeString(this.productName);
        dest.writeInt(this.productType);
        dest.writeString(this.productTradeMark);
    }
}