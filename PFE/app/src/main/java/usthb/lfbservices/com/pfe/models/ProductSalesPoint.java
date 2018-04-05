package usthb.lfbservices.com.pfe.models;



public class ProductSalesPoint {

	private int productId;
	private String salesPointId;
	private  int productQuantity;
	private double productPrice ;
	
	public ProductSalesPoint() {
		this(0,"",0,0.0);
	}
	
	public ProductSalesPoint(int productId, String salesPointId, int productQuantity, double productPrice) {
		super();
		this.productId = productId;
		this.salesPointId = salesPointId;
		this.productQuantity = productQuantity;
		this.productPrice = productPrice;
	}


	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
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
		return "ProductId : " + this.productId + "\nSalesPointId : " + this.salesPointId;
	}
}
