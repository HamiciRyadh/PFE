package com.lfbservices.pfe.model;

public class KeyValueID {
	private int typeCharacteristicId;
	private String productCharacteristicValue;
	
	
	public KeyValueID() {
		
	}


	public KeyValueID(int typeCharacteristicId, String productCharacteristicValue) {
		super();
		this.typeCharacteristicId = typeCharacteristicId;
		this.productCharacteristicValue = productCharacteristicValue;
	}


	public int getTypeCharacteristicId() {
		return typeCharacteristicId;
	}


	public String getProductCharacteristicValue() {
		return productCharacteristicValue;
	}


	public void setTypeCharacteristicId(int typeCharacteristicId) {
		this.typeCharacteristicId = typeCharacteristicId;
	}


	public void setProductCaracteristicValue(String productCharacteristicValue) {
		this.productCharacteristicValue = productCharacteristicValue;
	}


	
	
	
	

	
	

}
