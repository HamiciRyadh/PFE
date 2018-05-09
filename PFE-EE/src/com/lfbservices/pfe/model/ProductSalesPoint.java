package com.lfbservices.pfe.model;


/**
* <b> ProductSalesPoint est la classe qui represente les produits ainsi que leur points de vente </b>
* <p>
* ProductsSalesPoint  est caracterise par les informations suivantes :
* <ul>
* <li>  Identifiant  du Produit</li>
* <li> Identifiant du Point de vente </li>
* <li> Quantité disponible  </li>
* <li> Prix du produit </li>
* </ul>
* </p>
* 
*/

public class ProductSalesPoint {

	private String productBarcode;
	private String salesPointId;
	private  int productQuantity;
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
	public String getSalesPointId() {
		return salesPointId;
	}
	public int getProductQuantity() {
		return productQuantity;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
	}
	public void setSalesPointId(String salesPointId) {
		this.salesPointId = salesPointId;
	}
	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	
	
}
