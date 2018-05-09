package com.lfbservices.pfe.model;

public class KeyValue {
	private String key;
	private String value;

	
	public KeyValue(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	public KeyValue() {
		
	}
	
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
