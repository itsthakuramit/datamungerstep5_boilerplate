package com.stackroute.datamunger.query.parser;

import java.util.List;
/* 
 * This class will contain the elements of the parsed Query String such as conditions,
 * logical operators,aggregate functions, file name, fields group by fields, order by
 * fields, Query Type
 * */

	public class QueryParameter {
		
		private String fileName, baseQuery;
		private List<String> orderByFields;
		private List<String> groupByFields;
		private List<String> fields;
		private List<String> logicalOperators;
		private List<AggregateFunction> aggregateFunctions;
		private List<Restriction> restrictions;

		
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}

		
		
		
		public void setBaseQuery(String baseQuery) {
			this.baseQuery = baseQuery;
		}

		public String getBaseQuery() {
			return baseQuery;
		}


		
		
		public void setOrderByFields(List<String> orderByFields){
			this.orderByFields=orderByFields;
		}
		
		public List<String> getOrderByFields() {
			return orderByFields;
		}

		
		
		
		public void setGroupByFields(List<String> groupByFields){
			this.groupByFields=groupByFields;
		}
		
		public List<String> getGroupByFields() {
			return groupByFields;
		}

		
		
		
		public void setFields(List<String> fields) {
			this.fields=fields;
		}
		
		public List<String> getFields() {
			return fields;
		}

		
		
		public void setLogicalOperators(List<String> logicalOperators) {
			this.logicalOperators=logicalOperators;
		}
		
		public List<String> getLogicalOperators() {
			return logicalOperators;
		}

		
		
		
		
		public void setAggregateFunction(List<AggregateFunction> aggregateFunctions) {
			this.aggregateFunctions=aggregateFunctions;
		}
		
		public List<AggregateFunction> getAggregateFunctions() {
			return aggregateFunctions;
		}
		
		
		
		
		public void setRestrictions(List<Restriction> restrictions) {
			this.restrictions=restrictions;
		}
		
		public List<Restriction> getRestrictions() {
			return restrictions;
		}
		
		
		
		public String getQUERY_TYPE() {
			return null;
		}

	
}
