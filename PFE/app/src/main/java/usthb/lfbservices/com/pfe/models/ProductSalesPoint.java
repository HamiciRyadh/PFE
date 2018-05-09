package usthb.lfbservices.com.pfe.models;



public class ProductSalesPoint {

	private String productBarcode;
	private String salesPointId;
	private int productQuantity;
	private double productPrice ;
	
	public ProductSalesPoint() {
		this("","",0,0.0);
	}
	
	public ProductSalesPoint(String productBarcode, String salesPointId, int productQuantity, double productPrice) {
		super();
		this.productBarcode = productBarcode;
		this.salesPointId = salesPointId;
		this.productQuantity = productQuantity;
		this.productPrice = productPrice;
	}


	public String getProductBarcode() {
		return productBarcode;
	}

	public void setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
	}

	public String getSalespointId() {
		return salesPointId;
	}

	public void setSalespointId(String salespointId) {
		this.salesPointId = salespointId;
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


	@Override
	public String toString() {
		return "ProductBarcode : " + this.productBarcode + "\nSalesPointId : " + this.salesPointId;
	}
}
