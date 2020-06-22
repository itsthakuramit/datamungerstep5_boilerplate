package com.stackroute.datamunger.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.stackroute.datamunger.query.Query;
import com.stackroute.datamunger.query.parser.AggregateFunction;
import com.stackroute.datamunger.query.parser.QueryParameter;
import com.stackroute.datamunger.query.parser.QueryParser;
import com.stackroute.datamunger.query.parser.Restriction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataMungerTest {

	private static Query query;

	private static QueryParser queryParser;
	private static QueryParameter queryParameter;
	private String queryString;

	@Before
	public void setup() {
		// setup methods runs, before every test case runs
		// This method is used to initialize the required variables
		query = new Query();
		queryParser = new QueryParser();

	}

	@After
	public void teardown() {
		// teardown method runs, after every test case run
		// This method is used to clear the initialized variables
		query = null;
		queryParser = null;

	}

	/*
	 * The following test cases are used to check whether the parsing is working
	 * properly or not
	 */

	@Test
	public void testGetFileName() {
		queryString = "select * from data/ipl.csv";
		queryParameter = queryParser.parseQuery(queryString);
		assertEquals(
				"testGetFileName(): File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());

	}

	@Test
	public void testGetFileNameFailure() {
		queryString = "select * from data/ipl1.csv";
		queryParameter = queryParser.parseQuery(queryString);
		assertNotEquals(
				"testGetFileNameFailure(): File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
	}

	@Test
	public void testGetFields() {
		queryString = "select city, winner, team1,team2 from data/ipl.csv";
		queryParameter = queryParser.parseQuery(queryString);
		List<String> expectedFields = new ArrayList<>();
		expectedFields.add("city");
		expectedFields.add("winner");
		expectedFields.add("team1");
		expectedFields.add("team2");
		assertArrayEquals(
				"testGetFields() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				expectedFields.toArray(), queryParameter.getFields().toArray());
	}

	@Test
	public void testGetFieldsFailure() {
		queryString = "select city, winner, team1,team2 from data/ipl.csv";
		queryParameter = queryParser.parseQuery(queryString);
		assertNotNull(
				"testGetFieldsFailure() : Invalid Column / Field values. Please note that the query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				queryParameter.getFields());
	}

	@Test
	public void testGetFieldsAndRestrictions() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();
		List<String> fields = new ArrayList<String>();
		fields.add("city");
		fields.add("winner");
		fields.add("player_of_match");

		Boolean status = false;
		if (restrictions.get(0).getPropertyName().contains("season")
				&& restrictions.get(0).getPropertyValue().contains("2014")
				&& restrictions.get(0).getCondition().contains(">")) {
			status = true;
		}

		assertEquals(
				"testGetFieldsAndRestrictions() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetFieldsAndRestrictions() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetFieldsAndRestrictions() : Retrieval of Base Query failed. BaseQuery contains from the beginning of the query till the where clause",
				"select city,winner,player_of_match from data/ipl.csv", queryParameter.getBaseQuery());
		assertEquals(
				"testGetFieldsAndRestrictions() : Retrieval of conditions part failed. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string. ",
				true, status);

		
	}

	@Test
	public void testGetFieldsAndRestrictionsFailure() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();
		assertNotNull(
				"testGetFieldsAndRestrictionsFailure() : Hint: extract the conditions from the query string(if exists). for each condition, we need to capture the following: 1. Name of field, 2. condition, 3. value, please note the query might contain multiple conditions separated by OR/AND operators",
				restrictions);

	}

	@Test
	public void testGetRestrictionsAndAggregateFunctions() {
		queryString = "select count(city),sum(win_by_runs),min(season),max(win_by_wickets) from data/ipl.csv where season > 2014 and city ='Bangalore'";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();
		List<AggregateFunction> aggfunction = queryParameter.getAggregateFunctions();

		assertNotNull("testGetRestrictionsAndAggregateFunctions() : Empty Restrictions list", restrictions);
		assertNotNull("testGetRestrictionsAndAggregateFunctions() : Empty Aggregates list", aggfunction);

		List<String> fields = new ArrayList<String>();
		fields.add("count(city)");
		fields.add("sum(win_by_runs)");
		fields.add("min(season)");
		fields.add("max(win_by_wickets)");

		List<String> logicalop = new ArrayList<String>();
		logicalop.add("and");

		Boolean status = false;
		int counter = 0;
		if (restrictions.get(0).getPropertyName().contains("season")
				&& restrictions.get(0).getPropertyValue().contains("2014")
				&& restrictions.get(0).getCondition().contains(">")) {
			counter++;
		}
		if (restrictions.get(1).getPropertyName().contains("city")
				&& restrictions.get(1).getPropertyValue().contains("Bangalore")
				&& restrictions.get(1).getCondition().contains("=")) {
			counter++;
		}
		if (counter > 1) {
			status = true;
		}

		List<AggregateFunction> aggregatefunction = new ArrayList<AggregateFunction>();
		int loopcounter = 0;
		for (AggregateFunction afunction : aggfunction) {
			aggregatefunction.add(afunction);
			if (afunction.getFunction().contains("count") || afunction.getFunction().contains("sum")
					|| afunction.getFunction().contains("min") || afunction.getFunction().contains("max")) {
				loopcounter++;
			}
		}

		boolean aggregatestatus = false;
		if (loopcounter > 3) {
			aggregatestatus = true;
		}

		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : Retrieval of Base Query failed. BaseQuery contains from the beginning of the query till the where clause",
				"select count(city),sum(win_by_runs),min(season),max(win_by_wickets) from data/ipl.csv",
				queryParameter.getBaseQuery());
		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : Retrieval of conditions part failed. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string.",
				true, status);
		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : Retrieval of Aggregate part failed. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string.",
				true, aggregatestatus);
		assertEquals(
				"testGetRestrictionsAndAggregateFunctions() : Retrieval of Logical Operators failed. AND/OR keyword will exist in the query only if where conditions exists and it contains multiple conditions.The extracted logical operators will be stored in a String array which will be returned by the method. Please note that AND/OR can exist as a substring in the conditions as well. For eg: name='Alexander',color='Red' etc.",
				logicalop, queryParameter.getLogicalOperators());

		
	}

	@Test
	public void testGetGroupByOrderByClause() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014 and city='Bangalore' group by winner order by city";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();

		List<String> fields = new ArrayList<String>();
		fields.add("city");
		fields.add("winner");
		fields.add("player_of_match");

		List<String> logicalop = new ArrayList<String>();
		logicalop.add("and");

		List<String> orderByFields = new ArrayList<String>();
		orderByFields.add("city");

		List<String> groupByFields = new ArrayList<String>();
		groupByFields.add("winner");

		Boolean status = false;
		int counter = 0;
		if (restrictions.get(0).getPropertyName().contains("season")
				&& restrictions.get(0).getPropertyValue().contains("2014")
				&& restrictions.get(0).getCondition().contains(">")) {
			counter++;
		}
		if (restrictions.get(1).getPropertyName().contains("city")
				&& restrictions.get(1).getPropertyValue().contains("Bangalore")
				&& restrictions.get(1).getCondition().contains("=")) {
			counter++;
		}
		if (counter > 1) {
			status = true;
		}

		assertEquals(
				"testGetGroupByOrderByClause() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetGroupByOrderByClause() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetGroupByOrderByClause() : Retrieval of Base Query failed. BaseQuery contains from the beginning of the query till the where clause",
				"select city,winner,player_of_match from data/ipl.csv", queryParameter.getBaseQuery());
		assertEquals(
				"testGetGroupByOrderByClause() : Retrieval of conditions part failed. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string.",
				true, status);
		assertEquals(
				"testGetGroupByOrderByClause() : Retrieval of Logical Operators failed. AND/OR keyword will exist in the query only if where conditions exists and it contains multiple conditions.The extracted logical operators will be stored in a String array which will be returned by the method. Please note that AND/OR can exist as a substring in the conditions as well. For eg: name='Alexander',color='Red' etc.",
				logicalop, queryParameter.getLogicalOperators());

		assertEquals(
				"testGetGroupByOrderByClause() : Hint: Check getGroupByFields() method. The query string can contain more than one group by fields. it is also possible thant the query string might not contain group by clause at all. The field names, condition values might contain 'group' as a substring. For eg: newsgroup_name",
				groupByFields, queryParameter.getGroupByFields());

		assertEquals(
				"testGetGroupByOrderByClause() : Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists",
				orderByFields, queryParameter.getOrderByFields());

		

	}

	@Test
	public void testGetGroupByOrderByClauseFailure() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014 and city='Bangalore' group by winner order by city";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();
		assertNotNull(
				"testGetGroupByOrderByClauseFailure() : Hint: Check getGroupByFields() method. The query string can contain more than one group by fields. it is also possible thant the query string might not contain group by clause at all. The field names, condition values might contain 'group' as a substring. For eg: newsgroup_name",
				queryParameter.getGroupByFields());
		assertNotNull(
				"testGetGroupByOrderByClauseFailure() : Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists.",
				queryParameter.getOrderByFields());
		assertNotNull(
				"testGetGroupByOrderByClauseFailure() : Hint: extract the conditions from the query string(if exists). for each condition, we need to capture the following: 1. Name of field, 2. condition, 3. value, please note the query might contain multiple conditions separated by OR/AND operators",
				restrictions);

		
	}

	@Test
	public void testGetGroupByClause() {
		queryString = "select city,winner,player_of_match from data/ipl.csv group by city";
		queryParameter = queryParser.parseQuery(queryString);
		List<String> fields = new ArrayList<String>();
		fields.add("city");

		assertEquals(
				"testGetGroupByClause() : Hint: Check getGroupByFields() method. The query string can contain more than one group by fields. it is also possible thant the query string might not contain group by clause at all. The field names, condition values might contain 'group' as a substring. For eg: newsgroup_name",
				fields, queryParameter.getGroupByFields());
		assertNotNull(
				"testGetGroupByClause() : Hint: Check getGroupByFields() method. The query string can contain more than one group by fields. it is also possible thant the query string might not contain group by clause at all. The field names, condition values might contain 'group' as a substring. For eg: newsgroup_name",
				queryParameter.getGroupByFields());

		fields.add("winner");
		fields.add("player_of_match");

		assertEquals(
				"testGetGroupByClause() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetGroupByClause() : Retrieval of Base Query failed. BaseQuery contains from the beginning of the query till the where clause",
				"select city,winner,player_of_match from data/ipl.csv", queryParameter.getBaseQuery());
		assertEquals(
				"testGetGroupByClause() : Retrieval of conditions part is not returning null. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string",
				null, queryParameter.getRestrictions());
		assertEquals(
				"testGetGroupByClause() : Logical Operators should be null. AND/OR keyword will exist in the query only if where conditions exists and it contains multiple conditions.The extracted logical operators will be stored in a String array which will be returned by the method. Please note that AND/OR can exist as a substring in the conditions as well. For eg: name='Alexander',color='Red' etc",
				null, queryParameter.getLogicalOperators());

		

	}

	@Test
	public void testGetOrderByAndWhereConditionClause() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014 and city ='Bangalore' order by city";
		queryParameter = queryParser.parseQuery(queryString);

		List<Restriction> restrictions = queryParameter.getRestrictions();

		List<String> fields = new ArrayList<String>();
		fields.add("city");
		fields.add("winner");
		fields.add("player_of_match");

		List<String> logicalop = new ArrayList<String>();
		logicalop.add("and");

		List<String> orderByFields = new ArrayList<String>();
		orderByFields.add("city");

		Boolean status = false;
		int counter = 0;
		if (restrictions.get(0).getPropertyName().contains("season")
				&& restrictions.get(0).getPropertyValue().contains("2014")
				&& restrictions.get(0).getCondition().contains(">")) {
			counter++;
		}
		if (restrictions.get(1).getPropertyName().contains("city")
				&& restrictions.get(1).getPropertyValue().contains("Bangalore")
				&& restrictions.get(1).getCondition().contains("=")) {
			counter++;
		}
		if (counter > 1) {
			status = true;
		}

		assertEquals(
				"testGetOrderByAndWhereConditionClause() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetOrderByAndWhereConditionClause() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetOrderByAndWhereConditionClause() : Retrieval of Base Query failed. BaseQuery contains from the beginning of the query till the where clause",
				"select city,winner,player_of_match from data/ipl.csv", queryParameter.getBaseQuery());
		assertEquals(
				"testGetOrderByAndWhereConditionClause() : Retrieval of conditions part failed. The conditions part contains starting from where keyword till the next keyword, which is either group by or order by clause. In case of absence of both group by and order by clause, it will contain till the end of the query string.",
				true, status);
		assertEquals(
				"testGetOrderByAndWhereConditionClause() : Retrieval of Logical Operators failed. AND/OR keyword will exist in the query only if where conditions exists and it contains multiple conditions.The extracted logical operators will be stored in a String array which will be returned by the method. Please note that AND/OR can exist as a substring in the conditions as well. For eg: name='Alexander',color='Red' etc.",
				logicalop, queryParameter.getLogicalOperators());

		assertEquals(
				"testGetOrderByAndWhereConditionClause() : Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists",
				orderByFields, queryParameter.getOrderByFields());

		

	}

	@Test
	public void testGetOrderByAndWhereConditionClauseFailure() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where season > 2014 and city ='Bangalore' order by city";
		queryParameter = queryParser.parseQuery(queryString);
		List<Restriction> restrictions = queryParameter.getRestrictions();
		assertNotNull(
				"testGetOrderByAndWhereConditionClauseFailure() : Hint: extract the conditions from the query string(if exists). for each condition, we need to capture the following: 1. Name of field, 2. condition, 3. value, please note the query might contain multiple conditions separated by OR/AND operators",
				restrictions);
		assertNotNull(
				"testGetOrderByAndWhereConditionClauseFailure() :Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists.",
				queryParameter.getOrderByFields());
		
	}

	@Test
	public void testGetOrderByClause() {
		queryString = "select city,winner,player_of_match from data/ipl.csv where city='Bangalore' order by winner";
		queryParameter = queryParser.parseQuery(queryString);

		List<String> orderByFields = new ArrayList<String>();
		orderByFields.add("winner");

		List<String> fields = new ArrayList<String>();
		fields.add("city");
		fields.add("winner");
		fields.add("player_of_match");

		assertEquals(
				"testGetOrderByClause() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetOrderByClause() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetOrderByClause() : Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists",
				orderByFields, queryParameter.getOrderByFields());
		
	}

	@Test
	public void testGetOrderByWithoutWhereClause() {
		queryString = "select city,winner,player_of_match from data/ipl.csv order by city";
		queryParameter = queryParser.parseQuery(queryString);

		List<String> orderByFields = new ArrayList<String>();
		orderByFields.add("city");

		List<String> fields = new ArrayList<String>();
		fields.add("city");
		fields.add("winner");
		fields.add("player_of_match");

		assertEquals(
				"testGetOrderByClause() : File name extraction failed. Check getFile() method. File name can be found after a space after from clause. Note: CSV file can contain a field that contains from as a part of the column name. For eg: from_date,from_hrs etc",
				"data/ipl.csv", queryParameter.getFileName());
		assertEquals(
				"testGetOrderByClause() : Select fields extractions failed. The query string can have multiple fields separated by comma after the 'select' keyword. The extracted fields is supposed to be stored in a String array which is to be returned by the method getFields(). Check getFields() method",
				fields, queryParameter.getFields());
		assertEquals(
				"testGetOrderByClause() : Hint: Please note that we will need to extract the field(s) after 'order by' clause in the query, if at all the order by clause exists",
				orderByFields, queryParameter.getOrderByFields());


	}


	/*
	 * The following test cases are used to check whether the query processing are
	 * working properly
	 */

	@Test
	public void testSelectAllWithoutWhereClause() throws FileNotFoundException {
		int totalrecordsexpected = 577;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery("select * from data/ipl.csv");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={date=2008-04-18, venue=M Chinnaswamy Stadium, win_by_wickets=0, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore, result=normal, dl_applied=0, winner=Kolkata Knight Riders, player_of_match=BB McCullum, umpire1=Asad Rauf, season=2008, toss_winner=Royal Challengers Bangalore, umpire3=, id=1, umpire2=RE Koertzen, toss_decision=field, win_by_runs=140}")) {
					counter++;
				}
			} else if (recordscounter == 289) {
				if (itr.next().toString().contains(
						"289={date=2012-05-01, venue=Barabati Stadium, win_by_wickets=0, city=Cuttack, team1=Deccan Chargers, team2=Pune Warriors, result=normal, dl_applied=0, winner=Deccan Chargers, player_of_match=KC Sangakkara, umpire1=Aleem Dar, season=2012, toss_winner=Deccan Chargers, umpire3=, id=289, umpire2=AK Chaudhary, toss_decision=bat, win_by_runs=13}")) {
					counter++;
				}
			} else if (recordscounter == 577) {
				if (itr.next().toString().contains(
						"577={date=2016-05-29, venue=M Chinnaswamy Stadium, win_by_wickets=0, city=Bangalore, team1=Sunrisers Hyderabad, team2=Royal Challengers Bangalore, result=normal, dl_applied=0, winner=Sunrisers Hyderabad, player_of_match=BCJ Cutting, umpire1=HDPK Dharmasena, season=2016, toss_winner=Sunrisers Hyderabad, umpire3=, id=577, umpire2=BNJ Oxenford, toss_decision=bat, win_by_runs=8}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}

		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testSelectAllWithoutWhereClause() : Empty Dataset returned", dataSet);
		assertEquals("testSelectAllWithoutWhereClause() : Total number of records should be 577", totalrecordsexpected,
				dataSet.size());
		assertEquals(
				"testSelectAllWithoutWhereClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}

	@Test
	public void testSelectColumnsWithoutWhereClause() throws FileNotFoundException {
		int totalrecordsexpected = 577;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery("select city,winner,team1,team2 from data/ipl.csv");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else if (recordscounter == 289) {
				if (itr.next().toString().contains(
						"289={winner=Deccan Chargers, city=Cuttack, team1=Deccan Chargers, team2=Pune Warriors}")) {
					counter++;
				}
			} else if (recordscounter == 577) {
				if (itr.next().toString().contains(
						"577={winner=Sunrisers Hyderabad, city=Bangalore, team1=Sunrisers Hyderabad, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else {
				itr.next();
			}
			recordscounter++;
		}
		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testSelectColumnsWithoutWhereClause() : Empty Dataset returned", dataSet);
		assertEquals("testSelectColumnsWithoutWhereClause() : Total number of records should be 577",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testSelectColumnsWithoutWhereClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);

	

	}

	@Test
	public void testWithWhereGreaterThanClause() throws FileNotFoundException {
		int totalrecordsexpected = 60;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select season,city,winner,team1,team2,player_of_match from data/ipl.csv where season > 2015");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Rising Pune Supergiants, player_of_match=AM Rahane, city=Mumbai, team1=Mumbai Indians, team2=Rising Pune Supergiants, season=2016}")) {
					counter++;
				}
			} else if (recordscounter == 30) {
				if (itr.next().toString().contains(
						"30={winner=Kolkata Knight Riders, player_of_match=AD Russell, city=Bangalore, team1=Royal Challengers Bangalore, team2=Kolkata Knight Riders, season=2016}")) {
					counter++;
				}
			} else if (recordscounter == 60) {
				if (itr.next().toString().contains(
						"60={winner=Sunrisers Hyderabad, player_of_match=BCJ Cutting, city=Bangalore, team1=Sunrisers Hyderabad, team2=Royal Challengers Bangalore, season=2016}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}

		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereGreaterThanClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereGreaterThanClause() : Total number of records should be 60", totalrecordsexpected,
				dataSet.size());
		assertEquals(
				"testWithWhereGreaterThanClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);

	

	}

	@Test
	public void testWithWhereLessThanClause() throws FileNotFoundException {
		int totalrecordsexpected = 458;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query
				.executeQuery("select city,winner,team1,team2,player_of_match from data/ipl.csv where season < 2015");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=BB McCullum, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else if (recordscounter == 229) {
				if (itr.next().toString().contains(
						"229={winner=Royal Challengers Bangalore, player_of_match=S Aravind, city=Jaipur, team1=Rajasthan Royals, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else if (recordscounter == 458) {
				if (itr.next().toString().contains(
						"458={winner=Kolkata Knight Riders, player_of_match=MK Pandey, city=Bangalore, team1=Kings XI Punjab, team2=Kolkata Knight Riders}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}
		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereLessThanClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereLessThanClause() : Total number of records should be 458", totalrecordsexpected,
				dataSet.size());
		assertEquals(
				"testWithWhereLessThanClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);

		

	}

	@Test
	public void testWithWhereLessThanOrEqualToClause() throws FileNotFoundException {
		int totalrecordsexpected = 517;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select season,city,winner,team1,team2,player_of_match from data/ipl.csv where season <= 2015");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=BB McCullum, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore, season=2008}")) {
					counter++;
				}
			} else if (recordscounter == 258) {
				if (itr.next().toString().contains(
						"258={winner=Kolkata Knight Riders, player_of_match=L Balaji, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore, season=2012}")) {
					counter++;
				}
			} else if (recordscounter == 517) {
				if (itr.next().toString().contains(
						"517={winner=Mumbai Indians, player_of_match=RG Sharma, city=Kolkata, team1=Mumbai Indians, team2=Chennai Super Kings, season=2015}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}
		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereLessThanOrEqualToClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereLessThanOrEqualToClause() : Total number of records should be 517",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testWithWhereLessThanOrEqualToClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);

	

	}

	@Test
	public void testWithWhereGreaterThanOrEqualToClause() throws FileNotFoundException {
		int totalrecordsexpected = 119;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query
				.executeQuery("select city,winner,team1,team2,player_of_match from data/ipl.csv where season >= 2015");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=M Morkel, city=Kolkata, team1=Mumbai Indians, team2=Kolkata Knight Riders}")) {
					counter++;
				}
			} else if (recordscounter == 60) {
				if (itr.next().toString().contains(
						"60={winner=Rising Pune Supergiants, player_of_match=AM Rahane, city=Mumbai, team1=Mumbai Indians, team2=Rising Pune Supergiants}")) {
					counter++;
				}
			} else if (recordscounter == 119) {
				if (itr.next().toString().contains(
						"119={winner=Sunrisers Hyderabad, player_of_match=BCJ Cutting, city=Bangalore, team1=Sunrisers Hyderabad, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}
		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereGreaterThanOrEqualToClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereGreaterThanOrEqualToClause() : Total number of records should be 119",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testWithWhereGreaterThanOrEqualToClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}

	@Test
	public void testWithWhereNotEqualToClause() throws FileNotFoundException {

		int totalrecordsexpected = 315;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select city,team1,team2,winner,toss_decision from data/ipl.csv where toss_decision != bat");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore, toss_decision=field}")) {
					counter++;
				}
			} else if (recordscounter == 157) {
				if (itr.next().toString().contains(
						"157={winner=Delhi Daredevils, city=Dharamsala, team1=Kings XI Punjab, team2=Delhi Daredevils, toss_decision=field}")) {
					counter++;
				}
			} else if (recordscounter == 315) {
				if (itr.next().toString().contains(
						"315={winner=Sunrisers Hyderabad, city=Delhi, team1=Gujarat Lions, team2=Sunrisers Hyderabad, toss_decision=field}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}

		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereNotEqualToClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereNotEqualToClause() : Total number of records should be 315", totalrecordsexpected,
				dataSet.size());
		assertEquals(
				"testWithWhereNotEqualToClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}

	@Test
	public void testWithWhereEqualAndNotEqualClause() throws FileNotFoundException {
		int totalrecordsexpected = 195;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select season,city,winner,team1,team2,player_of_match from data/ipl.csv where season >= 2013 and season <= 2015");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=SP Narine, city=Kolkata, team1=Delhi Daredevils, team2=Kolkata Knight Riders, season=2013}")) {
					counter++;
				}
			} else if (recordscounter == 97) {
				if (itr.next().toString().contains(
						"97={winner=Chennai Super Kings, player_of_match=RA Jadeja, city=Ranchi, team1=Chennai Super Kings, team2=Kolkata Knight Riders, season=2014}")) {
					counter++;
				}
			} else if (recordscounter == 195) {
				if (itr.next().toString().contains(
						"195={winner=Mumbai Indians, player_of_match=RG Sharma, city=Kolkata, team1=Mumbai Indians, team2=Chennai Super Kings, season=2015}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}
		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereEqualAndNotEqualClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereEqualAndNotEqualClause() : Total number of records should be 195",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testWithWhereEqualAndNotEqualClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}

	@Test
	public void testWithWhereTwoConditionsEqualOrNotEqualClause() throws FileNotFoundException {
		int totalrecordsexpected = 155;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select city,winner,team1,team2,player_of_match from data/ipl.csv where season >= 2013 and toss_decision != bat");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=SP Narine, city=Kolkata, team1=Delhi Daredevils, team2=Kolkata Knight Riders}")) {
					counter++;
				}
			} else if (recordscounter == 78) {
				if (itr.next().toString().contains(
						"78={winner=Kings XI Punjab, player_of_match=GJ Bailey, city=Mumbai, team1=Kings XI Punjab, team2=Mumbai Indians}")) {
					counter++;
				}
			} else if (recordscounter == 155) {
				if (itr.next().toString().contains(
						"155={winner=Sunrisers Hyderabad, player_of_match=DA Warner, city=Delhi, team1=Gujarat Lions, team2=Sunrisers Hyderabad}")) {
					counter++;
				}
			} else {
				itr.next();
			}

			recordscounter++;
		}

		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereTwoConditionsEqualOrNotEqualClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereTwoConditionsEqualOrNotEqualClause() : Total number of records should be 155",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testWithWhereTwoConditionsEqualOrNotEqualClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}

	@Test
	public void testWithWhereThreeConditionsEqualOrNotEqualClause() throws FileNotFoundException {
		int totalrecordsexpected = 577;
		int recordscounter = 1;
		boolean dataexpectedstatus = false;

		HashMap dataSet = query.executeQuery(
				"select city,winner,team1,team2,player_of_match from data/ipl.csv where season >= 2008 or toss_decision != bat and city = bangalore");

		Set map = dataSet.entrySet();
		Iterator itr = map.iterator();

		int counter = 0;
		while (itr.hasNext()) {
			if (recordscounter == 1) {
				if (itr.next().toString().contains(
						"1={winner=Kolkata Knight Riders, player_of_match=BB McCullum, city=Bangalore, team1=Kolkata Knight Riders, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else if (recordscounter == 289) {
				if (itr.next().toString().contains(
						"289={winner=Deccan Chargers, player_of_match=KC Sangakkara, city=Cuttack, team1=Deccan Chargers, team2=Pune Warriors}")) {
					counter++;
				}
			} else if (recordscounter == 577) {
				if (itr.next().toString().contains(
						"577={winner=Sunrisers Hyderabad, player_of_match=BCJ Cutting, city=Bangalore, team1=Sunrisers Hyderabad, team2=Royal Challengers Bangalore}")) {
					counter++;
				}
			} else {
				itr.next();
			}
			recordscounter++;
		}

		if (counter > 2) {
			dataexpectedstatus = true;
		}

		assertNotNull("testWithWhereThreeConditionsEqualOrNotEqualClause() : Empty Dataset returned", dataSet);
		assertEquals("testWithWhereThreeConditionsEqualOrNotEqualClause() : Total number of records should be 577",
				totalrecordsexpected, dataSet.size());
		assertEquals(
				"testWithWhereThreeConditionsEqualOrNotEqualClause() : Total number of records are matching but the records returned does not match the expected data",
				true, dataexpectedstatus);


	}


}