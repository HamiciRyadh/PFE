package usthb.lfbservices.com.pfe.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

@Entity(tableName = "SalesPoint")
public class SalesPoint {

	@PrimaryKey
	@NonNull
	private String salesPointId;
	private double salesPointLat;
	private double salesPointLong;
	private String salesPointName;
	private String salesPointAddress;
	private String salesPointPhoneNumber;
	private String salesPointWebSite;
	private double salesPointRating;
	@ColumnInfo(typeAffinity = ColumnInfo.BLOB)
	private byte[] image;
	@Ignore
	private String salesPointPhotoReference;
	private int salesPointCity;
	
    @Ignore
	public SalesPoint(@NonNull String salesPointId, double salesPointLat, double salesPointLong, String salesPointName,
                      String salesPointAddress, int salesPointCity){
		super();
		this.salesPointId = salesPointId;
		this.salesPointLat = salesPointLat;
		this.salesPointLong = salesPointLong;
		this.salesPointName = salesPointName;
		this.salesPointAddress = salesPointAddress;
		this.salesPointCity = salesPointCity;

	}
	
	@Ignore
	public SalesPoint(@NonNull String salesPointId, String salesPointPhoneNumber, String salesPointWebSite, double salesPointRating,
                      String salesPointPhotoReference) {
		super();
		this.salesPointId = salesPointId;
		this.salesPointPhoneNumber = salesPointPhoneNumber;
		this.salesPointWebSite = salesPointWebSite;
		this.salesPointRating = salesPointRating;
		this.salesPointPhotoReference = salesPointPhotoReference;
	}

	public SalesPoint() {

	}


    public SalesPoint(@NonNull String salesPointId, double salesPointLat, double salesPointLong, String salesPointName,
                      String salesPointAddress, int salesPointCity,
                      String salesPointPhoneNumber, String salesPointWebSite, double salesPointRating,
                      byte[] image){
        super();
        this.salesPointId = salesPointId;
        this.salesPointLat = salesPointLat;
        this.salesPointLong = salesPointLong;
        this.salesPointName = salesPointName;
        this.salesPointAddress = salesPointAddress;
        this.salesPointCity =salesPointCity;

        this.salesPointPhoneNumber = salesPointPhoneNumber;
        this.salesPointWebSite = salesPointWebSite;
        this.salesPointRating = salesPointRating;
        this.image = image;
    }

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getSalesPointPhotoReference() {
		return salesPointPhotoReference;
	}

	public void setSalesPointPhotoReference(String salesPointPhotoReference) {
		this.salesPointPhotoReference = salesPointPhotoReference;
	}

	@NonNull
	public String getSalesPointId() {
		return salesPointId;
	}

	public void setSalesPointId(@NonNull String salesPointId) {
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

	public int getSalesPointCity() {
		return salesPointCity;
	}

	public void setSalesPointCity(int salesPointCity) {
		this.salesPointCity = salesPointCity;
	}

	public LatLng getSalesPointLatLng() {
		return new LatLng(this.salesPointLat, this.salesPointLong);
	}

	@Override
	public String toString() {
		return "ID : " + this.salesPointId;
	}
}
