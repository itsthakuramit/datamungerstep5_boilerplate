package com.stackroute.datamunger.reader;

import com.stackroute.datamunger.query.DataSet;
import com.stackroute.datamunger.query.parser.QueryParameter;

public interface QueryProcessingEngine {

	public DataSet getResultSet(QueryParameter queryParameter);
	
}
