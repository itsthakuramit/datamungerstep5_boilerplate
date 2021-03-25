package com.stackroute.datamunger.query.parser;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {
	
	
	private QueryParameter queryParameter = new QueryParameter();
	/*
	 * This method will parse the queryString and will return the object of
	 * QueryParameter class
	 */
	public QueryParameter parseQuery(String queryString) {
	
		queryParameter.setFileName(FileName(queryString));
		queryParameter.setBaseQuery(BaseQuery(queryString));
		queryParameter.setOrderByFields(OrderByFields(queryString));
		queryParameter.setGroupByFields(GroupByFields(queryString));
		queryParameter.setFields(Fields(queryString));
		queryParameter.setLogicalOperators(LogicalOperators(queryString));
		queryParameter.setAggregateFunction(AggregateFunction(queryString));
		queryParameter.setRestrictions(Restrictions(queryString));
		
		return queryParameter;
		
	}
	
	
		public String BaseQuery(String queryString){
		
				String arr[]= queryString.split(" where | group by ");
				String str=arr[0].trim();
				return str;
			}
	
	
		/*
		 * extract the name of the file from the query. File name can be found after the
		 * "from" clause.
		 */
		public String FileName(String queryString){
		
				String[] arr = queryString.split("from");
				String str=arr[1].toLowerCase().trim();
				String[] arr1= str.split(" ");
				String str1= arr1[0];
				
				return str1;
			}
		
		
		/*
		 * extract the order by fields from the query string. Please note that we will
		 * need to extract the field(s) after "order by" clause in the query, if at all
		 * the order by clause exists. For eg: select city,winner,team1,team2 from
		 * data/ipl.csv order by city from the query mentioned above, we need to extract
		 * "city". Please note that we can have more than one order by fields.
		 */
		public List<String> OrderByFields(String queryString) {
			
			String[] arr= new String[] {};
			String str="";
			List<String> splitList = new ArrayList<String>();
			
			if(queryString.contains("order by"))
				{
				arr=queryString.split("order by");
				str=arr[1].trim();
					if(str.equals(","))
					{
						arr=str.split(",");
					}
					else 
					{
						arr=str.split("\n");
				    }
					for(int i=0; i<arr.length; i++)
					{
						splitList.add(arr[i].trim());
					}
				}
			else
				
					arr=null;
			
			return splitList;
		}
		
		
		/*
		 * extract the group by fields from the query string. Please note that we will
		 * need to extract the field(s) after "group by" clause in the query, if at all
		 * the group by clause exists. For eg: select city,max(win_by_runs) from
		 * data/ipl.csv group by city from the query mentioned above, we need to extract
		 * "city". Please note that we can have more than one group by fields.
		 */	
		public List<String> GroupByFields(String queryString)
		{
			String[] arr= new String[] {};
			String str="";
			List<String> splitList = new ArrayList<String>();
			
			if(queryString.contains("group by"))
				{
				arr=queryString.split(" group by | order by ");
				str=arr[1].trim();
					if(str.equals(","))
					{
						arr=str.split(",");
					}
					else 
					{
						arr=str.split("\n");
				    }
					for(int i=0; i<arr.length; i++)
					{
						splitList.add(arr[i].trim());
					}
				}
			else
					arr=null;
		
			return splitList;
		}
		
			
		/*
		 * extract the selected fields from the query string. Please note that we will
		 * need to extract the field(s) after "select" clause followed by a space from
		 * the query string. For eg: select city,win_by_runs from data/ipl.csv from the
		 * query mentioned above, we need to extract "city" and "win_by_runs". Please
		 * note that we might have a field containing name "from_date" or "from_hrs".
		 * Hence, consider this while parsing.
		 */
		public ArrayList<String> Fields(String queryString) {
			String strSelect = queryString.toLowerCase().split("select")[1].trim();
			String str = strSelect.split("from")[0].trim();
			String[] arr2 = new String[] {};
			ArrayList<String> splitList = new ArrayList<String>();

			if (str.contains(",")) {
				arr2 = str.split(",");

				for (int i = 0; i < arr2.length; i++) {
					splitList.add(arr2[i].trim());
				}
				return splitList;
			} else
			{
				splitList.add(str);
				return splitList;
			}
		}
		
		
		/*
		 * extract the conditions from the query string(if exists). for each condition,
		 * we need to capture the following: 
		 * 1. Name of field 
		 * 2. condition 
		 * 3. value
		 * 
		 * For eg: select city,winner,team1,team2,player_of_match from data/ipl.csv
		 * where season >= 2008 or toss_decision != bat
		 * 
		 * here, for the first condition, "season>=2008" we need to capture: 
		 * 1. Name of field: season 
		 * 2. condition: >= 
		 * 3. value: 2008
		 * 
		 * the query might contain multiple conditions separated by OR/AND operators.
		 * Please consider this while parsing the conditions.
		 * 
		 */
		public List<Restriction> Restrictions(String queryString) {
			List<Restriction> list = new ArrayList<Restriction>();
			if (queryString.contains("where")) {
				String[] trimFrom = queryString.split("where ");
				queryString = trimFrom[1];
				String[] trimOrderby = queryString.split(" order by | group by ");
				queryString = trimOrderby[0];
				String[] finalStr = { queryString };
				if (queryString.contains("and") || queryString.contains("or"))
					;
				{

					finalStr = queryString.split(" and | or ");

					for (int i = 0; i < finalStr.length; i++) {
						if (finalStr[i].contains("=")) {
							String[] tokens1 = finalStr[i].split("=");
							Restriction res1 = new Restriction(tokens1[0].trim().replaceAll("'", ""),
									tokens1[1].trim().replaceAll("'", ""), "=");

							list.add(res1);

						} else {
							String[] tokens1 = finalStr[i].split(" ");
							Restriction res1 = new Restriction(tokens1[0].trim().replaceAll("'", ""),
									tokens1[2].trim().replaceAll("'", ""), tokens1[1].trim().replaceAll("'", ""));

							list.add(res1);
						}

					}

				}

				return list;
			} else
				return null;
		}
		
		
		/*
		 * extract the logical operators(AND/OR) from the query, if at all it is
		 * present. For eg: select city,winner,team1,team2,player_of_match from
		 * data/ipl.csv where season >= 2008 or toss_decision != bat and city =
		 * bangalore
		 * 
		 * the query mentioned above in the example should return a List of Strings
		 * containing [or,and]
		 */
		public List<String> LogicalOperators(String queryString) {
			if (queryString.contains("where")) {
				List<String> finalResList = new ArrayList<String>();
				String[] trimFrom = queryString.split("where ");
				queryString = trimFrom[1];
				String[] splitOrderby = queryString.split(" order by | group by ");
				queryString = splitOrderby[0];

				String[] strArr = queryString.split(" ");
				for (int i = 0; i < strArr.length; i++) {
					if (strArr[i].equals("and")) {
						finalResList.add("and");
					} else if (strArr[i].equals("or")) {
						finalResList.add("or");
					}
				}
				return finalResList;
			} else
				return null;
		}
		
		
		/*
		 * extract the aggregate functions from the query. The presence of the aggregate
		 * functions can determined if we have either "min" or "max" or "sum" or "count"
		 * or "avg" followed by opening braces"(" after "select" clause in the query
		 * string. in case it is present, then we will have to extract the same. For
		 * each aggregate functions, we need to know the following: 
		 * 1. type of aggregate function(min/max/count/sum/avg) 
		 * 2. field on which the aggregate function is being applied
		 * 
		 * Please note that more than one aggregate function can be present in a query
		 * 
		 * 
		 */
		public List<AggregateFunction> AggregateFunction(String queryString) {
			List<AggregateFunction> aggregateList = new ArrayList<AggregateFunction>();

			String[] arr = queryString.split("select ");
			String[] arr0 = arr[1].split(" from");
			String[] arr1 = arr0[0].split(",");
			for (int i = 0; i < arr1.length; i++) {

				if (arr1[i].contains("sum(") || arr1[i].contains("count(") || arr1[i].contains("min(")
						|| arr1[i].contains("max(") || arr1[i].contains("avg(")) {
					String[] trimLeft = arr1[i].split("\\(");

					if (trimLeft[0].contains("sum")) {
						String[] trimRight = trimLeft[1].split("\\)");
						AggregateFunction obj = new AggregateFunction(trimRight[0], "sum");
						aggregateList.add(obj);
					} else if (trimLeft[0].contains("count")) {
						String[] trimRight = trimLeft[1].split("\\)");
						AggregateFunction obj = new AggregateFunction(trimRight[0], "count");
						aggregateList.add(obj);

					} else if (trimLeft[0].contains("min")) {
						String[] trimRight = trimLeft[1].split("\\)");
						AggregateFunction obj = new AggregateFunction(trimRight[0], "min");
						aggregateList.add(obj);

					} else if (trimLeft[0].contains("max")) {
						String[] trimRight = trimLeft[1].split("\\)");
						AggregateFunction obj = new AggregateFunction(trimRight[0], "max");
						aggregateList.add(obj);

					} else if (trimLeft[0].contains("avg")) {
						String[] trimRight = trimLeft[1].split("\\)");
						AggregateFunction obj = new AggregateFunction(trimRight[0], "avg");
						aggregateList.add(obj);
					}
				}

			}

			return aggregateList;
		}

	
	
}
