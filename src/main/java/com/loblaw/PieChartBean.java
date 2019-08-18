package com.loblaw;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;

import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.PieChartModel;

import com.loblaw.Pojo.StateId;
import com.loblaw.Pojo.StateIdJsonMapper;
import com.loblaw.util.AutomationConstants;
import com.loblaw.util.CommonDateUtil;


@ManagedBean
@ViewScoped
public class PieChartBean {
	static final Logger logger = Logger.getLogger(PieChartBean.class.getName());
	
    private PieChartModel model;    
    private String deploymentType;
    private Date deploymentDate;
    private String selectedDpTime;
    private int totalEndpoints;
    private int passed;
    private int failed;
    private int noResp;
    private String symantecToM;
    private String patch;
    public static String selectedDp;
    List<String> dropDownList = new ArrayList<String>();
    public static String filepath = new String();
    public static MultiValueMap<String, String> mapOfNameAndDate = new MultiValueMap<String, String>();
    public static MultiValueMap<String, String> mapOfNameDateAndTime = new MultiValueMap<String, String>();
    public static Set<String> setOfDates = new HashSet<String>();
    public static  Set<String> deploymentTimeList = new HashSet<String>();
    private List<Double> durationList = new ArrayList<Double>();    
    private String timeTakenToShow;
    
    
    
    
   public String getTimeTakenToShow() {
	   getMaximumDuration();
		return timeTakenToShow;
	}

	public void setTimeTakenToShow(String timeTakenToShow) {
		this.timeTakenToShow = timeTakenToShow;
	}

	public String getSelectedDpTime() {
		return selectedDpTime;
	}
	public void setSelectedDpTime(String selectedDpTime) {
		this.selectedDpTime = selectedDpTime;
	}
	public Set<String> getDeploymentTimeList() {
		return deploymentTimeList;
	}
	public void setDeploymentTimeList(Set<String> deploymentTimeList) {
		this.deploymentTimeList = deploymentTimeList;
	}
	
    private List<PieChartModel> pieChartModelList ;
    
    
    public void deploymentTypeListener(org.primefaces.event.SelectEvent event){
    	this.deploymentType=deploymentType;
		
    }
	public List<PieChartModel> getPieChartModelList() {
		return pieChartModelList;
	}

	
	public void setPieChartModelList(List<PieChartModel> pieChartModelList) {
		this.pieChartModelList = pieChartModelList;
	}	

	public List<String> getDropDownList() throws java.text.ParseException {
		dropDownList = listFilesForFolder(new File (AutomationConstants.WEB_MONITOR_FILE_DIRECTOR));
		Collections.sort(dropDownList, new SortIgnoreCase());	
		return dropDownList;
	}

	public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
	public void setDropDownList(List<String> dropDownList) {
		this.dropDownList =  dropDownList;
		
	}

	public int getNoResp() {
		return noResp;
	}

	public void setNoResp(int noResp) {
		this.noResp = noResp;
	}

	public String getSymantecToM() {
		return symantecToM;
	}

	public void setSymantecToM(String symantecToM) {
		this.symantecToM = symantecToM;
	}

	public String getPatch() {
		return patch;
	}

	public void setPatch(String patch) {
		this.patch = patch;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getTotalEndpoints() {
		return totalEndpoints;
	}

	public void setTotalEndpoints(int totalEndpoints) {
		this.totalEndpoints = totalEndpoints;
	}

	public Date getDeploymentDate() {
		return deploymentDate;
	}

	public void setDeploymentDate(Date deploymentDate) {
		this.deploymentDate = deploymentDate;
	}

	public String getDeploymentType() {
		return deploymentType;
	}

	public void setDeploymentType(String deploymentType) {
		this.deploymentType = deploymentType;
	}
	public static void main (String[] args){
		PieChartBean pie = new PieChartBean();
		//pie.init();
	}

	@PostConstruct
    public void init()  { 		
		
		logger.debug("Inside init method" );
    	List<StateIdJsonMapper> stateAttr = null;
    	logger.debug("The Path to read file " + filepath);
    	
			stateAttr = JSonFileReader.readJsonFile(filepath);
			if(null != stateAttr){		
    	totalEndpoints = stateAttr.size();
    	for (StateIdJsonMapper state : stateAttr) {
    		String s = new String();
    		if(state.getReturnCode() != null){
    			 s = state.getReturnCode();
    		}
			
    		if(s.equals("3")){
    			noResp++;
    		}
			
			if(s.equals("2")){				
				failed++;
			}	
			List<StateId> stateId = state.getStateId();
			Double sumOfDuration =0.0;			
			for (StateId stateId2 : stateId) {				
				if(null != stateId2.getDuration()){
					Double d = stateId2.getDuration();
					sumOfDuration += d;				   
				}
			}
			 durationList.add(sumOfDuration);
		}
	}
    	passed = totalEndpoints - failed - noResp;
    	
        model = new PieChartModel();
       
        model.set("Pass", passed);
        model.set("Fail", failed);
        model.set("No Response", noResp);

        //followings are some optional customizations:
        //set title
        model.setTitle(deploymentType);
        
        //set legend position to 'e' (east), other values are 'w', 's' and 'n'
        model.setLegendPosition("e");
        //enable tooltips
        model.setShowDatatip(true);
       
        //show labels inside pie chart
        model.setShowDataLabels(true);
        //show label text  as 'value' (numeric) , others are 'label', 'percent' (default). Only one can be used.
        model.setDataFormat("value");
        //format: %d for 'value', %s for 'label', %d%% for 'percent'
        //model.setDataLabelFormatString("%d");
        //pie sector colors
        model.setSeriesColors("00c081,FF0000");
        model.setExtender("skinPie");
    }
	
	private void getMaximumDuration(){		
		if(null != durationList){
			Double d = Collections.max(durationList);	
		
		  long millis =  d.longValue();
		   long days = TimeUnit.MILLISECONDS.toDays(millis);
	        millis -= TimeUnit.DAYS.toMillis(days);
	        long hours = TimeUnit.MILLISECONDS.toHours(millis);
	        millis -= TimeUnit.HOURS.toMillis(hours);
	      long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	     
	      if(Long.valueOf(minutes) ==0){
	    	    millis -= TimeUnit.MINUTES.toMillis(minutes);
	    	   long seconds   = TimeUnit.MILLISECONDS.toSeconds(millis);
	    	   if(Long.valueOf(seconds) ==0){
	    		   /*long miliseconds   = TimeUnit.MILLISECONDS.toMillis(seconds);*/
	    		   
	    		   timeTakenToShow = String.format("%.2f", d)+" Milliseconds";	    	   
	    	   }
	    	   else{
		       timeTakenToShow= seconds+" Seconds";
	    	   }
	      }
	     
	      else{	    	   
		        timeTakenToShow= minutes+" Minutes";
		      
	      }
		}    
	     
	   }
    
    public void showDataTable(ActionEvent ae) {    	
        RequestContext.getCurrentInstance().openDialog("endpointsDetails" ,getDialogOptions(), null);
    }

    public Map<String, Object> getDialogOptions() {
        HashMap options = new HashMap<Object, Object>();
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("modal", true);
        options.put("height", 600);        
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");        
        return options;
    }
    
    public static List listFilesForFolder(final File folder) throws java.text.ParseException {
		 Set<String> set = new HashSet<String>();
		 List<String> list = new ArrayList<String>();
		 CommonDateUtil commonDate = new CommonDateUtil();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	
	        	if(fileEntry.length() !=0){
	        	//logger.debug("File Entry Name " +fileEntry.getName());
	            String  s = fileEntry.getName();
	            int numberOfTokenizer = commonDate.numberOfTokenizerInFile(s);
	            if(numberOfTokenizer == 2 && s.contains(".")){
	            String [] strArr = s.split("_");
	            String dpType = strArr[0];
	            String dpDate = strArr[1];
	            String dpTime = strArr[2];
	            String  dpTimeArr =  dpTime.split("\\.")[0];
	            DateFormat dateFormat = new SimpleDateFormat("HHmmss");
		        Date d = dateFormat.parse(dpTimeArr);
		        String dpTimeToShow = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		       /* deploymentTimeList.add(dpTimeToShow);*/
		       // System.out.println("In List Of Folders Deployment Type and Date" + dpType+"_"+dpDate);
		        //System.out.println("In List Of Folders Deployment Time to Show " + dpTimeToShow);
		        mapOfNameDateAndTime.put(dpType+"_"+dpDate, dpTimeToShow);
	           setOfDates.add(CommonDateUtil.ConvertDate(dpDate));	 
	           mapOfNameAndDate.put("a", CommonDateUtil.ConvertDate(dpDate));
	           mapOfNameAndDate.put(dpType, CommonDateUtil.ConvertDate(dpDate));
	            set.add(dpType);
	            }
	        	}
	        }
	    }
	    list.addAll(set);
		return list;
	}
    
   
    
public String onSubmit() throws FileNotFoundException ,ValidatorException{	
	if(deploymentType == null || "".equalsIgnoreCase(deploymentType)){
		String msg = "Please select Deployment Type.."; 
		 throw new ValidatorException(new FacesMessage(msg));
		 
	}
	
	if((deploymentType != null || !"".equalsIgnoreCase(deploymentType)) && (deploymentDate !=null) && (selectedDpTime !=null)){
		logger.debug("Redirecting to Statistics Page");		
		filepath = readFile(deploymentType,CommonDateUtil.changeDateFormat(deploymentDate),formatDeploymentTime(selectedDpTime));	
		logger.debug("File Path >>>" + filepath);
		if(filepath == null){
			return "SearchCriteria?faces-redirect=true";
		}
		else{		
		return "statistics?faces-redirect=true" ;
		}
	}
	return "SearchCriteria?faces-redirect=true";
}

public String onClear() {
	setDeploymentType(null);
	setDeploymentDate(null);
	setSelectedDpTime(null);
	return "SearchCriteria?faces-redirect=true";
}

public void dateChange(SelectEvent event) 
{   
      Date date = (Date)event.getObject();
     
      logger.info("deploymentType" + selectedDp +"deploymentDate" + CommonDateUtil.changeDateFormat(deploymentDate));     
      String DpTyepAndDpDate = selectedDp+"_"+CommonDateUtil.changeDateFormat(deploymentDate);      
      List<String> listOfTime =  (List<String>)mapOfNameDateAndTime.get(DpTyepAndDpDate);
      Set<String> setOfTime = new HashSet<String>(listOfTime);      
      PieChartBean.deploymentTimeList = setOfTime;
     // System.out.println("Date >>>>>>>>>" +date.toString());
}


private  String readFile(String selectedDpType , String selectedDpDate, String selectedTime) throws FileNotFoundException{	
	
	String file = getFileToRead(selectedDpType+"_"+selectedDpDate+"_"+selectedTime);	
	logger.debug(file);
	return file;
	
}



private static String formatDeploymentTime(String selectedDpTime){
	String hours ="";
	String min ="";
	String sec = "";
	if(null !=selectedDpTime){
		String[] dpTimeArr = selectedDpTime.split(":");
		
		if(dpTimeArr[0].length() != 2){
			hours = "0"+dpTimeArr[0];
		}
		else{
			hours =dpTimeArr[0];
		}
		if(dpTimeArr[1].length() != 2){
			min = "0"+dpTimeArr[1];
		}
		else{
			min =dpTimeArr[1];
		}
		if(dpTimeArr[2].length() != 2){
			sec = "0"+dpTimeArr[2];
		}	
		else{
			sec =dpTimeArr[2];
		}
	}
	return hours+min+sec;
	
}




public  String getFileToRead(String filAndDate) throws FileNotFoundException {
	String str = AutomationConstants.WEB_MONITOR_FILE_DIRECTOR;
	 File folder = new File (str);
	 String filetoread =null;
	 //List<String> list = new ArrayList<String>();
    for (final File fileEntry : folder.listFiles()) {	       
           // System.out.println(fileEntry.getName());
        	String  s = fileEntry.getName();
        	String [] strArr = s.split("_");
            String dpType = strArr[0];            
            String dpDate = strArr[1];
            String dpTime = strArr[2];
            String  dpTimeArr =  dpTime.split("\\.")[0];
            String fileInDir = dpType+"_"+dpDate+"_"+dpTimeArr;
            logger.debug("File in Directory :" +fileInDir);
            logger.debug("File Name with Date :" +filAndDate);
            if(fileInDir.equals(filAndDate)){
            	 filetoread = str+AutomationConstants.SLASH+s;
            	
            }
            
        }
    
	return filetoread;
}
    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                        "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
   

    public PieChartModel getModel() {
        return model;
    }
}