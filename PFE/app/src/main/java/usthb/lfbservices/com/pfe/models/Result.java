package usthb.lfbservices.com.pfe.models;

import java.util.List;


public class Result {
	
	
	private List<SalesPoint> salesPoints;
	private List<ProductSalesPoint> productSalesPoints ;
	
	
    public Result() {
    	this(null,null);
    }
	
	public Result(List<SalesPoint> salesPoints, List<ProductSalesPoint> productSalesPoints) {
		super();
		this.salesPoints = salesPoints;
		this.productSalesPoints = productSalesPoints;
	}
	
	
	public List<SalesPoint> getSalesPoints() {
		return salesPoints;
	}
	public List<ProductSalesPoint> getProductSalesPoints() {
		return productSalesPoints;
	}
	public void setSalesPoints( List<SalesPoint> salesPoints) {
		salesPoints = salesPoints;
	}
	public void setProductSalesPoints( List<ProductSalesPoint> productSalesPoints) {
		this.productSalesPoints = productSalesPoints;
	}

   
}
