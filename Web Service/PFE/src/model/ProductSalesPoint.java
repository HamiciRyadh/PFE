package model;


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

	private int productId;
	private String salespointId;
	private  int productQuantity;
	private double productPrice ;
	
	public ProductSalesPoint() {
		this(0,"",0,0.0);
	}
	
	public ProductSalesPoint(int productId, String salespointId, int productQuantity, double productPrice) {
		super();
		this.productId = productId;
		this.salespointId = salespointId;
		this.productQuantity = productQuantity;
		this.productPrice = productPrice;
	}
	public int getProductId() {
		return productId;
	}
	public String getSalespointId() {
		return salespointId;
	}
	public int getproductQuantity() {
		return productQuantity;
	}
	public double getproductPrice() {
		return productPrice;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public void setSalespointId(String salespointId) {
		this.salespointId = salespointId;
	}
	public void setproductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}
	public void setproductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	
	
}
