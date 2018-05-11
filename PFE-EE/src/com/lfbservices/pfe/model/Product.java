package com.lfbservices.pfe.model;


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
	
	private String productBarcode;
	private String productName;
	private int productType;
	private String productTradeMark;
	
	
	public Product() {
		this("","",0,"");
	}
	
	
	public Product(String productBarcode, String productName, int productType, String productTradeMark) {
		super();
		this.productBarcode = productBarcode;
		this.productName = productName;
		this.productType = productType;
		this.productTradeMark = productTradeMark;
	}
	
	
	/*


	public Product(String productId, String productName, int productType, int productCategory, String productTradeMark,
			Map<String, String> caracteristics) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.productCategory = productCategory;
		this.productTradeMark = productTradeMark;
		this.productCaracteristics = caracteristics;
	}


	public Map<String, String> getCaracteristics() {
		return productCaracteristics;
	}


	public void setCaracteristics(Map<String, String> caracteristics) {
		this.caracteristics = caracteristics;
	}
*/

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
