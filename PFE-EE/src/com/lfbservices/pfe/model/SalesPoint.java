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
	private String salesPointAddress;
	private String salesPointWilaya;
	private String salesPointPhoneNumber;
	private String salesPointWebSite; 
	private double salesPointRating;
	private String salesPointPhotoReference;
	
	//photo, les horaires d'ouvertures
	
	
	public SalesPoint(String salesPointId, double salesPointLat, double salesPointLong, String salesPointName,
			String salesPointAddress, String salesPointWilaya){
		super();
		this.salesPointId = salesPointId;
		this.salesPointLat = salesPointLat;
		this.salesPointLong = salesPointLong;
		this.salesPointName = salesPointName;
		this.salesPointAddress = salesPointAddress;
		this.salesPointWilaya = salesPointWilaya;

	}
	
	
	public SalesPoint(String salesPointId, String salesPointPhoneNumber, String salesPointWebSite, double salesPointRating,
			String salesPointPhotoReference) {
		super();
		this.salesPointId = salesPointId;
		this.salesPointPhoneNumber = salesPointPhoneNumber;
		this.salesPointWebSite = salesPointWebSite;
		this.salesPointRating = salesPointRating;
		this.salesPointPhotoReference = salesPointPhotoReference;
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


	public String getSalesPointAddress() {
		return salesPointAddress;
	}


	public void setSalesPointAddress(String salesPointAddress) {
		this.salesPointAddress = salesPointAddress;
	}


	public String getSalesPointPhoneNumber() {
		return salesPointPhoneNumber;
	}


	public void setSalesPointPhoneNumber(String salesPointPhoneNumber) {
		this.salesPointPhoneNumber = salesPointPhoneNumber;
	}


	public String getSalesPointWebSite() {
		return salesPointWebSite;
	}


	public void setSalesPointWebSite(String salesPointWebSite) {
		this.salesPointWebSite = salesPointWebSite;
	}


	public double getSalesPointRating() {
		return salesPointRating;
	}


	public void setSalesPointRating(double salesPointRating) {
		this.salesPointRating = salesPointRating;
	}


	public String getSalesPointPhotoReference() {
		return salesPointPhotoReference;
	}


	public void setSalesPointPhotoReference(String salesPointPhotoReference) {
		this.salesPointPhotoReference = salesPointPhotoReference;
	}


	public String getSalesPointWilaya() {
		return salesPointWilaya;
	}


	public void setSalesPointWilaya(String salesPointWilaya) {
		this.salesPointWilaya = salesPointWilaya;
	}
	
	
	
}
