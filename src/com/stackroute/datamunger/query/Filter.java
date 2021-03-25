package com.stackroute.datamunger.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.stackroute.datamunger.query.parser.Restriction;

//This class contains methods to evaluate expressions
public class Filter {
	
	/* 
	 * The evaluateExpression() method of this class is responsible for evaluating 
	 * the expressions mentioned in the query. It has to be noted that the process 
	 * of evaluating expressions will be different for different data types. there 
	 * are 6 operators that can exist within a query i.e. >=,<=,<,>,!=,= This method 
	 * should be able to evaluate all of them. 
	 * Note: while evaluating string expressions, please handle uppercase and lowercase 
	 * 
	 */
	
	public boolean evaluateExpression(Restriction restriction, String fieldValue, String dataType) {
		
		if(restriction.getCondition().equals("="))
			return isEqual(fieldValue, restriction.getPropertyValue(), dataType);
		
		else if(restriction.getCondition().matches("!="))
			return isNotEqual(fieldValue, restriction.getPropertyValue(), dataType);
		
		else if(restriction.getCondition().equals(">"))
			return isGreaterThan(fieldValue, restriction.getPropertyValue(), dataType);
		
		else if(restriction.getCondition().equals(">="))
			return isGreaterThanOrEqualTo(fieldValue, restriction.getPropertyValue(), dataType);
		
		else if(restriction.getCondition().equals("<"))
			return isLessThan(fieldValue, restriction.getPropertyValue(), dataType);
		
		else
			return isLessThanOrEqualTo(fieldValue, restriction.getPropertyValue(), dataType);
	}
	
	
	
	private String getDateFormat(String date) {
		String format = "";
		if(date.matches("^[0-9]{2}/[0-9]{2}/[0-9]{4}$"))
			format = "dd/mm/yyyy";
		
		else if(date.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
			format = "yyyy-mm-dd";
		
		else if(date.matches("^[0-9]{2}-[a-z]{3}-[0-9]{2}$"))
			format = "dd-mon-yy";
		
		else if(date.matches("^[0-9]{2}-[a-z]{3}-[0-9]{4}$"))
			format ="dd-mon-yyyy";
		
		else if(date.matches("^[0-9]{2}-[a-z]{3,9}-[0-9]{2}$"))
			format = "dd-month-yy";
		
		else 
			format ="dd-month-yyyy";
		
		return format;
	}
	
	
	
	//Method containing implementation of equalTo operator
	private boolean isEqual(String s1, String s2, String dataType) {
		if(dataType.equals("java.lang.Integer")) {
			return Integer.parseInt(s1)==Integer.parseInt(s2);
		}
		else if(dataType.equals("java.lang.Double")) {
			return Double.parseDouble(s1)==Double.parseDouble(s2);
		}
		else if(dataType.equals("java.util.Date")) {
			 SimpleDateFormat formatter = new SimpleDateFormat(getDateFormat(s1));
			 try {
				if(formatter.parse(s1).compareTo(formatter.parse(s2))==0)
					 return true;
				 else
					 return false;
			}
			 catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		else if(dataType.equals("java.util.Object"))
			return false;
		
		else {
			if(s1.compareTo(s2)==0)
				return true;
			else
				return false;
		}
	}
	
	
	
	
	
	//Method containing implementation of notEqualTo operator
	private boolean isNotEqual(String s1, String s2, String dataType) {
		return !isEqual(s1, s2, dataType);
	}
	
	
	
	
	
	//Method containing implementation of greaterThan operator
	private boolean isGreaterThan(String s1, String s2, String dataType) {
		if(dataType.equals("java.lang.Integer")) {
			return Integer.parseInt(s1)>Integer.parseInt(s2);
		}
		else if(dataType.equals("java.lang.Double")) {
			return Double.parseDouble(s1.toLowerCase())>Double.parseDouble(s2.toLowerCase());
		}
		else if(dataType.equals("java.util.Date")) {
			 SimpleDateFormat formatter = new SimpleDateFormat(getDateFormat(s1));
			 try {
				if(formatter.parse(s1).compareTo(formatter.parse(s2))>0)
					 return true;
				 else
					 return false;
			}
			catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}
		else if(dataType.equals("java.util.Object"))
			return false;
		
		else {
			if(s1.compareTo(s2)>0)
				return true;
			else
				return false;
		}
	}
	
	
	
	
	
	
	//Method containing implementation of greaterThanOrEqualTo operator
	private boolean isGreaterThanOrEqualTo(String s1, String s2, String dataType) {
		return isEqual(s1, s2, dataType)|isGreaterThan(s1, s2, dataType);
	}
	
	
	
	
	
	//Method containing implementation of lessThan operator
	private boolean isLessThan(String s1, String s2, String dataType) {
		return !isGreaterThanOrEqualTo(s1, s2, dataType);
	}
	
	
	
	
	//Method containing implementation of lessThanOrEqualTo operator
	private boolean isLessThanOrEqualTo(String s1, String s2, String dataType) {
		return isEqual(s1, s2, dataType)|isLessThan(s1, s2, dataType);
	}
	

	
	
	//Method containing implementation of equalTo operator
	
	
	
	
	
	//Method containing implementation of notEqualTo operator
	
	
	
	
	
	
	
	//Method containing implementation of greaterThan operator
	
	
	
	
	
	
	
	//Method containing implementation of greaterThanOrEqualTo operator
	
	
	
	
	
	
	//Method containing implementation of lessThan operator
	  
	
	
	
	
	//Method containing implementation of lessThanOrEqualTo operator
	
}