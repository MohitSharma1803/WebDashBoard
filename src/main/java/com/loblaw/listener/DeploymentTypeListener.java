package com.loblaw.listener;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import com.loblaw.PieChartBean;
import com.loblaw.util.CalenderBean;

public class DeploymentTypeListener implements ValueChangeListener{

	public void processValueChange(ValueChangeEvent event)
			throws AbortProcessingException {		
		PieChartBean.selectedDp= event.getNewValue().toString();
		
	}
	
}
