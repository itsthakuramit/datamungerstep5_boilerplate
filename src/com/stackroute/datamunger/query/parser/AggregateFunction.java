package com.stackroute.datamunger.query.parser;

/* This class is used for storing name of field, aggregate function for 
 * each aggregate function
 * */
public class AggregateFunction {
	private String function,field;
	
	public AggregateFunction(String field, String function) {
		super();
		this.function = function;
		this.field = field;
	}
	
	
	public void setFunction(String function) {
		this.function = function;
	}
	

	public void setField(String field) {
		this.field = field;
	}

	
	public String getFunction() {
		return function;
	}

	
	public String getField() {
		return field;
	}	

}