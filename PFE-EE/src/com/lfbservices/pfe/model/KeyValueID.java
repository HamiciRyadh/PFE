package com.lfbservices.pfe.model;

public class KeyValueID {
	private int typeCaracteristicId;
	private String productCaracteristicValue;
	
	
	public KeyValueID() {
		
	}


	public KeyValueID(int typeCaracteristicId, String productCaracteristicValue) {
		super();
		this.typeCaracteristicId = typeCaracteristicId;
		this.productCaracteristicValue = productCaracteristicValue;
	}


	public int getTypeCaracteristicId() {
		return typeCaracteristicId;
	}


	public String getProductCaracteristicValue() {
		return productCaracteristicValue;
	}


	public void setTypeCaracteristicId(int typeCaracteristicId) {
		this.typeCaracteristicId = typeCaracteristicId;
	}


	public void setProductCaracteristicValue(String productCaracteristicValue) {
		this.productCaracteristicValue = productCaracteristicValue;
	}


	
	
	
	

	
	

}
