package com.loblaw.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loblaw.JSonFileReader;
import com.loblaw.Pojo.StateId;
import com.loblaw.Pojo.StateIdJsonMapper;

public class AutomationJsonParser {
	
	
	static final Logger logger = Logger.getLogger(AutomationJsonParser.class.getName());
/*public static void main(String[] args) throws FileNotFoundException,
	IOException, ParseException {
List<StateIdJsonMapper> ls = readJsonFile("C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\sampleFiles\\hightstate-applied_20190712_162019.log"
		//"C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\Symentic_To_Mcafee_Antivirus\\spacewalk_cleanup_20190611_153509.log"
		);
for (StateIdJsonMapper mp : ls) {
	System.out.println(mp.getEndPoint()+">>>>" + mp.getReturnCode()+">>");
	List<StateId> id = mp.getStateId();
	for (StateId stateId : id) {
		System.out.println("Id :" +stateId.getId());
		System.out.println("Comment :" +stateId.getComment());
	}
}
}*/

public List readJsonFile(String filePath) throws ParseException {
JSONParser parser = new JSONParser();
String ispName = "";
logger.debug(filePath);
List<StateIdJsonMapper> mainAttributesList = new ArrayList<StateIdJsonMapper>();
Map<String, Object> map = new HashMap<String, Object>();
List<StateId> list = new ArrayList<StateId>();
List<Map<String, Object>> test = new ArrayList<Map<String, Object>>();


try {
	if(null != filePath){
	Object obj = parser.parse(new FileReader(filePath));
	JSONObject jsonObject = (JSONObject) obj;
	Set set = jsonObject.keySet();
	Iterator<String> iterator = set.iterator();
	
	while (iterator.hasNext()) {
		StateIdJsonMapper stateIdMapper = new StateIdJsonMapper();
		JSONObject endPointJsonObj = null;
		ispName = iterator.next();
		if(jsonObject.get(ispName).toString().contains("The minion function caused an exception: Traceback (most recent call last)")){
			stateIdMapper.setEndPoint(ispName);
			stateIdMapper.setReturnCode("3");
			stateIdMapper.setStatus("No Response");
			List<StateId> l = new ArrayList<StateId>();
			StateId stateIds = new StateId();
			stateIds.setName("Minion not returned");
			stateIds.setId("Minion not returned");	
			stateIds.setChanges("Minion not returned");
			stateIds.setDuration(0.0);
			stateIds.setComment(jsonObject.get(ispName).toString());
			l.add(stateIds);
			stateIdMapper.setStateId(l);
			
			//System.out.println(ispName);
			
		}
		else if(jsonObject.get(ispName).toString().contains("saltenv base is not available on the salt master or through a configured fileserver")){
			stateIdMapper.setEndPoint(ispName);
			stateIdMapper.setReturnCode("3");
			stateIdMapper.setStatus("No Response");
			List<StateId> l = new ArrayList<StateId>();
			StateId stateIds = new StateId();
			stateIds.setName("saltenv base is not available on the salt master or through a configured fileserver");
			stateIds.setId("saltenv base is not available on the salt master or through a configured fileserver");	
			stateIds.setChanges("saltenv base is not available on the salt master or through a configured fileserver");
			stateIds.setDuration(0.0);
			stateIds.setComment(jsonObject.get(ispName).toString());
			l.add(stateIds);
			stateIdMapper.setStateId(l);
		}
		else{	
			
		endPointJsonObj = (JSONObject) jsonObject.get(ispName);
		if(endPointJsonObj.isEmpty()){
			stateIdMapper.setEndPoint(ispName);
			stateIdMapper.setReturnCode("3");
			stateIdMapper.setStatus("No Response");
			List<StateId> l = new ArrayList<StateId>();
			StateId stateIds = new StateId();
			stateIds.setName("Minion not returned");
			stateIds.setId("Minion not returned");	
			stateIds.setChanges("Minion not returned");
			stateIds.setComment("OutPut is Blank");
			stateIds.setDuration(0.0);
			l.add(stateIds);
			stateIdMapper.setStateId(l);
		}
		if(!endPointJsonObj.isEmpty()){
		map.put(ispName, endPointJsonObj);
		List listt=childOfEndPoint(map, test);
		List listtt=stateIDs(test,stateIdMapper);				
		stateIdMapper.setEndPoint(ispName);
		stateIdMapper.setStateId(listtt);				
		map.clear();
		listt.clear();
		}
		}
		mainAttributesList.add(stateIdMapper);
	}
	}
	
} catch (FileNotFoundException e) {
	logger.error(e.getMessage());
	e.printStackTrace();
} catch (IOException e) {
	logger.error(e.getMessage());
	e.printStackTrace();
}


return mainAttributesList;
}

private static List stateIDs(
	List<Map<String, Object>> test , StateIdJsonMapper stateIdMapper) {
List<StateId> list =new ArrayList<StateId>();
for (Map<String, Object> listt : test) {

	for (Map.Entry<String, Object> entry : listt.entrySet()) {
		StateId stateIds = new StateId();
		
		
		if(entry.toString().equals("retcode=0") || entry.toString().equals("retcode=1") ||  entry.toString().equals("retcode=2")){
			Long returnCode = (Long) entry
					.getValue();
			if(returnCode.toString().equals("0")){
				stateIdMapper.setStatus("Pass");
            }
            else {
            	stateIdMapper.setStatus("Fail");
            }
			stateIdMapper.setReturnCode(returnCode.toString());
			
		}
		
		else {
			
			JSONObject stateIdJsonObj = (JSONObject) entry
					.getValue();
			
			String name = (String) stateIdJsonObj.get("name");
			stateIds.setName(name);
			Boolean result = (Boolean) stateIdJsonObj.get("result");
			stateIds.setStateResult(result.toString());
			Double duration = (Double) stateIdJsonObj
					.get("duration");
			stateIds.setDuration(duration);
			Long runNum = (Long) stateIdJsonObj.get("__run_num__");
			stateIds.setRunNum(runNum.toString());
			String comment = (String) stateIdJsonObj.get("comment");
			stateIds.setComment(comment);
			String id = (String) stateIdJsonObj.get("__id__");
			if(id == null){
				stateIds.setId(entry.getKey());
			}
			else{
			stateIds.setId(id);
			}
			JSONObject changesJsonObject = (JSONObject) stateIdJsonObj
					.get("changes");
			stateIds.setChanges(changesJsonObject.toString());
			list.add(stateIds);
			
		}
		
	}

}

return list;
}

private static List childOfEndPoint(Map<String, Object> map,
	List<Map<String, Object>> test) {
String names;
for (Map.Entry<String, Object> entry : map.entrySet()) {
	Map<String, Object> mapp = new HashMap<String, Object>();
	JSONObject stateIdJsonObj = (JSONObject) entry.getValue();

	Set sett = stateIdJsonObj.keySet();
	Iterator<String> iteratorr = sett.iterator();

	while (iteratorr.hasNext()) {
		names = iteratorr.next();

		mapp.put(names, stateIdJsonObj.get(names));
	}

	test.add(mapp);

}

return test;
}


}
