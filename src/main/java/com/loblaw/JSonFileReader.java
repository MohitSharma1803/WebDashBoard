package com.loblaw;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import com.loblaw.Pojo.StateIdJsonMapper;
import com.loblaw.util.AutomationJsonParser;



public class JSonFileReader {
	
	static final Logger logger = Logger.getLogger(JSonFileReader.class.getName());
	
	public static List<StateIdJsonMapper> readJsonFile(String file)  {
		logger.debug("file>>>" + file);
		AutomationJsonParser jsonParser = new AutomationJsonParser();
		List<StateIdJsonMapper> mainAttributesList =null;
		try {
			mainAttributesList = jsonParser.readJsonFile(file);
		} catch (org.json.simple.parser.ParseException e) {
			logger.error(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mainAttributesList;
	}
	

}
