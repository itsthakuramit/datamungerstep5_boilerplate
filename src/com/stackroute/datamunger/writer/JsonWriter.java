package com.stackroute.datamunger.writer;

import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonWriter {
	/*
	 * This method will write the resultSet object into a JSON file. On successful
	 * writing, the method will return true, else will return false
	 */
	@SuppressWarnings("rawtypes")
	public boolean writeToJson(Map resultSet) {

		/*
		 * Gson is a third party library to convert Java object to JSON. We will use
		 * Gson to convert resultSet object to JSON
		 */
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		@SuppressWarnings("unused")
		String result = gson.toJson(resultSet);

		/*
		 * write JSON string to data/result.json file. As we are performing File IO,
		 * consider handling exception
		 */

		/* return true if file writing is successful */

		/* return false if file writing is failed */

		/* close BufferedWriter object */

		return false;
	}

}
