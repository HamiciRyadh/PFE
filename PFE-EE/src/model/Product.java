package model;

/**
* <b> Product est la classe qui represente un produit</b>
* <p>
* un produit est caracterise par les informations suivantes :
* <ul>
* <li>  Identifiant </li>
* <li> Nom </li>
* <li> Type </li>
* <li> Categorie </li>
* <li> Marque </li>
* </ul>
* </p>
* 
*/

public class Product {
	
	private int productId;
	private String productName;
	private int productType;
	private int productCategory;
	private String productTradeMark;
	
	
	public Product() {
		this(0,"",0,0,"");
	}
	
	
	public Product(int productId, String productName, int productType, int productCategory,
			String productTradeMark) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.productCategory = productCategory;
		this.productTradeMark = productTradeMark;
	}


	public int getProductId() {
		return productId;
	}


	public void setProductId(int productId) {
		this.productId = productId;
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


	public int getProductCategory() {
		return productCategory;
	}


	public void setProductCategory(int productCategory) {
		this.productCategory = productCategory;
	}


	public String getProductTradeMark() {
		return productTradeMark;
	}


	public void setProductTradeMark(String productTradeMark) {
		this.productTradeMark = productTradeMark;
	}
	
	



}
