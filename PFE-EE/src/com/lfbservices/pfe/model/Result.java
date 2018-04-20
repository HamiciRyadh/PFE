package com.lfbservices.pfe.model;

import java.util.ArrayList;
import java.util.List;

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
	
	
	//List<Product> products ;
	List<SalesPoint> salesPoints;
	List<ProductSalesPoint> productSalesPoints ;
	
	
	public Result(List<SalesPoint> salesPoints, List<ProductSalesPoint> productSalesPoints) {
		super();
		this.salesPoints = salesPoints;
		this.productSalesPoints = productSalesPoints;
	}
	
	
	public Result() {
		this(new ArrayList<SalesPoint>(), new ArrayList<ProductSalesPoint>());
	}



	public List<SalesPoint> getSalesPoints() {
		return salesPoints;
	}


	public void setSalesPoints(List<SalesPoint> salesPoints) {
		this.salesPoints = salesPoints;
	}


	public List<ProductSalesPoint> getProductSalesPoints() {
		return productSalesPoints;
	}


	public void setProductSalesPoints(List<ProductSalesPoint> productSalesPoints) {
		this.productSalesPoints = productSalesPoints;
	}
	
	
    
   
}
