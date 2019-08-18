package com.loblaw.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.MultiValueMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loblaw.Pojo.StateId;
import com.loblaw.Pojo.StateIdJsonMapper;

public class JsonSampleReader {

	public static MultiValueMap<String, String> mapOfN = new MultiValueMap<String, String>();

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {
		List<StateIdJsonMapper> ls = readSampleFromMain();
		for (StateIdJsonMapper mp : ls) {
			System.out.println(mp.getEndPoint()+">>>>" + mp.getReturnCode()+">>");
			List<StateId> id = mp.getStateId();
			for (StateId stateId : id) {
				System.out.println("Id :" +stateId.getId());
				System.out.println("Comment :" +stateId.getComment());
			}
		}
	}

	private static List readSampleFromMain() throws ParseException {
		JSONParser parser = new JSONParser();
		String ispName = "";

		List<StateIdJsonMapper> mainAttributesList = new ArrayList<StateIdJsonMapper>();
		List<StateId> dupstatesList = new ArrayList<StateId>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<StateId> list = new ArrayList<StateId>();
		List<Map<String, Object>> test = new ArrayList<Map<String, Object>>();
		
		try {
			Object obj = parser
					.parse(new FileReader(
							"C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\Symentic_To_Mcafee_Antivirus\\sample.json"
							//"C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\Symentic_To_Mcafee_Antivirus\\spacewalk_cleanup_20190611_153509.log"
							));
			JSONObject jsonObject = (JSONObject) obj;
			Set set = jsonObject.keySet();
			Iterator<String> iterator = set.iterator();
			Iterator<String> stateIdSetIterator = null;

			while (iterator.hasNext()) {
				StateIdJsonMapper stateIdMapper = new StateIdJsonMapper();
				JSONObject endPointJsonObj = null;
				ispName = iterator.next();
				if(jsonObject.get(ispName).toString().contains("The minion function caused an exception: Traceback (most recent call last)")){
					stateIdMapper.setEndPoint(ispName);
					stateIdMapper.setReturnCode("3");
					List<StateId> l = new ArrayList<StateId>();
					StateId stateIds = new StateId();
					stateIds.setName("Minion not returned");
					stateIds.setId("Minion not returned");	
					stateIds.setChanges("Minion not returned");
					stateIds.setComment(jsonObject.get(ispName).toString());
					l.add(stateIds);
					stateIdMapper.setStateId(l);
					
					//System.out.println(ispName);
					
				}
				else{				
				endPointJsonObj = (JSONObject) jsonObject.get(ispName);
				if(endPointJsonObj.isEmpty()){
					stateIdMapper.setEndPoint(ispName);
					stateIdMapper.setReturnCode("3");
					List<StateId> l = new ArrayList<StateId>();
					StateId stateIds = new StateId();
					stateIds.setName("Minion not returned");
					stateIds.setId("Minion not returned");	
					stateIds.setChanges("Minion not returned");
					stateIds.setComment("OutPut is Blank");
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
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

	private static Map readJsonStateObj(Map m) {

		return m;

	}

	private static Map jsonReader() throws FileNotFoundException, IOException,
			ParseException {
		Map<String, List<String>> m = new HashMap<String, List<String>>();

		JSONParser parser = new JSONParser();
		JSONObject endPointJsonObj = null;
		Object obj = parser
				.parse(new FileReader(
						"C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\Symentic_To_Mcafee_Antivirus\\sample.json"));
		JSONObject jsonObject = (JSONObject) obj;
		Set set = jsonObject.keySet();
		Set stateIdSet = null;
		Iterator<String> iterator = set.iterator();
		Iterator<String> stateIdSetIterator = null;
		String stateIds = null;

		List<String> duplicate = new ArrayList<String>();
		while (iterator.hasNext()) {
			List<String> listOfString = new ArrayList<String>();
			String endpoint = (String) iterator.next();
			// System.out.println(endpoint);

			endPointJsonObj = (JSONObject) jsonObject.get(endpoint);
			stateIdSet = endPointJsonObj.keySet();
			stateIdSetIterator = stateIdSet.iterator();
			while (stateIdSetIterator.hasNext()) {
				String name = null;
				stateIds = (String) stateIdSetIterator.next();
				if (stateIds.equals("retcode")) {
					Long returnCode = (Long) endPointJsonObj.get("retcode");
					listOfString.add(returnCode.toString());
				} else {
					JSONObject stateIdJsonObj = (JSONObject) endPointJsonObj
							.get(stateIds);
					name = (String) stateIdJsonObj.get("name");
					listOfString.add(name);

				}

			}

			duplicate.addAll(listOfString);
		}
		System.out.println(duplicate);
		return m;
	}

	public static Set listFilesForFolder(final File folder) {
		Set<String> set = new HashSet<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				String s = fileEntry.getName();
				String[] strArr = s.split("_");
				String dpType = strArr[0];
				String dpDate = strArr[1];
				mapOfN.put(dpType, ConvertDate(dpDate));
				set.add(dpType);

			}
		}
		return set;
	}

	public static String ConvertDate(String strDate) {
		String date1 = null;
		try {

			// input date format
			SimpleDateFormat df_in = new SimpleDateFormat("yyyyMMdd");

			// output date format
			SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd");
			Date date = df_in.parse(strDate);
			date1 = df_output.format(date);
		} catch (Exception e) {
			System.out.println("Invalid Date: " + e.getMessage());
		}
		return date1;
	}

	private static void readFile() throws FileNotFoundException {
		String s = "Spacewalkcleanup";
		String date = "20190611";
		List<String> filesToRead = listFiles(s + "_" + date);
		for (String value : filesToRead) {
			System.out.println(value);

		}

	}

	public static List listFiles(String filAndDate)
			throws FileNotFoundException {
		String str = "C:\\Users\\mohishar\\Mohit\\DevOps\\json_samples\\sampleFiles";
		File folder = new File(str);
		List<String> list = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			String s = fileEntry.getName();
			String[] strArr = s.split("_");
			String dpType = strArr[0];
			String dpDate = strArr[1];
			String fileInDir = dpType + "_" + dpDate;
			if (fileInDir.equals(filAndDate)) {
				String filetoread = str + "\\" + s;
				list.add(filetoread);
			}

		}

		return list;
	}

	public static String[] getSpecialDays(String DpName) {
		List<String> coll = (List<String>) mapOfN.get(DpName);
		String[] result = coll.toArray(new String[coll.size()]);
		System.out.println(result[0] + result[1]);

		return result;
	}

}
