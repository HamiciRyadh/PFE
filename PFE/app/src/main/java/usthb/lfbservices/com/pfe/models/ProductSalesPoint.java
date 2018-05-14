package usthb.lfbservices.com.pfe.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

@Entity(tableName = "ProductSalesPoint",
		primaryKeys = { "salesPointId", "productBarcode"},
		foreignKeys = {
				@ForeignKey(entity = Product.class,
						parentColumns = "productBarcode",
						childColumns = "productBarcode"),
				@ForeignKey
				(entity = SalesPoint.class,
						parentColumns = "salesPointId",
						childColumns = "salesPointId")
				})


public class ProductSalesPoint {

	@NonNull
	private String productBarcode = "";
	@NonNull
	private String salesPointId = "";
	private int productQuantity;
	private double productPrice ;
	

	@Ignore
    public ProductSalesPoint() {

    }
	
	public ProductSalesPoint(@NonNull String productBarcode,@NonNull  String salesPointId, int productQuantity, double productPrice) {
		super();
		this.productBarcode = productBarcode;
		this.salesPointId = salesPointId;
		this.productQuantity = productQuantity;
		this.productPrice = productPrice;
	}

	@NonNull
	public String getProductBarcode() {
		return productBarcode;
	}

	public void setProductBarcode(@NonNull String productBarcode) {
		this.productBarcode = productBarcode;
	}

	@NonNull
	public String getSalesPointId() {
		return salesPointId;
	}

	public void setSalesPointId(@NonNull String salesPointId) {
		this.salesPointId = salesPointId;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	@NonNull
	@Override
	public String toString() {
		return "ProductBarcode : " + this.productBarcode + "\nSalesPointId : " + this.salesPointId;
	}
}
