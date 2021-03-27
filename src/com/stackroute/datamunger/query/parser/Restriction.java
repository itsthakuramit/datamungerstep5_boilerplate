package com.stackroute.datamunger.query.parser;

/*
 * This class is used for storing name of field, condition and value for 
 * each conditions
 * */
public class Restriction {
	private String propertyName;
	private String propertyValue;
	private String condition;
	
	
	public Restriction(String propertyName, String propertyValue, String condition) {
		super();
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.condition = condition;
	}
	
	
	public String getPropertyName() {
		return propertyName;
	}
	
	
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	
	public String getPropertyValue() {
		return propertyValue;
	}
	
	
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	
	public String getCondition() {
		return condition;
	}
	
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	
}