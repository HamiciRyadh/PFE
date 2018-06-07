package com.lfbservices.pfe.model;

/**
* <b> salesPoint est la classe qui represente un point de vente </b>
* <p>
* un point de vente est caracterise par les informations suivantes :
* <ul>
* <li>  Identifiant </li>
* <li> Latitude </li>
* <li> Longitude </li>
* <li> Nom </li>
* <li> Adresse </li>
*<li> Numero de Telephone </li>
*<li> Site Web </li>
*<li> Note d Appreciation  </li>
* </ul>
* </p>
* 
*/
 

public class SalesPoint {
	
	private String salesPointId;
	private double salesPointLat;
    private double salesPointLong;
	private String salesPointName;
	
	
	
	public SalesPoint() {
		
	}
	
	public SalesPoint(String salesPointId, double salesPointLat, double salesPointLong, String salesPointName){
		super();
		this.salesPointId = salesPointId;
		this.salesPointLat = salesPointLat;
		this.salesPointLong = salesPointLong;
		this.salesPointName = salesPointName;
	}
	
	
	public String getSalesPointId() {
		return salesPointId;
	}


	public void setSalesPointId(String salesPointId) {
		this.salesPointId = salesPointId;
	}


	public double getSalesPointLat() {
		return salesPointLat;
	}


	public void setSalesPointLat(double salesPointLat) {
		this.salesPointLat = salesPointLat;
	}


	public double getSalesPointLong() {
		return salesPointLong;
	}


	public void setSalesPointLong(double salesPointLong) {
		this.salesPointLong = salesPointLong;
	}


	public String getSalesPointName() {
		return salesPointName;
	}


	public void setSalesPointName(String salesPointName) {
		this.salesPointName = salesPointName;
	}
	
	
}
