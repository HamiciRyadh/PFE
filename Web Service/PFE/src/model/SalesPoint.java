package model;

/**
* <b> SalesPoint est la classe qui represente un point de vente </b>
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
	
	private String salespointId;
	private double salespointLat;
    private double salespointLong;
	private  String salespointName;
	private  String salespointAddress;
	private String salespointPhoneNumber;
	private String salespointWebSite; 
	private float salespointRating;
	
	//photo, les horaires d'ouvertures , et les commentaires de clients.
	
	
	public SalesPoint(String salespointId, double salespointLat, double salespointLong, String salespointName,
			String salespointAddress){
		super();
		this.salespointId = salespointId;
		this.salespointLat = salespointLat;
		this.salespointLong = salespointLong;
		this.salespointName = salespointName;
		this.salespointAddress = salespointAddress;

	}
	
	
	public SalesPoint(String salespointId, String salespointPhoneNumber, String salespointWebSite, float salespointRating) {
		super();
		this.salespointId = salespointId;
		this.salespointPhoneNumber = salespointPhoneNumber;
		this.salespointWebSite = salespointWebSite;
		this.salespointRating = salespointRating;
	}

	public String getSalespointId() {
		return salespointId;
	}

	public double getSalespointLat() {
		return salespointLat;
	}

	public double getSalespointLong() {
		return salespointLong;
	}

	public String getSalespointName() {
		return salespointName;
	}

	public String getSalespointAddress() {
		return salespointAddress;
	}

	public String getSalespointPhoneNumber() {
		return salespointPhoneNumber;
	}

	public String getSalespointWebSite() {
		return salespointWebSite;
	}

	public float getSalespointRating() {
		return salespointRating;
	}

	public void setSalespointId(String salespointId) {
		this.salespointId = salespointId;
	}

	public void setSalespointLat(double salespointLat) {
		this.salespointLat = salespointLat;
	}

	public void setSalespointLong(double salespointLong) {
		this.salespointLong = salespointLong;
	}

	public void setSalespointName(String salespointName) {
		this.salespointName = salespointName;
	}

	public void setSalespointAddress(String salespointAddress) {
		this.salespointAddress = salespointAddress;
	}

	public void setSalespointPhoneNumber(String salespointPhoneNumber) {
		this.salespointPhoneNumber = salespointPhoneNumber;
	}

	public void setSalespointWebSite(String salespointWebSite) {
		this.salespointWebSite = salespointWebSite;
	}

	public void setSalespointRating(float salespointRating) {
		this.salespointRating = salespointRating;
	}
	
	
	
	
	
	 

  
	
	

}
