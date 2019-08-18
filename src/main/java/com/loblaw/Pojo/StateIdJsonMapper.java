package com.loblaw.Pojo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StateIdJsonMapper {
	
	private List<StateId> stateId;
	private String returnCode;
	private String endPoint;	
	private String status;


	public StateIdJsonMapper(List<StateId> stateIds, String returnCode,
			String endPoint, String status) {
		
		this.stateId = stateId;
		this.returnCode = returnCode;
		this.endPoint = endPoint;
		this.status = status;
	}


	public StateIdJsonMapper(List<StateId> stateId, String endPoint) {
		
		this.stateId = stateId;
		this.endPoint = endPoint;
	}


	public StateIdJsonMapper(){		
	}	
	
	
	public StateIdJsonMapper(List<StateId> stateId) {
		
		this.stateId = stateId;
	}


	public StateIdJsonMapper(String endPoint) {		
		this.endPoint = endPoint;
	}
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public List<StateId> getStateId() {
		sortByRunNumber();
		return stateId;
	}
	
	public String sortByRunNumber() {
		   
		   Collections.sort(stateId, new Comparator<StateId>() {

			public int compare(StateId o1, StateId o2) {
						
				return o1.getRunNum().compareTo(o2.getRunNum());
						
			}
		   });
		return null;	
		}

	public void setStateId(List<StateId> stateId) {
		this.stateId = stateId;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
	
	

}
