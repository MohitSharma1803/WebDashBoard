package com.loblaw.Beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.servlet.jsp.tagext.ValidationMessage;

import org.apache.log4j.Logger;
import org.apache.poi.hpsf.HPSFException;
import org.json.simple.parser.ParseException;

import com.loblaw.PieChartBean;
import com.loblaw.Pojo.StateId;
import com.loblaw.Pojo.StateIdJsonMapper;
import com.loblaw.util.AutomationConstants;
import com.loblaw.util.AutomationJsonParser;
import com.loblaw.util.CommonDateUtil;

@ManagedBean
@ViewScoped
public class EndPointsAttributes {
	static final Logger logger = Logger.getLogger(EndPointsAttributes.class.getName());
	
	private List<StateIdJsonMapper> mainAttributesList = null;
	private List<StateId> statesList = new ArrayList<StateId>();
	private StateIdJsonMapper selectedAttribute;
	private Map<String,List<StateId>> endpointsMap = new HashMap<String,List<StateId>>();
	private StateId selectedStateId;
	private String dpTypeToShow;
	private String dpDateToShow;
	private String dpTimeToShow;
	
	public String getDpTimeToShow() {
		return dpTimeToShow;
	}

	public void setDpTimeToShow(String dpTimeToShow) {
		this.dpTimeToShow = dpTimeToShow;
	}

	public String getDpTypeToShow() {
		return dpTypeToShow;
	}

	public void setDpTypeToShow(String dpTypeToShow) {
		this.dpTypeToShow = dpTypeToShow;
	}

	public String getDpDateToShow() {
		return dpDateToShow;
	}

	public void setDpDateToShow(String dpDateToShow) {
		this.dpDateToShow = dpDateToShow;
	}

	public StateId getSelectedStateId() {
		return selectedStateId;
	}

	public void setSelectedStateId(StateId selectedStateId) {		
		this.selectedStateId = selectedStateId;
	}

	public List<StateId> getStatesList() {
		return statesList;
	}

	public void setStatesList(List<StateId> statesList) {
		this.statesList = statesList;
	}

	public StateIdJsonMapper getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(StateIdJsonMapper selectedAttribute) {		
		this.selectedAttribute = selectedAttribute;		
	}

	
	
	
	public void setMainAttributesList(List<StateIdJsonMapper> mainAttributesList) {
		this.mainAttributesList = mainAttributesList;
	}

	public List<StateIdJsonMapper> getMainAttributesList() {
		sortByStatus();
		return mainAttributesList;
	}

	public String sortByStatus() {
	   
		   Collections.sort(mainAttributesList, new Comparator<StateIdJsonMapper>() {

			public int compare(StateIdJsonMapper o1, StateIdJsonMapper o2) {
						
				return o1.getStatus().compareTo(o2.getStatus());
						
			}
		   });
		return null;	
		}
	
	@PostConstruct	
	private void postConstruct ()    {
		logger.info("In postConstruct");
		try {
			tokenizePathAndDate();
		} catch (java.text.ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		AutomationJsonParser jsonParser = new AutomationJsonParser();
		try {
			if(PieChartBean.filepath !=null){
				mainAttributesList = jsonParser.readJsonFile(PieChartBean.filepath);
			}
			else{
				logger.error("Wrong Criteria is Selected by User");
				returnHomePage();
				
				
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			logger.error(e1.getMessage());
			e1.printStackTrace();
		}
		

    }
	
	private String returnHomePage(){
		return "SearchCriteria?faces-redirect=true";
	}
	private void tokenizePathAndDate() throws java.text.ParseException{
		logger.info("In tokenizePathAndDate");
		String filePath = PieChartBean.filepath;
		logger.debug("FilePath" + filePath);
		if(filePath == null){
			logger.error("Wrong Criteria selected by user");
		}
		else{
		String[] splitString= filePath.split(Pattern.quote(AutomationConstants.SLASH));
		String dpTypeAndDate="";
		for (int i=0 ; i<splitString.length; i++) {
			dpTypeAndDate = splitString[splitString.length-1];
			
		}
		
		String[] splitDpTypeAndDate = dpTypeAndDate.split("_");
		dpTypeToShow=splitDpTypeAndDate[0];
		dpDateToShow=CommonDateUtil.ConvertDate(splitDpTypeAndDate[1]);		
		String  dpTimeArr =  splitDpTypeAndDate[2].split("\\.")[0];
        DateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Date d = dateFormat.parse(dpTimeArr);
        dpTimeToShow = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		}
	}
	
	
	
	@SuppressWarnings("static-access")
	public void download() {					
		try {
			logger.info("In download Excel file");
			String fileName=dpTypeToShow+dpDateToShow+".xls";
			 
			com.loblaw.util.CommonUtil commonUtil = new com.loblaw.util.CommonUtil();			
			try {
				commonUtil.exportToExcel(dpTypeToShow+"_"+dpDateToShow, setHeader(mainAttributesList), setContent(mainAttributesList), fileName);
			} catch (HPSFException e) {				
				e.printStackTrace();
			}			
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
			@SuppressWarnings("unused")
			javax.servlet.ServletContext servletContext = (javax.servlet.ServletContext) context
			.getExternalContext().getContext();
			javax.servlet.http.HttpServletResponse httpServletResponse = (javax.servlet.http.HttpServletResponse) javax.faces.context.FacesContext
			.getCurrentInstance().getExternalContext().getResponse();
			javax.servlet.ServletOutputStream servletOutputStream = httpServletResponse
			.getOutputStream();
			httpServletResponse.setHeader("Content-disposition","attachment; filename="+fileName);
			httpServletResponse.setContentType("application/x-download");


			byte[] b = new byte[1024];
			int i = 0;
			java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
			while ((i = fis.read(b)) > 0) {
				servletOutputStream.write(b, 0, i);
			}

		} catch (java.io.IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		javax.faces.context.FacesContext.getCurrentInstance().responseComplete();
	}
	
	public ArrayList<String> setHeader(List<StateIdJsonMapper> stateData){
		ArrayList<String> oneRow = new ArrayList<String>();
		oneRow.add("END POINT");
        oneRow.add("OVERALL STATUS");
        StateIdJsonMapper stateIdJsonMapper = stateData.get(0);
        List<StateId> stateId = stateIdJsonMapper.getStateId();
        	//StateId id =stateId.get(0);
        	for (int i=1; i<= stateId.size(); i++) {
        		oneRow.add("STATE ID_"+i);
                oneRow.add("STATE ID NAME_"+i);
                oneRow.add("STATE RUN NUMBER_"+i);
                oneRow.add("STATE DURATION(MS)_"+i);
                oneRow.add("STATE RESULT_"+i);
                oneRow.add("STATE COMMENT_"+i); 
                oneRow.add("STATE CHANGES_"+i); 
        	} 
		return oneRow;
	}
	public List<List<String>> setContent(List<StateIdJsonMapper> stateData ){
		
		List<List<String>> exportWrapData = new ArrayList<List<String>>();
	for(StateIdJsonMapper stateIdJsonMapper : stateData)
		{
		List<String> exportData =new ArrayList<String>();
		ArrayList<String> oneRow = new ArrayList<String>();
		oneRow.add(stateIdJsonMapper.getEndPoint());
		oneRow.add(stateIdJsonMapper.getStatus());
		List<StateId> stateId = stateIdJsonMapper.getStateId();
		for (StateId stateId2 : stateId) {
			oneRow.add(stateId2.getId());
			oneRow.add(stateId2.getName());
			oneRow.add(stateId2.getRunNum());
			if(null !=stateId2 && null !=stateId2.getDuration()){
			oneRow.add(stateId2.getDuration().toString());
			}
			else{
			oneRow.add("0.0");
			}
			oneRow.add(stateId2.getStateResult());
			oneRow.add(stateId2.getComment());
			oneRow.add(stateId2.getChanges());
		}		
		exportData.addAll(oneRow);
		exportWrapData.add(exportData);
		}	
	return exportWrapData;
	}
}
