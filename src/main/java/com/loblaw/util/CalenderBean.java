package com.loblaw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.view.ViewScoped;

import org.apache.commons.collections4.map.MultiValueMap;

import com.loblaw.PieChartBean;
@ManagedBean
public class CalenderBean {
	
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public String[] getSpecialDays() throws ParseException {	
		System.out.println("selectedDpTypeselectedDpType"+PieChartBean.selectedDp);
		
		MultiValueMap map = PieChartBean.mapOfNameAndDate;
		
		
			//System.out.println("selectedDpTypeselectedDpType"+selectedDpType);
			//System.out.println("map.get(selectedDpType >>>" + map.get(PieChartBean.selectedDp));
		    List<String> setOfDates = (List<String>) map.get(PieChartBean.selectedDp);
		
		
		if(null != setOfDates && setOfDates.size() !=0){
		//Set<String> setOfDates =  PieChartBean.setOfDates;		
		List<String> coll = new ArrayList<String>();
		
		for (String strDate : setOfDates) {
			Date date = sdf.parse(strDate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			coll.add(String.format("'%s'", sdf.format(cal.getTime())));			
		}
		String[] result = coll.toArray(new String[coll.size()]);
		
		  return result;
		
		}
		else{
			 String[] result = new String [1] ;
			  result[0] = "2019-03-21";
			  return result;
			
		}
		
	}
	
	 
}
