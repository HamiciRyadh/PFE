package model;

import java.util.ArrayList;

/**
* <b> Result est la classe qui represente le resultat retroune suite a une recherche </b>
* <p>
* Le resultat est caracterise par les informations suivantes :
* <ul>
* <li> La liste des produits correspondant a la recherche  </li>
* <li> La liste des points de vente ou le produit est commercialise </li>
* <li> La liste qui donne pour chaque point de vente , la quantite et le prix du produit </li>
* </ul>
* </p>
*
*/


public class Result {
	
	
	ArrayList<Product> products ;
	ArrayList<SalesPoint> SalesPoints;
	ArrayList<ProductSalesPoint> productSalesPoints ;
	
	
    public Result() {
    	this(null,null,null);
    }
	
	public Result(ArrayList<Product> products, ArrayList<SalesPoint> salesPoints,
			ArrayList<ProductSalesPoint> productSalesPoints) {
		super();
		this.products = products;
		SalesPoints = salesPoints;
		this.productSalesPoints = productSalesPoints;
	}
	
	
	public ArrayList<Product> getProducts() {
		return products;
	}
	public ArrayList<SalesPoint> getSalesPoints() {
		return SalesPoints;
	}
	public ArrayList<ProductSalesPoint> getProductSalesPoints() {
		return productSalesPoints;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	public void setSalesPoints(ArrayList<SalesPoint> salesPoints) {
		SalesPoints = salesPoints;
	}
	public void setProductSalesPoints(ArrayList<ProductSalesPoint> productSalesPoints) {
		this.productSalesPoints = productSalesPoints;
	}

   
}
